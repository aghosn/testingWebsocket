testingWebsocket
================

Simple test of HTML5 websockets

## What it is

Synchronizes the play/pause between different clients.
I'm testing the websockets (used in the javascript) communication with a simple server in Java.
So far it has a webserver in java, a Server listening for websockets, and ClientHandler to handle the sockets.

## How to run it 
* Simply go into the folder mainApp and use 'sbt run'
* If you select Server, you have to open the file in mainApp/resources/views/index.html
* If you select MyHttpServer, in the browser (google chrome) go to http://localhost:8000/index.html

## Issues
* HttpServer does not support well video streaming. Hence, the video.currentTime doesn't work. That means that the play/pause button simply stops and pause the video on all clients, but does not reset the currentTime.
* The above problem can be ignored by running only the server, and instead opening the index.html file with a web browser. 
## TODO

