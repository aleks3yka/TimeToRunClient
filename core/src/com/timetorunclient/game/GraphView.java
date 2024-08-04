package com.timetorunclient.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.timetorunclient.game.entities.Entity;
import com.timetorunclient.game.space.Edge;
import com.timetorunclient.game.space.Graph;
import com.timetorunclient.game.space.Vertex;
import com.timetorunclient.game.ui.Slider;
import com.timetorunclient.game.ui.Wheel;
import com.timetorunclient.game.utils.ArrayUtils;
import com.timetorunclient.game.utils.CosEasingFunction;
import com.timetorunclient.game.utils.SoftEasingFunction;
import com.timetorunclient.game.utils.Style;

import java.util.ArrayList;
import java.util.Comparator;

public class GraphView {
    private static final float FRAME_DURATION = 0.2f;
    Graph graph;
    float x, y;
    float maxRadius, edgeWidth, vertexWidth;
    VertexView[] vertices;
    EdgeView[] edgeBuffer;
    EntityView[] entityBuffer;
    Slider slider;
    Wheel wheel;
    float wheelPrevAngle;
    ArrayList<GraphElement> drawList;
    Comparator<GraphElement> comparator = (t1, t2) -> Math.round(Math.signum(
            Math.round(Math.signum(t1.priority - t2.priority))
            + Math.round(Math.signum(t1.z - t2.z)) * 2
    ));
    String vertexName;
    TextureAtlas gameAtlas;
    public GraphView(float maxRadius, float x, float y, float vertexWidth, float edgeWidth,
                     Graph graph,
                     String vertex, TextureAtlas gameAtlas,
                     Wheel wheel, Slider slider){
        this.graph = graph;
        this.maxRadius = maxRadius;
        this.edgeWidth = edgeWidth;
        this.vertexWidth = vertexWidth;
        this.x = x;
        this.y = y;
        this.wheel = wheel;
        this.slider = slider;
        this.vertexName = vertex;
        this.gameAtlas = gameAtlas;
        this.drawList = new ArrayList<>();
        this.edgeBuffer = new EdgeView[100];
        this.firstCircleVertices = new ArrayList<>();
        this.secondCircleVertices = new ArrayList<>();
        this.wheelPrevAngle = wheel.getAngel();
//        vertices = new VertexView[30];
//        for(int i = 0; i < vertices.length; i++){
//            vertices[i] = new VertexView(vertexWidth, vertex, this);
//        }
    }

    public void sync(Vertex[] vertices, Edge[] edges, Entity[] entities){
        this.vertices = new VertexView[vertices.length];
        for(int i = 0; i < this.vertices.length; i++){
            this.vertices[i] = new VertexView(vertexWidth,this, vertices[i]);
        }
        this.edgeBuffer = new EdgeView[edges.length];
        for(int i = 0; i < edges.length; i++){
            edgeBuffer[i] = new EdgeView(edgeWidth, edges[i], this);
        }
        this.entityBuffer = new EntityView[entities.length];
        for(int i = 0; i < entities.length; i++){
            entityBuffer[i] = new EntityView(vertexWidth, entities[i], this);
        }
    }

    public void update(float deltaTime){
        drawList.clear();

        //check if user using wheel
        if(wheel.isWheelTouched()){
            float delta = (wheel.getAngel() - wheelPrevAngle)*MathUtils.degreesToRadians;
            if(delta < -MathUtils.PI){
                delta += MathUtils.PI2;
            }else if (delta > MathUtils.PI){
                delta -= MathUtils.PI2;
            }
            this.wheelPrevAngle = wheel.getAngel();
            for(int i : firstCircleVertices){
                vertices[i].lock();
                vertices[i].rotateBy(delta, deltaTime);
            }
        }else{
            for(int i : firstCircleVertices){
                vertices[i].unlock();
                vertices[i].velocityTransition(firstCircleSpeed, 1);
            }
        }

        if(vertices != null) {
            for (VertexView i : vertices) {
                i.update(deltaTime);
                i.updateCoordinates(slider.getValue());
                if (i.onScreen) {
                    drawList.add(i);
                }
            }
        }

        if(edgeBuffer != null){
            for (int i = 0; i < edgeBuffer.length; i++) {
                edgeBuffer[i].update(deltaTime);
                if (edgeBuffer[i].onScreen) {
                    drawList.add(edgeBuffer[i]);
                }
            }
        }
        if(entityBuffer != null) {
            for (int i = 0; i < entityBuffer.length; i++) {
                entityBuffer[i].update(deltaTime);
                if (entityBuffer[i].onScreen) {
                    drawList.add(entityBuffer[i]);
                }
            }
        }

        drawList.sort(comparator);

    }

