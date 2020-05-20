package sample;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

/**
 * A sample servlet to demonstrate how to use JCICSX to LINK to a CICS Program with CHAR data
 */
@WebServlet("/SampleServlet")
public class SampleServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * Name of the program to invoke.
     */
    private static final String PROG_NAME = "PROG";
    
    /**
     * Name of the channel to use.
     */
    private static final String CHANNEL = "CHAN";
    
    /**
     * Name of the container used to send data to the target program.
     */
    private static final String INPUT_CONTAINER = "INPUTDATA";
    
    /**
     * Name of the container which will contain the response from the target program.
     */
    private static final String OUTPUT_CONTAINER = "OUTPUTCONT";

    /**
     * Data to place in the container to be sent to the target program.
     */
    private static final String INPUTSTRING = "Hello from Java";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		response.getWriter().print("Hello world!");
		
        // Message to emit as the response
        String resultStr = null;

        // Gets the current CICS Context for the environment we're running in
        CICSContext task = CICSContext.getCICSContext();
        
		try {
			// Create a reference to the Program we will invoke and specify the channel
	        // Don't syncpoint between remote links, this is the default
			// Link to the program with an input container, containing the input string of "Hello from Java"
			// Get the output from the Program as a string
			
			resultStr = task.createProgramLinkerWithChannel(PROG_NAME, CHANNEL)
				.setSyncOnReturn(false)
				.setStringInput(INPUT_CONTAINER, INPUTSTRING)
				.link()
				.getStringOutput(OUTPUT_CONTAINER);

			if (resultStr == null) {
				// Missing response container
				resultStr = "<missing>";
			}
			
			// Format the final message and print it
			String msg = MessageFormat.format("Returned from link to {0} with a text response of \'{1}\'",
					PROG_NAME, resultStr);
			response.getWriter().println(msg);
			
		} catch (CICSConditionException e) {
			response.getWriter().println("An exception has occured" + 
					"\nRESP: " + e.getResp2() + 
					"\nRESP2: " + e.getResp2() + 
					"\nMessage: " + e.getMessage());
		}
    }

}