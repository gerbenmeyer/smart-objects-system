# System behaviour #
This page explains the behaviour of SOS in three different cases. These cases are: a data client adding a new object, a user requesting the web interface, and a user training an agent. Each will be discussed next.

## Data client adding a new object ##

The figure below shows a UML communication diagram, indicating the different steps and function calls between classes required when a data client is adding a new data object.

![http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour1.png](http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour1.png)

## User requesting the web interface ##

Requesting the web interface consists of two seperate HTTP-requests, one for the content to be put on map, and one for content of the details pane. The figure below shows two UML communication diagrams, indicating the different steps and function calls between classes required for these two HTTP-requests.

![http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour2.png](http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour2.png)

## User training an agent ##

Training an agent through the web interface is in principle a HTTP-request as well. The figure below shows a UML communication diagram, indicating the different steps and function calls between classes required for the HTTP-request when training an agent.

![http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour3.png](http://smart-objects-system.googlecode.com/svn/wiki/systembehaviour3.png)