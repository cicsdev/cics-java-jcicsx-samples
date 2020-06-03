# Java Liberty Restapp Sample

This sample is the same as the existing JCICS [cics-java-liberty-restapp sample](https://github.com/cicsdev/cics-java-liberty-restapp/blob/master/src/Java/com/ibm/cicsdev/restapp/InfoResource.java), but updated to use JCICSX instead of JCICS to show the similarities and differences. 


Sample RESTful web application for deployment to a Liberty JVM server in CICS. The application is supplied with two resources:

1. `InfoResource` - This queries the JVM server environment using system properties and uses JAXB beans to return a JSON response detailing the CICS environment.
1. `ReverseResource` - This is similar to `InfoResource`, but uses the JCICSX API to link to the COBOL program `EDUCHAN` using channels and containers. An input string is passed to `EDUCHAN`, which is then reversed and returned, along with the time from CICS. 

The following Java source components are supplied in the [`src/main/java`](src/main/java) directory in this repository.

## Java package com.ibm.cicsdev.restapp
* [`CICSApplication`](src/main/java/com/ibm/cicsdev/restapp/CICSApplication.java) - Sets the `ApplicationPath` for resources in this application
* [`InfoResource`](src/main/java/com/ibm/cicsdev/restapp/InfoResource.java) - Returns JSON structure using `CICSInformation` bean
* [`ReverseResource`](src/main/java/com/ibm/cicsdev/restapp/ReverseResource.java) - Returns JSON structure using `ReverseResult` bean


## Java package com.ibm.cicsdev.restapp.bean
* [`CICSEnvironment`](src/main/java/com/ibm/cicsdev/restapp/bean/CICSEnvironment.java) - JAXB bean returning JSON structure containing information about CICS product and version
* [`CICSInformation`](src/main/java/com/ibm/cicsdev/restapp/bean/CICSInformation.java) - JAXB bean returning JSON structure containing CICS applid, time and JVM server name and instance of `CICSEnvironment`
* [`ReverseResult`](src/main/java/com/ibm/cicsdev/restapp/bean/ReverseResult.java) - JAXB bean returning JSON structure containg input and output containers sent to `EDUCHAN` COBOL program


## Supporting files
* [`EDUCHAN.cbl`](src/main/cobol/EDUCHAN.cbl) - A sample CICS COBOL that returns the date and time and reversed input using channels and containers.
Download and compile the supplied COBOL program EDUCHAN and deploy into CICS.


## Pre-reqs

* CICS TS V5.1 or later, due to the usage of the `getString()` methods.
* Java SE 1.7 or later on the z/OS system
* Java SE 1.7 or later on the workstation


## Setup

This application links to the COBOL program EDUCHAN, which can be found in `src/main/cobol`.  
Download and compile the supplied COBOL program EDUCHAN and deploy into CICS.

This sample is a Maven project, which uses the [CICS Bundle Maven plugin](https://github.com/IBM/cics-bundle-maven) to package the web application in a CICS bundle and deploy this to CICS. This requires the CICS bundle deployment API to be enabled in CICS as a [prerequisite](https://www.ibm.com/support/knowledgecenter/en/SSGMCP_5.6.0/configuring/cmci/config-bundle-api.html). Alternatively, if you aren't using Maven, you could take the source from this project and use one of the other methods of deploying the application to CICS such as creating a CICS bundle project in CICS Explorer and adding the source as a dynamic web project include. 

To run the sample as-is, fill out values in the configuration block in pom.xml
   ```xml
   <configuration>
     <defaultjvmserver>DFHWLP</defaultjvmserver>
     <url>http://yourcicsurl.com:9080</url>
     <username>${cics-user-id}</username>
     <password>${cics-password}</password>
     <bunddef>DEMOBUNDLE</bunddef>
     <csdgroup>BAR</csdgroup>
     <cicsplex>CICSEX56</cicsplex>
     <region>IYCWEMW2</region>
   </configuration>
   ```
Running `mvn clean install` will package the web application into a CICS Bundle and install and enable it. 
You can then view the web server `http://yourcicsurl.com:9080/cics-java-liberty-restapp-0.0.1-SNAPSHOT/`

#### Using a web browser you can issue the following HTTP GET requests

* http://host:port/cics-java-liberty-restapp-0.0.1-SNAPSHOT/cicsinfo

This will invoke the `InfoResource` class and return the following JSON response with information about the target CICS system:

`{"applid":"IYK2Z32E","jvmServer":"DFHWLP","time":"2016-09-09T16:19:55.384Z","cicsEnvironment":{"cicsProduct":"CICS Transaction Server for z/OS","cicsVersion":"5.3.0"}}`


* http://host:port/cics-java-liberty-restapp-0.0.1-SNAPSHOT/

This will invoke the `ReverseResource` class which links to the CICS COBOL program and reverses the default string "Hello from Java" returning the following JSON response:

`{"time":"2016-09-09T16:15:52.756Z","original":"Hello from Java","reverse":"avaJ morf olleH","truncated":false}`


* http://host:port/cics-java-liberty-restapp-0.0.1-SNAPSHOT/ilovecics

This will invoke the `ReverseResource` class which links to the CICS COBOL program reversing the input string "ilovecics" as follows:

`{"time":"2016-09-09T16:15:32.466Z","original":"ilovecics","reverse":"scicevoli","truncated":false}`
