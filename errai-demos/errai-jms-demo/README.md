Errai::JMS::Demos, Using an MDB (Message-Driven Bean)
============================================================
Author: Dmitrii Tikhomirov 
Level: Intermediate
Technologies: JMS, EJB, MDB  
Summary: The `helloworld-mdb` quickstart uses *JMS* and *EJB Message-Driven Bean* (MDB) to create and deploy JMS topic and queue resources in WildFly.  
Target Product: WildFly  
Source: <https://github.com/treblereel/errai/>  

What is it?
-----------

The `errai-jms-demo` quickstart demonstrates the use of *JMS* and *EJB Message-Driven Bean* with Errai Framework

This project creates two JMS resources:

* A queue named `HELLOWORLDMDBQueue` bound in JNDI as `java:/queue/HELLOWORLDMDBQueue`
* A topic named `HELLOWORLDMDBTopic` bound in JNDI as `java:/topic/HELLOWORLDMDBTopic`


System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7 or later. 

Use of WILDFLY_HOME
---------------

In the following instructions, replace `WILDFLY_HOME` with the actual path to your WildFly installation. The installation path is described in detail here: [Use of WILDFLY_HOME and JBOSS_HOME Variables](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_OF_EAP7_HOME.md#use-of-eap_home-and-jboss_home-variables).


Start the WildFly Server with the Full Profile
---------------

1. Open a command prompt and navigate to the root of the WildFly directory.
2. The following shows the command line to start the server with the full profile:

        For Linux:   WILDFLY_HOME/bin/standalone.sh -c standalone-full.xml
        For Windows: WILDFLY_HOME\bin\standalone.bat -c standalone-full.xml


Build and Deploy the Quickstart
-------------------------

1. Make sure you have started the WildFly server as described above.
2. Open a command prompt and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean install wildfly:deploy

4. This will deploy `target/errai-jms-demo-4.0.0-SNAPSHOT.war` to the running instance of the server. Look at the WildFly console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations:

        ...
        INFO  [org.wildfly.extension.messaging-activemq] (MSC service thread 1-4) WFLYMSGAMQ0002: Bound messaging object to jndi name java:/queue/HELLOWORLDMDBQueue
        INFO  [org.wildfly.extension.messaging-activemq] (MSC service thread 1-2) WFLYMSGAMQ0002: Bound messaging object to jndi name java:/topic/HELLOWORLDMDBTopic
        ....
        INFO  [org.apache.activemq.artemis.core.server] (ServerService Thread Pool -- 67) AMQ221003: trying to deploy queue jms.queue.HelloWorldMDBQueue
        ...
        INFO  [org.apache.activemq.artemis.core.server] (ServerService Thread Pool -- 12) AMQ221003: trying to deploy queue jms.topic.HelloWorldMDBTopic
        INFO  [org.jboss.as.ejb3] (MSC service thread 1-7) WFLYEJB0042: Started message driven bean 'HelloWorldQueueMDB' with 'activemq-ra.rar' resource adapter
        INFO  [org.jboss.as.ejb3] (MSC service thread 1-6) WFLYEJB0042: Started message driven bean 'HelloWorldQTopicMDB' with 'activemq-ra.rar' resource adapter
        

Access the application 
---------------------

The application will be running at the following URL: <http://localhost:8080/errai-jms-demo-4.0.0-SNAPSHOT> and ready to receive messages.

Investigate the Server Console Output
-------------------------

Look at the WildFly console or Server log and you should see log messages like the following:

[org.jboss.as.ejb3] (MSC service thread 1-8) WFLYEJB0042: Started message driven bean 'HelloWorldTopicMDB' with 'activemq-ra.rar' resource adapter

[org.jboss.as.ejb3] (MSC service thread 1-1) WFLYEJB0042: Started message driven bean 'HelloWorldQueueMDB' with 'activemq-ra.rar' resource adapter



Undeploy the Archive
--------------------

1. Make sure you have started the WildFly server as described above.
2. Open a command prompt and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy


Debug the Application
------------------------------------

If you want to debug the source code of any library in the project, run the following command to pull the source into your local repository. The IDE should then detect it.

    mvn dependency:sources
   
