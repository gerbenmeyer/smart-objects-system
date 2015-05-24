# System structure #
This page contains an overview of the most important classes used by the Smart Objects System software and an explanation of what they do. The following image shows the class diagram of the system, divided in parts.

![http://smart-objects-system.googlecode.com/svn/wiki/systemstructure.png](http://smart-objects-system.googlecode.com/svn/wiki/systemstructure.png)

## System Core ##

This part of the system holds the core functionality and infrastructure.

### SOSServer ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?main/SOSServer.html'>SOSGeneric.main.SOSServer</a></i></font>

This abstract class is the starting point of the system. It reads the settings, starts the HTTP and XML listeners, adds the default agents and prepares several collection and storage objects which are used in the system. Also, an AgentsProcessor is created, which manages the execution of agents. Any implementing project has to extend this class, which has to be run in order to start the system.

### Settings ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?main/Settings.html'>SOSGeneric.main.Settings</a></i></font>

A proxy class which reads the settings from an .ini file through the use of a Properties object. Predefined setting names have been defined in this class as static strings.

### HTMLConnection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/HTTPListener.html'>SOSGeneric.util.clientconnection.HTTPListener</a></i></font>

<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/htmltool/HtmlGenerator.html'>SOSGeneric.util.htmltool.HtmlGenerator</a></i></font>

Actually consists of two classes, HTTPListener and HtmlGenerator.
HTTPListener listens for HTTP connections at a certain port. It handles requests and finds the right agents, instructing them to generate a HTML page through a HtmlGenerator object. This object provides several methods for adding elements to the HTML UI. Child implementations also include generation of JavaScript code for HTML pages.

### XMLConnection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/XMLListener.html'>SOSGeneric.util.clientconnection.XMLListener</a></i></font>

<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/XMLClientConnection.html'>SOSGeneric.util.clientconnection.XMLClientConnection</a></i></font>

Actually consists of two classes, XMLListener and XMLClientConnection.
XMLListener listens on a port for connections, creating a XMLClientConnection. XMLClientConnection handles the reception of commands from a client (which are serialized [XMLServerCommands](http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/XMLServerCommand.html)) and translates them to a server action, which might be send back to the client.

## Data Representation ##

These structures represent data within the system.

### Agent ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/Agent.html'>SOSGeneric.model.agent.Agent</a></i></font>

An abstract class which acts as a base for all agents. An agent is constructed through its properties, for which the Agent class provides get and set methods. Different access levels are available through the use of interfaces and some common property names are defined as statics strings in the class. Methods for learning, execution and garbage collection are defined in this class. Additionally, generate methods are available which control the generation of the web interface. An implementing project will have to extend the Agent class to provide its application specific behaviour and interface generation.

### Property ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/property/Property.html'>SOSGeneric.model.agent.property.Property</a></i></font>

A Property is a basic data structure which defines an agent. A number of property types have been defined, see the util.enums.PropertyType enum or model.agent.property.properties package for details.

### AgentCollection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/collection/AgentCollection.html'>SOSGeneric.model.agent.collection.AgentCollection</a></i></font>

An instance of this class is used to retrieve agents from a storage. It implements two interfaces (one through the other) to provide different levels of access.

### LocationCollection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/locations/LocationCollection.html'>SOSGeneric.model.locations.LocationCollection</a></i></font>

A collection of locations, uses a LocatonCollectionStorage to cache already known addresses and their geographical locations. It also has a remote counterpart, to be used in client applications.

### Classifier ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/classification/Classifier.html'>SOSGeneric.model.agent.classification.Classifier</a></i></font>

The Classifier object is responsible for the learning part of the application. It uses a storage to save the behaviour of an Agent type, based on its status and several Properties.

## Remote Data Representation ##

These structures allow for information exchange between a client and server.

### RemoteAgentCollection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/RemoteAgentCollection.html'>SOSGeneric.util.clientconnection.RemoteAgentCollection</a></i></font>

An extension of AgentCollection which uses XML communication with a running server to retrieve Agents. This class is used in client applications.

### RemoteLocationCollection ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?util/clientconnection/RemoteLocationCollection.html'>SOSGeneric.util.clientconnection.RemoteLocationCollection</a></i></font>

A collection allowing clients to retrieve locations from the server, via an XML communication connection.

## Data Storage ##

This part of the system manages the storage of the before mentioned data structures. All default implementations utilize a MySQL database.

### AgentStorage ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?data/agents/AgentStorage.html'>SOSGeneric.data.agents.AgentStorage</a></i></font>

An implementation of this class provides the storage of individual agents and provides methods to manage and retrieve properties of those agents.

### AgentCollectionStorage ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?data/agents/AgentCollectionStorage.html'>SOSGeneric.data.agents.AgentCollectionStorage</a></i></font>

An implementation of this class provides the storage of a collection of agents. This inculdes some basic, much used properties of the agent.

### LocationCollectionStorage ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?data/locations/LocationCollectionStorage.html'>SOSGeneric.data.locations.LocationCollectionStorage</a></i></font>

An implementation of this abstract class provides a data storage for locations. This means a cache of known addersses can be build, so location lookup is fast.

### ClassifierCollectionStorage ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?data/classification/ClassifierCollectionStorage.html'>SOSGeneric.data.classification.ClassifierCollectionStorage</a></i></font>

A collection storing all learned Agent instances.

## Data Interfaces ##

These interfaces are defined to provide two levels of access to the data structures: The Viewable interface provides reading ('getters') methods, while the Mutable interface allows mutation ('setters').

### AgentInterface ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/AgentViewable.html'>SOSGeneric.model.model.agent.AgentViewable</a></i></font>

<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/AgentMutable.html'>SOSGeneric.model.agent.AgentMutable</a></i></font>

Actually two interfaces that must be impelmented by a class to act as an Agent.

### AgentCollectionInterface ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/collection/AgentCollectionViewable.html'>SOSGeneric.model.agent.collection.AgentCollectionViewable</a></i></font>

<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/agent/collection/AgentCollectionMutable.html'>SOSGeneric.model.agent.collection.AgentCollectionMutable</a></i></font>

An interface for AgentCollections. It defines methods for agent addition and retrieval. Actually consists of two interface, a Viewable and Mutable interface.

### LocationCollectionInterface ###
<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/locations/LocationCollectionViewable.html'>SOSGeneric.model.locations.LocationCollectionViewable</a></i></font>

<font size='1'><i><a href='http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/index.html?model/locations/LocationCollectionMutable.html'>SOSGeneric.model.locations.LocationCollectionMutable</a></i></font>

An interface that must be implemented by a LocationCollection. It defines several methods for location retrieval and lookup. Actually consists of two interface, a Viewable and Mutable interface.