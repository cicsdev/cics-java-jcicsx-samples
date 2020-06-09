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
	
	private CICSContext task;

	public CurrencyConverter(CICSContext task) {
		this.task = task;
	}
	
	public String convertCurrency(String accountName) throws CICSConditionException, InvalidConversionRateException {
		double balance = getAccountBalance(accountName);
		double rate = getUSDGBPConversionRate();
		double convertedBalance = balance * rate;
		return String.format("%.2f", convertedBalance);
	}
	
	private double getAccountBalance(String accountName) throws CICSConditionException {
		byte[] bytes = task.getChannel("ACCOUNTS").getBITContainer(accountName).get();
		return ByteBuffer.wrap(bytes).getDouble();
	}
	
	private double getUSDGBPConversionRate() throws CICSConditionException, InvalidConversionRateException {
		byte[] bytes = task.getChannel("RATES").getBITContainer("USD-GBP").get();
		
		double rate = ByteBuffer.wrap(bytes).getDouble();			
		
		if (rate < 0) {
			throw new InvalidConversionRateException();
		}
		
		return rate;
	}
	
}
