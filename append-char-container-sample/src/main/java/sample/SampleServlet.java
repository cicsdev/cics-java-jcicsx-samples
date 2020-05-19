package sample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.jcicsx.CHARContainer;
import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/SampleServlet")
public class SampleServlet extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
    
    private static final String CHANNEL_NAME = "charchan";
    
    private static final String CONTAINER_NAME = "charcont";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		response.getWriter().print("Hello world!");
		
		// Gets the current CICS Context for the environment we're running in
		CICSContext task = CICSContext.getCICSContext();
		
		try {
			// Create a new channel called "charchan", with a CHAR container called "charcont"
			// Add the text to the CHAR container
			CHARContainer charContainer = task.getChannel(CHANNEL_NAME)
					.getCHARContainer(CONTAINER_NAME)
					.put("I'm running under task ");
			
			// Get the current task number that this unit of work is running under
			Integer taskNumber = task.getTaskNumber();
			
			// Add the task number to the enf of the CHAR container
			charContainer = charContainer.append(taskNumber.toString());
			
			// Get the full contents of the container, and print this
			String combinedString = charContainer.get();
			response.getWriter().println(combinedString);
			
		} catch (CICSConditionException e) {
			System.out.println("An exception has occured with" + 
					" RESP: " + e.getResp2() + 
					" RESP2: " + e.getResp2() + 
					" Message: " + e.getMessage());
		}
    }

}