    public void draw(SpriteBatch batch){

        //draw;
        for(GraphElement i : drawList){
            i.draw(batch);
            //System.out.println(i.x + " " + i.y + " " + i.angle + " " + i.onScreen + " " + i.transiting);
        }
    }

    int centerVertex = -1;

    ArrayList<Integer> firstCircleVertices;
    float firstCircleSpeed;

    ArrayList<Integer> secondCircleVertices;

    public void lookAt(int vertex, float lookTime) {
        if(vertex == this.centerVertex){
            return;
        }
        boolean ok = true;
        for(int i = 0; i < vertices.length; i++){
            ok &= (graph.minimumEdges[vertex][i] != -1);
        }
        if(!ok){
            graph.findMinimumEdges(vertex);
        }
        ArrayList<Integer> firstCircleVertices = new ArrayList<>();
        ArrayList<Integer> secondCircleVertices = new ArrayList<>();
        for(int i = 0; i < vertices.length; i++){
            if(graph.minimumEdges[vertex][i] == 1){
                firstCircleVertices.add(i);
            } else if (graph.minimumEdges[vertex][i] == 2) {
                secondCircleVertices.add(i);
            }
        }

        float startAngle1 = MathUtils.PI*3/2 + (MathUtils.random()-0.5f), deltaAngle1 = MathUtils.PI2 / firstCircleVertices.size(),
                startAngle2 = 0 + (MathUtils.random()-0.5f), deltaAngle2 = MathUtils.PI2 / secondCircleVertices.size();
        float speed1 = 0, speed2 = Math.signum(MathUtils.random()-0.5f) * (MathUtils.random() / 2 + 0.5f);
        this.firstCircleSpeed = speed1;

        int it = ArrayUtils.interceptSorted(this.firstCircleVertices, firstCircleVertices);

        if(it != -1){
            startAngle1 = vertices[this.firstCircleVertices.get(it)].angle + it * deltaAngle1;
        }

        it = ArrayUtils.interceptSorted(this.secondCircleVertices, secondCircleVertices);

        if(it != -1){
            speed2 = vertices[this.secondCircleVertices.get(it)].velocity;
            startAngle2 = vertices[this.secondCircleVertices.get(it)].angle + it * deltaAngle2 + lookTime * speed2;
        }

        if(centerVertex != -1){
            vertices[centerVertex].alphaTransition(0, lookTime);
        }

        for(Integer i : this.firstCircleVertices){
            vertices[i].unlock();
            vertices[i].alphaTransition(0, lookTime);

        }
        for(Integer i : this.secondCircleVertices){
            vertices[i].alphaTransition(0, lookTime);
        }

        centerVertex = vertex;
        this.firstCircleVertices = firstCircleVertices;
        this.secondCircleVertices = secondCircleVertices;

        vertices[centerVertex].transition(vertices[centerVertex].angle
                + vertices[centerVertex].velocity * lookTime,
                vertices[centerVertex].velocity, 0, 1, lookTime);

        int j = 0;

        for(Integer i : this.firstCircleVertices){
            vertices[i].transition(startAngle1 - deltaAngle1*j, speed1,
                    maxRadius/2, 1, lookTime);
            j++;
        }

        j = 0;

        for(Integer i : this.secondCircleVertices){
            vertices[i].transition(startAngle2 - deltaAngle2*j, speed2,
                    maxRadius, 1, lookTime);
            j++;
        }
    }

    public int getSelectedVertex(){
        if(this.firstCircleVertices == null || this.firstCircleVertices.isEmpty()){
            return -1;
        }
        int best = firstCircleVertices.get(0);
        for(int i = 1; i < this.firstCircleVertices.size(); i++){
            int now = this.firstCircleVertices.get(i);
            if(vertices[best].y > vertices[now].y
                    || (vertices[best].y == vertices[now].y && vertices[now].z > vertices[best].z)){
                best = now;
            }
        }
        return best;
    }



    class GraphElement{
        float z;
        int priority;
        void draw(SpriteBatch batch){}
    }
    class EdgeView extends GraphElement{
        Sprite sprite;
        Style style;
        float height;
        float alpha;
        boolean onScreen, deleted;
        VertexView a, b;
        Edge representedEdge;
        EdgeView(float thickness, Edge representedEdge, GraphView graph){
            this.style = new Style(representedEdge.spriteName, graph.gameAtlas, FRAME_DURATION);
            this.sprite = style.getOutput();
            this.height = thickness;
            this.priority = 1;
            this.onScreen = false;
            this.representedEdge = representedEdge;
            setVertices(graph.vertices[representedEdge.getA()], graph.vertices[representedEdge.getB()]);
        }

