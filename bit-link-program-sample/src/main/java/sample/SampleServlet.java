package sample;

/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2020 All Rights Reserved                       */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

import java.nio.ByteBuffer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

/**
 * A sample servlet to demonstrate how to use JCICSX to LINK to a CICS Program with BIT data
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SampleServlet {
	
    /**
     * Name of the program to invoke.
     */
    private static final String PROG_NAME = "CONVERT";
    
    /**
     * Name of the channel to use.
     */
    private static final String CHANNEL = "MYCHANNEL";
    
    /**
     * Name of the container used to send data to the target program.
     */
    private static final String INPUT_CONTAINER = "INPUTDATA";
    
    /**
     * Name of the container which will contain the response from the target program.
     */
    private static final String OUTPUT_CONTAINER = "OUTPUTDATA";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @GET
    @Path("/{text}")
    public String convertCelciusToFahrenheit(@PathParam("text") String temperatureString) {
    	
    	Integer temperature = null;
    	
    	try {
    		temperature = Integer.valueOf(temperatureString);
		} catch (NumberFormatException e) {
			Response.serverError().entity("Input is not a valid number").build();
		}
		
        // Message to emit as the response
        String convertedTemperature = null;

        // Gets the current CICS Context for the environment we're running in
        CICSContext task = CICSContext.getCICSContext();
        
        // Allocate a byte buffer of 4 bytes for our integer
        ByteBuffer bytebuffer = ByteBuffer.allocate(4);

        //Data to place in the container to be sent to the target program.
        byte[] inputData = bytebuffer.putInt(temperature).array();
        bytebuffer.rewind();
        
        
		try {
			// Create a reference to the Program we will invoke and specify the channel
	        // Don't syncpoint between remote links, this is the default
			// Link to the program with an input container, containing the input data
			task.createProgramLinkerWithChannel(PROG_NAME, CHANNEL)
				.setSyncOnReturn(false)
				.setBytesInput(INPUT_CONTAINER, inputData)
				.link();
			
			// Get the data from the output container as a byte array
			// You could remove task.getChannel(CHANNEL) and do this as one chained command above, but this demonstrates how you could call this part later on in your program
			byte[] returnedData = task.getChannel(CHANNEL)
				.getBITContainer(OUTPUT_CONTAINER)
				.get();
			
			bytebuffer.put(returnedData);
			bytebuffer.rewind();
			convertedTemperature = String.valueOf(bytebuffer.getInt());
			
			if (convertedTemperature == null) {
				// Missing response container
				convertedTemperature = "<missing>";
			}
			
			// Format the final message and print it
			return "Returned from link to \'" + PROG_NAME + "\'. " + temperature + " degrees celcius = " + convertedTemperature + " degrees fahrenheit";
			
		} catch (CICSConditionException e) {
			String msg = "An exception has occured" + 
					"\nRESP: " + e.getRespCode() + 
					"\nRESP2: " + e.getResp2() + 
					"\nMessage: " + e.getMessage();
			
			Response r = Response.serverError().entity(msg).build();
			
			// Pass the error back up the handler chain
			throw new WebApplicationException(e, r);
		}
    }

}