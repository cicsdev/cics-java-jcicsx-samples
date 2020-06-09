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

import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;

public class CurrencyConverter {
	
	public static final String USD2GBP_CONTAINER = "USD-GBP";
	public static final String RATES_CHANNEL = "RATES";
	public static final String ACCOUNT_CHANNEL = "ACCOUNTS";
	
	private CICSContext task;

	public CurrencyConverter(CICSContext task) {
		this.task = task;
	}
	
	public String convertCurrency(String accountName) throws CICSConditionException, InvalidConversionRateException {
		if (accountName == null || accountName.length() == 0) {
			throw new IllegalArgumentException("Account name must be provided");
		}
		double balance = getAccountBalance(accountName);
		double rate = getUSDGBPConversionRate();
		double convertedBalance = balance * rate;
		return String.format("%.2f", convertedBalance);
	}
	
	private double getAccountBalance(String accountName) throws CICSConditionException {
		byte[] bytes = task.getChannel(ACCOUNT_CHANNEL).getBITContainer(accountName).get();
		return ByteBuffer.wrap(bytes).getDouble();
	}

	private double getUSDGBPConversionRate() throws CICSConditionException, InvalidConversionRateException {
		byte[] bytes = task.getChannel(RATES_CHANNEL).getBITContainer(USD2GBP_CONTAINER).get();
		
		double rate = ByteBuffer.wrap(bytes).getDouble();			
		
		if (rate < 0) {
			throw new InvalidConversionRateException();
		}
		
		return rate;
	}
	
}