        private void edgeChangeHandler(){
            if(onScreen);
        }

        private void setVertices(VertexView a, VertexView b){
            this.a = a;
            this.b = b;
        }
        void update(float deltaTime){
            if(a == null || b == null){
                return;
            }
            this.z = Math.min(a.z, b.z);
            this.alpha = Math.min(a.alpha, b.alpha);
            onScreen = (this.alpha != 0);
            if(!onScreen){
                return;
            }
            style.setAnimation(representedEdge.getSpriteIndex());
            style.update(deltaTime);
            Vector2 vector = new Vector2(b.x, b.y);
            vector.sub(a.x, a.y);
            sprite.setRegionWidth(Math.min(sprite.getRegionWidth(), Math.round(vector.len() * sprite.getRegionHeight() / height)));
            sprite.setSize(vector.len(), height);
            sprite.setOrigin(0, height/2);
            sprite.setOriginBasedPosition(a.x, a.y);
            sprite.setRotation(vector.angleDeg());
        }

        @Override
        void draw(SpriteBatch batch) {
            sprite.draw(batch, alpha);
        }
    }
    class EntityView extends GraphElement{
        Sprite sprite;
        Style style;
        Entity representedEntity;
        float width;
        float alpha;
        boolean onScreen;
        GraphView graph;
        EntityView(float width, Entity representedEntity, GraphView graph){
            this.style = new Style(representedEntity.spriteName, gameAtlas, FRAME_DURATION);
            this.sprite = this.style.getOutput();
            this.width = width;
            this.priority = 3;
            this.onScreen = false;
            this.graph = graph;
            this.representedEntity = representedEntity;
        }
        void update(float deltaTime){
            this.style.setAnimation(representedEntity.getSpriteIndex());
            this.style.update(deltaTime);
            this.sprite.setSize(width,
                    this.sprite.getRegionHeight() * width / this.sprite.getRegionWidth());
            this.sprite.setOrigin(width/2, 0);
            VertexView a = graph.vertices[representedEntity.getVertex()];
            if(representedEntity.getBeginMoving() == -1){
                this.z = a.z;
                this.alpha = a.alpha;
                onScreen = (this.alpha != 0);
                if(!onScreen){
                    return;
                }
                sprite.setFlip(false, false);
                sprite.setOriginBasedPosition(a.x, a.y);

            }else{
                VertexView b = graph.vertices[representedEntity.getDestination()];
                this.z = Math.max(a.z, b.z);
                this.alpha = Math.min(a.alpha, b.alpha);
                onScreen = (this.alpha != 0);
                if(!onScreen){
                    return;
                }
                Vector2 vector = new Vector2(b.x, b.y);
                vector.sub(a.x, a.y);
                Vector2 pos = new Vector2(a.x, a.y);
                float parameter = representedEntity.getPos();
                pos.mulAdd(vector, parameter);
                sprite.setOriginBasedPosition(pos.x, pos.y);
                if(vector.x < 0){
                    sprite.setFlip(true, false);
                }else{
                    sprite.setFlip(false, false);
                }
            }
        }

        @Override
        void draw(SpriteBatch batch) {
            sprite.draw(batch, alpha);
        }
    }
    class VertexView extends GraphElement{
        /*
        * Steps to use:
        * 1. update
        * 2. updateCoordinates
        * 3. Draw
        * profit!
        * */
        private static final float MEAN_COEFFICIENT = 0.9f;
        GraphView graph;
        Style style;
        Sprite sprite;
        float width, height;
        float x, y;
        float angle, velocity, radius;
        float newAngle, newVelocity, newRadius;
        boolean transiting;
        float transitionTime, timePassed;
        SoftEasingFunction angleTransition, radiusTransition, alphaTransition;
        CosEasingFunction velocityTransition;
        boolean onScreen;
        boolean free;
        float alpha;
        float newAlpha;
        Vertex representedVertex;

        VertexView(float width, GraphView graph, Vertex representedVertex){
            this.onScreen = false;
            this.alpha = 0;
            this.style = new Style(representedVertex.spriteName, gameAtlas, FRAME_DURATION);
            this.sprite = this.style.getOutput();
            this.width = width;
            this.graph = graph;
            this.transiting = false;
            this.angle = 0;
            this.velocity = 0;
            this.priority = 2;
            this.free = true;
            this.representedVertex = representedVertex;
        }

