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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

/**
 * A sample servlet to demonstrate how to use JCICSX to get data from BIT containers
 */
@WebServlet("/CurrencyServlet")
public class CurrencyServlet extends HttpServlet {
	
	Logger log = Logger.getLogger(CurrencyServlet.class.getName());
	
    private static final long serialVersionUID = 1L;
    
    public static String getDefaultAccountName() {
    	return "cicsuser";
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		
		String accountName = request.getParameter("acctname");
		if (accountName == null) {
			accountName = getDefaultAccountName();
		}
		response.getWriter().print("Converting balance for user "+accountName+"<br/>");
		
		// Gets the current CICS Context for the environment we're running in
		CICSContext task = CICSContext.getCICSContext();
		
		try {
			CurrencyConverter converter = new CurrencyConverter(task);
			String convertedCurrency = converter.convertCurrency(accountName);
			response.getWriter().append("$").println(convertedCurrency);
		} catch (CICSConditionException e) {
			log.log(Level.SEVERE, "Exception occurred converting currency", e);
			response.getWriter().println("An exception has occured" + 
					"\nRESP: " + e.getResp2() + 
					"\nRESP2: " + e.getResp2() + 
					"\nMessage: " + e.getMessage());
		} catch (InvalidConversionRateException e) {
			response.getWriter().println("An exception occured while trying to get the conversion rate");
			log.log(Level.SEVERE, "Exception occurred getting currency conversion rate", e);
		}
    }

}