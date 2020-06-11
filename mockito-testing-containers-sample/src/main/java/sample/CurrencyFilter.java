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
import java.nio.ByteBuffer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.ibm.cics.jcicsx.BITContainer;
import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;
import com.ibm.cics.jcicsx.Channel;

/*
 * This servlet filter runs before the main servlet and sets up some sample data - an account balance and a conversion rate.
 */
@WebFilter("/CurrencyServlet")
public class CurrencyFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String accountName = request.getParameter("acctname");
		if (accountName == null || accountName.length() == 0) {
			accountName = CurrencyServlet.getDefaultAccountName();
		}
		
		CICSContext task = CICSContext.getCICSContext();
		try {
			seedAccountContainer(task, accountName);
			seedConversionContainer(task);
		} catch (CICSConditionException e) {
			throw new ServletException("Exception settings up CICS environment", e);
		}

		chain.doFilter(request, response);
		
		
	}
	
	private void seedAccountContainer(CICSContext task, String accountName) throws CICSConditionException {
		Channel accountChannel = task.getChannel(CurrencyConverter.ACCOUNT_CHANNEL);
		BITContainer accountContainer = accountChannel.getBITContainer(accountName);
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(53000);
		accountContainer.put(bytes);
	}
	
	private void seedConversionContainer(CICSContext task) throws CICSConditionException {
		Channel conversionChannel = task.getChannel(CurrencyConverter.RATES_CHANNEL);
		BITContainer conversionContainer = conversionChannel.getBITContainer(CurrencyConverter.USD2GBP_CONTAINER);
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(0.79);
		conversionContainer.put(bytes);
	}

	@Override
	public void destroy() {}

}