        void update(float deltaTime){
            this.style.setAnimation(representedVertex.getSpriteIndex());
            this.style.update(deltaTime);
            this.height = width * sprite.getRegionHeight()/sprite.getRegionWidth();
            this.sprite.setOriginCenter();
            this.sprite.setSize(this.width, this.height);
            if(transiting){
                timePassed += deltaTime;
                if(timePassed >= transitionTime){
                    if(velocityTransition != null){
                        velocity = newVelocity;
                        velocityTransition = null;
                    }
                    if(angleTransition != null){
                        angle = newAngle;
                        velocity = newVelocity;
                        angleTransition = null;
                        deltaTime = (timePassed-transitionTime);
                    }
                    if(radiusTransition != null){
                        radius = newRadius;
                        radiusTransition = null;
                    }
                    if(alphaTransition != null){
                        alphaTransition = null;
                        alpha = newAlpha;
                    }
                    transiting = false;
                } else {
                    if(velocityTransition != null){
                        velocity = velocityTransition.getValue(timePassed);
                    }
                    if (angleTransition != null) {
                        angle = angleTransition.getValue(timePassed);
                        velocity = angleTransition.getSpeed(timePassed);
                    }
                    if (radiusTransition != null) {
                        radius = Math.max(Math.min(radiusTransition.getValue(timePassed), maxRadius), 0);
                    }
                    if (alphaTransition != null) {
                        alpha = Math.max(Math.min(alphaTransition.getValue(timePassed), 1), 0);
                    }
                }
            }
            if(angleTransition == null && this.free){
                angle += deltaTime * velocity;
            }
            onScreen = !(alpha == 0.0f);
            if(angle >= MathUtils.PI2){
                angle -= MathUtils.PI2;
            }else if(angle < 0){
                angle += MathUtils.PI2;
            }
        }

        void rotateBy(float angle, float dt){
            if(this.free){
                return;
            }
            this.angle += angle;
            this.velocity = velocity * MEAN_COEFFICIENT + angle / dt * (1-MEAN_COEFFICIENT);
        }

        void lock(){
            if(this.angleTransition != null){
                return;
            }
            if(velocityTransition != null){
                velocityTransition = null;
                transiting = false;
            }
            this.free = false;
        }

        void unlock(){
            this.free = true;
        }
        
        //parameter 0 -- circles
        //parameter 1 -- triangle
        void updateCoordinates(float parameter){
            if(!onScreen){
                return;
            }
            parameter = MathUtils.sin(parameter*MathUtils.PI/2);
            this.x = this.graph.x + MathUtils.cos(angle) * radius;
            this.y = this.graph.y
                    + MathUtils.sin(angle) * radius * (1 - parameter)
                    + parameter * (this.graph.maxRadius - radius * 2);
            this.z = -MathUtils.sin(angle) * radius * parameter
                    + (1 - parameter) * (this.graph.maxRadius - radius * 2);
            sprite.setOriginBasedPosition(this.x, this.y);
        }

        void transition(float newAngle, float newVelocity,
                        float newRadius,
                        float newAlpha,
                        float time){
            if(!this.free){
                return;
            }
            velocityTransition = null;

            if(newAngle - angle < -MathUtils.PI)
                newAngle += MathUtils.PI2;
            else if(newAngle - angle > MathUtils.PI)
                newAngle -= MathUtils.PI2;
            if(this.angleTransition != null) this.angleTransition =
                    new SoftEasingFunction(timePassed, this.angleTransition, newAngle, newVelocity, time);
            else this.angleTransition = new SoftEasingFunction(angle, newAngle, velocity, newVelocity, time);
            this.newAngle = newAngle;
            this.newVelocity = newVelocity;

            if(this.radiusTransition != null) this.radiusTransition
                    = new SoftEasingFunction(timePassed, this.radiusTransition, newRadius, time);
            else this.radiusTransition = new SoftEasingFunction(radius, newRadius, time);
            this.newRadius = newRadius;

            alphaTransition(newAlpha, time);
            this.transitionTime = time;
            this.timePassed = 0;
            this.transiting = true;
        }
        void alphaTransition(float newAlpha, float time){

            if(this.alphaTransition != null)
                this.alphaTransition = new SoftEasingFunction(this.timePassed, this.alphaTransition,
                        newAlpha, time);
            else this.alphaTransition = new SoftEasingFunction(alpha, newAlpha, time);
            this.newAlpha = newAlpha;
            this.transitionTime = time;
            this.timePassed = 0;
            this.transiting = true;
        }

        void velocityTransition(float v2, float time){
            if(v2 == velocity || transiting){
                return;
            }
            velocityTransition = new CosEasingFunction(velocity, v2, time);
            transiting = true;
            transitionTime = time;
            timePassed = 0;
        }

        @Override
        void draw(SpriteBatch batch){
            if(!onScreen){
                return;
            }
            //System.out.println(x + " " +  y);
            sprite.draw(batch, this.alpha);
        }


    }
}
