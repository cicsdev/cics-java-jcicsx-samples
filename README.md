# JCICSX-Samples

This repository contains samples to show different scenarios of how you might use the JCICSX API. 
These will not work out of the box, as you will require valid CICS resources eg CICS Programs and Libraries for them to use. 
Each sample is contained within a web application which can be packaged as a WAR in a CICS bundle and deployed to CICS using the [CICS bundle Maven plugin](https://github.com/IBM/cics-bundle-maven) or the [CICS bundle Gradle plugin](https://github.com/IBM/cics-bundle-gradle). If you're just interested in the Java code, this can be found in *src/main/java*.

Full documentation on Java development using the JCICSX API can be found in the [IBM Knowledge Center](https://www.ibm.com/support/knowledgecenter/SSGMCP_5.6.0/applications/developing/java/jcicsx-api.html).


## Prerequisites

* CICS TS V5.6 or later
* Java SE 1.7 or later on the z/OS system
* Java SE 1.7 or later on the workstation



## [append-char-container-sample](append-char-container-sample)

This sample shows how you can create a channel. It then creates a container in that channel, which can contain CHAR data. Additional CHAR data is then appended to the container, and it then gets the combined data from the channel as a string output. 
  
## [bit-link-program-sample](bit-link-program-sample)

This sample walks through how to use the JCICSX API to perform a LINK to a CICS Program. During the LINK, it passes through a BIT container containing a temperature in Celcius, and then receives BIT data as the output from the CICS Program containing the temperature converted to Fahrenheit. 
  
## [char-link-program-sample](char-link-program-sample)

This sample takes a string and links to a COBOL program with a channel and input CHAR container. The COBOL program reverses the string and returns it to the Java program in an output CHAR container. 

## [cics-java-liberty-restapp](cics-java-liberty-restapp)

This sample mimics the existing JCICS [cics-java-liberty-restapp sample](https://github.com/cicsdev/cics-java-liberty-restapp/blob/master/src/Java/com/ibm/cicsdev/restapp/InfoResource.java), but is updated to use JCICSX instead of JCICS to show the similarities and differences. It creates a RESTful application that takes a string as an input and reverses it, and get the date and time from CICS. 

## [mockito-testing-containers-sample](mockito-testing-containers-sample)

This sample shows how to get data from BIT containers, and how to test the core logic of your application by using Mockito to mock the JCICSX calls. 


## Licence

This project is licensed under the Eclipse Public License, Version 2.0.
