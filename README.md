testingWebsocket
================

Simple test of HTML5 websockets

## What it is

Synchronizes the play/pause between different clients.
I'm testing the websockets (used in the javascript) communication with a simple server in Java.
So far it has a webserver in java, a Server listening for websockets, and ClientHandler to handle the sockets.

## How to run it 
* Simply go into the folder mainApp and use 'sbt run'
* In the browser (google chrome) got to http://localhost:8000/index.html

## TODO
* Implement handling of messages for websocket
* Send video.currentTime to client so that it can follow at same pace
* Clean up
