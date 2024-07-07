java -jar bundletool.jar build-apks --bundle=.\android\release\android-release.aab ^
--output=.\android\release\android-universal.apks --overwrite --mode=universal ^
--ks=.\keyStorage\myKey.jks ^
--ks-pass=file:.\keyStorage\keystoragepass.txt ^
--ks-key-alias=key0 ^
--key-pass=file:.\keyStorage\aliaspass.txt