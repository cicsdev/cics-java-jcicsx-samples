# JCICSX-Samples

This repository contains samples to show different scenarios of how you might use the JCICSX API. 
These will not work out of the box, as you will require valid CICS resources eg CICS Programs for them to use. 

## append-char-container-sample

This sample shows how you can create a channel. It then creates a container in that channel, which can contain CHAR data. Additional CHAR data is then appended to the container, and it then gets the combined data from the channel as a string output. 
  
## bit-link-program-sample

This sample walks through how to use the JCICSX API to perform a LINK to a CICS Program. During the LINK, it passes through a CHAR container, and then receives BIT data as the output from the CICS Program. 
  
## char-link-program-sample

This sample is similar to the above, but shows the data being returned in a CHAR container, and therefore recieves a string as the output of the linked CICS Program. 
