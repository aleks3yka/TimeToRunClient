package com.timetorunclient.game.connection;


import com.timetorunclient.game.GraphView;
import com.timetorunclient.game.entities.Entity;
import com.timetorunclient.game.space.Edge;
import com.timetorunclient.game.space.Graph;
import com.timetorunclient.game.space.Vertex;

public class Connection {
    Graph graph;
    boolean giveMapToGraph;
    public Connection (){
        this.giveMapToGraph = false;
    }
    public void setGraph(Graph graph){
        this.graph = graph;
    }
    public void update(){
        if(giveMapToGraph || graph == null){
            return;
        }

        Entity[] entities = new Entity[1];
        entities[0] = new Entity(0, 1,"playerIdling", "playerMoving");
        Edge[] edges = new Edge[10];
        Vertex[] vertices = new Vertex[8];
        vertices[0] = new Vertex("vertex");
        vertices[1] = new Vertex("vertex");
        vertices[2] = new Vertex("vertex");
        vertices[3] = new Vertex("vertex");
        vertices[4] = new Vertex("vertex");
        vertices[5] = new Vertex("playerIdling");
        vertices[6] = new Vertex("vertex");
        vertices[7] = new Vertex("vertex");

        edges[0] = new Edge(0, 1, 1, true, "edge");
        edges[1] = new Edge(0, 2, 1, true, "edge");
        edges[2] = new Edge(2, 3, 1, true, "edge");
        edges[3] = new Edge(1, 3, 1, true, "edge");
        edges[4] = new Edge(3, 4, 1, true, "edge");
        edges[5] = new Edge(4, 5, 1, true, "edge");
        edges[6] = new Edge(4, 6, 1, true, "edge");
        edges[7] = new Edge(5, 6, 1, true, "playerMoving");
        edges[8] = new Edge(6, 7, 1, true, "edge");
        edges[9] = new Edge(5, 7, 1, true, "edge");
        graph.setGraph(vertices, edges, entities, entities[0]);
        giveMapToGraph = true;
    }
}
