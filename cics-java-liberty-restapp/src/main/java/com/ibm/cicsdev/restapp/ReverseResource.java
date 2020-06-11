/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2020 All Rights Reserved                       */
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */

package com.ibm.cicsdev.restapp;

import java.util.Calendar;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;
import com.ibm.cicsdev.restapp.bean.ReverseResult;

/**
 * RESTful web application that links to the CICS COBOL program EDUCHAN and
 * returns a reversed input string.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ReverseResource {

	/**
	 * Formatting string used to produce an ISO-8601 standard timestamp.
	 */
	private static final String ISO8601_FORMAT = "%tFT%<tT.%<tLZ";

	/**
	 * Default string to reverse.
	 */
	private static final String DEFAULT_STRING = "Hello from Java";

	/**
	 * Name of the CICS program the {@link #reverse(String)} method will LINK to.
	 */
	private static final String PROGRAM_NAME = "EDUCHAN";

	/**
	 * Name of the channel to create. Must be 16 characters or less.
	 */
	private static final String CHANNEL_NAME = "MYCHANNEL";

	/**
	 * Name of the container used to pass the data to the CICS program.
	 */
	private static final String INPUT_CONTAINER = "INPUTDATA";

	/**
	 * Name of the container used to pass the data from the CICS program.
	 */
	private static final String OUTPUT_CONTAINER = "OUTPUTDATA";

	/**
	 * GET method with no additional input
	 * 
	 * @return - JAXB bean ReverseResult with input, output and time
	 */
	@GET
	public ReverseResult reverseNoArgs() {
		return reverse(DEFAULT_STRING);
	}

	/**
	 * GET method to process input string from URI path Links to CICS program to
	 * reverse input string
	 * 
	 * @param inputStr - String input
	 * @return - JAXB bean ReverseResult with input, output and time
	 */
	@GET
	@Path("/{text}")
	public ReverseResult reverse(@PathParam("text") String inputStr) {

		// Truncate the input string
		inputStr = inputStr.trim();

		// Get hold of the current CICS Task
		CICSContext task = CICSContext.getCICSContext();

		String outputStr;

		try {
			// Create a reference to the Program we will invoke
			// Create a channel to store the data
			// Create a CHAR container populated with a simple String
			// CHAR containers will be created in UTF-16 when created in JCICSX
			// Add the input string to the response object
			// Link to the CICS program
			// Read CHAR container from channel container data as formatted string
			// CICS returns this in a UTF-16 format and JCICSX reads this into a String
			outputStr = task.createProgramLinkerWithChannel(PROGRAM_NAME, CHANNEL_NAME)
					.setStringInput(INPUT_CONTAINER, inputStr).link().getOutputCHARContainer(OUTPUT_CONTAINER).get();

		} catch (CICSConditionException e) {
			// Report the error
			String msg = "An exception has occured" + "\nRESP: " + e.getResp2() + "\nRESP2: " + e.getResp2()
					+ "\nMessage: " + e.getMessage();
			Response r = Response.serverError().entity(msg).build();

			// Pass the error back up the handler chain
			throw new WebApplicationException(e, r);
		}

		// Create the result bean
		ReverseResult result = new ReverseResult();

		// Populate with the original string
		result.setOriginalText(inputStr);

		// Format the current time to ISO 8601 standards
		Calendar nowUTC = Calendar.getInstance(TimeZone.getTimeZone("Z"));
		result.setTime(String.format(ISO8601_FORMAT, nowUTC));

		// Trim the output and store in the result object
		result.setReverseText(outputStr.trim());

		// Was this truncated?
		result.setTruncated(outputStr.length() < inputStr.length());

		// Return result object
		return result;
	}
}
