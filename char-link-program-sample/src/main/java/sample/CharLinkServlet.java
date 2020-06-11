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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

/**
 * A sample servlet to demonstrate how to use JCICSX to LINK to a CICS Program
 * with CHAR data
 */
@WebServlet("/SampleServlet")
public class CharLinkServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Name of the program to invoke.
	 */
	private static final String PROG_NAME = "EDUCHAN";

	/**
	 * Name of the channel to use.
	 */
	private static final String CHANNEL = "MYCHANNEL";

	/**
	 * Name of the container used to send data to the target program.
	 */
	private static final String INPUT_CONTAINER = "INPUTDATA";

	/**
	 * Name of the container which will contain the response from the target
	 * program.
	 */
	private static final String OUTPUT_CONTAINER = "OUTPUTDATA";

	/**
	 * Data to place in the container to be sent to the target program.
	 */
	private static final String INPUTSTRING = "Hello from Java";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		response.getWriter().print("Hello world! ");

		// Message to emit as the response
		String resultStr = null;

		// Gets the current CICS Context for the environment we're running in
		CICSContext task = CICSContext.getCICSContext();

		try {
			// Create a reference to the Program we will invoke and specify the channel
			// Don't syncpoint between remote links, this is the default
			// Link to the program with an input container, containing the input string of
			// "Hello from Java"
			task.createProgramLinkerWithChannel(PROG_NAME, CHANNEL).setSyncOnReturn(false)
					.setStringInput(INPUT_CONTAINER, INPUTSTRING).link();

			// Get the data from the output container as a string
			// You could remove task.getChannel(CHANNEL) and do this as one chained command
			// above, but this demonstrates how you could call this part later on in your
			// program
			resultStr = task.getChannel(CHANNEL).getCHARContainer(OUTPUT_CONTAINER).get();

			if (resultStr == null) {
				// Missing response container
				resultStr = "<missing>";
			}

			// Format the final message and print it
			String msg = "Returned from link to \'" + PROG_NAME + "\' with a text response of \'" + resultStr + "\'";
			response.getWriter().println(msg);

		} catch (CICSConditionException e) {
			response.getWriter().println("An exception has occured" + "\nRESP: " + e.getRespCode() + "\nRESP2: "
					+ e.getResp2() + "\nMessage: " + e.getMessage());
		}
	}

}