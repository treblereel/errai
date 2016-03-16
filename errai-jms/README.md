
This is work in it's early stages.
==================================

The goal of this project is to provide a useful subset of JMS on the client that works in GWT.
If you are looking for examples, take a look at the jms-dem-sample.

That it is:
==========

MDB implementation for Errai Framwork.



What is already done:
=====================

* Subscribe to Queues
* Subscribe to Topics
* Module will try to redelivery message to queue subscriber 10 times if sunscriber goes offline, on fail, other subscriber will receive that message   
* Simple round-robin support for queues, because we generate only one MDB on servers side for any number of clients
* Message types:
	- Object
	- Text
	- Byte
	- Map
	- Stream -- not support

TODO:
====
 
* JMSContext -- @Resource injection
* send messages to Topics and Queueus


Command line execution
======================


Build the module using: 
	mvn clean install
To run tests:
	mvn clean verify -Pintegration-test
	
Have fun,
The Errai Team
