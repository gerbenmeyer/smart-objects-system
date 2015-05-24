# Settings #

This page gives an overview of the different settings available in the configuration file of a server application with an explanation how to use them.
Most of the settings described here have a static string in the [Settings](http://smart-objects-system.googlecode.com/svn/trunk/SOSGeneric/doc/main/Settings.html) class, so they can be referred to easily.

## Application ##

These settings are mainly used to display information in the header of the interface.

**_application\_name_** = (string)

The name of the application.

**_application\_name\_abbreviation_** = (string)

The abbreviation of the application name.

**_application\_version_** = (string)

The version of the application

**_application\_icon_** = (string)

The icon of the application, placed in the directory set at the following _html\_data\_dir_ setting.

## Directory locations ##

**_html\_data\_dir_** = (string)

The directory where resources for the HTML interface can be found, like images and icons. May be relative or absolute.

## Server settings ##

**_xml\_port_** = (int)

The port at which the server should listen for client connections, which use XML to exchange information.

**_xml\_server\_password_** = (string)

The password to be provided by a client to establish a connection.

**_http\_port_** = (int)

The port at which the server should listen for HTTP connections, which is used as the user interface.

**_http\_server\_address_** = (string)

The address of the server, used when creating external links.

**_google\_maps\_v2\_api\_key_** = (string)

SOS uses the Google Maps v3 API by default to look up addresses. When this becomes unavailable for some reason (time-out, or limit of requests reached), it falls back to the v2 API, which requires an access key.

## Agent settings ##

**_agent\_execution\_priority_** = (low/normal/high)

Set the priority of the agent execution. A higher priority will increase the CPU load.

**_pause\_agent\_execution\_when\_putting\_agents_** = (boolean)

If set to true, agents will be paused from execution when agents are added or modified.

**_agent\_problem\_detection\_enabled_** = (boolean)

Set to true to enable problem detection.

**_agent\_problem\_learning\_enabled_** = (boolean)

Set to true to enable agent learning.

## Database setup ##

**_database\_host_** = (string)

The host name of database server.

**_database\_user_** = (string)

The database user name.

**_database\_password_** = (string)

The database password associated with the database user name.

**_database\_name_** = (string)

The name of the database.

## HTML interface ##

**_show\_all\_objects_** = (boolean)

Set to true if the 'all' menu item should be shown.

**_show\_overview\_lists_** = (boolean)

Set to true if the details pane should list overviews.

_**show\_small\_details\_pane** = (boolean)_

Set to true if the details pane should be smaller than default.

**_default\_agent_** = (string)

The default agent which will generate the frontpage.

**_default\_clustering_** = (boolean)

Set to true to enable clustering of map markers by default.

**_keyword\_deeplink_** = (string)

The keyword used to create a deep link to agents.

## Notification settings ##

**_notification\_email\_enabled_** = (boolean)

Set to true if the SOS server should send emails when agents change their status.

**_notification\_email\_threshold_** = (int)

A number in minutes, indicating the miminum amount of minutes between two notification emails.

**_notification\_email\_status\_threshold_** = (int)

A number indicating the status threshold when sending notification emails, i.e. -3 means that only agents in the error status are included, -2 means that both agents in the error and warning status are included, etc.

**_notification\_email\_recipient_** = (string)

The email recipient of the notification email. Multiple email addresses can be provided by seperation with a comma.

**_notification\_email\_content_** = (string)

The directory where resources for the HTML content of the notification email can be found, like images and icons. Must be absolute.

**_notification\_email\_allowed\_types_** = (string)

The agent types which are included in the notification email. Multiple types can be provided by seperation with a comma.

## SMTP settings ##

**_smtp\_email\_address_** = (string)

The email address used for sending notification emails.

**_smtp\_server_** = (string)

The SMTP server used for sending notification emails.

**_smtp\_username_** = (string)

The username used for sending emails through the SMTP server.

**_smtp\_password_** = (string)

The password used for sending emails through the SMTP server.