package mockitotests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.jcicsx.BITContainer;
import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;
import com.ibm.cics.jcicsx.Channel;
import com.ibm.cics.jcicsx.RespCode;

import sample.CurrencyConverter;
import sample.InvalidConversionRateException;

public class CurrencyConverterTest {
	
	private CICSContext task;
	private CurrencyConverter currencyConverter;
	private Channel accountsChannel;
	private Channel ratesChannel;
	private BITContainer accountContainer;
	private BITContainer rateContainer;

	/**
	 * This is called before each test to reset all the mocked objects and initialise the currency converter
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// mock the task object, as we don't want the unit tests to actually call CICS
		task = Mockito.mock(CICSContext.class);
		
		// mock the channel objects and return these when getChannel is called on the mocked task
		accountsChannel = Mockito.mock(Channel.class);
		Mockito.when(task.getChannel("ACCOUNTS")).thenReturn(accountsChannel);
		ratesChannel = Mockito.mock(Channel.class);
		Mockito.when(task.getChannel("RATES")).thenReturn(ratesChannel);
		
		// mock the container objects that will be returned from the mocked channels above
		accountContainer = Mockito.mock(BITContainer.class);
		Mockito.when(accountsChannel.getBITContainer("CURRENT")).thenReturn(accountContainer);
		
		rateContainer = Mockito.mock(BITContainer.class);
		Mockito.when(ratesChannel.getBITContainer("USD-GBP")).thenReturn(rateContainer);

		currencyConverter = new CurrencyConverter(task);
	}

	/**
	 * Test that a converted currency is rounded down correctly to 2 decimal places
	 * @throws Exception
	 */
	@Test
	public void testRoundDown() throws Exception {

		Mockito.when(accountContainer.get()).thenReturn(getBytes(12.14));
		Mockito.when(rateContainer.get()).thenReturn(getBytes(0.82));
		String convertedCurrency = currencyConverter.convertCurrency("CURRENT");
		
		// value is 9.9548, check this is rounded down to 9.95
		assertEquals("9.95", convertedCurrency);
	}
	
	/**
	 * Test that a converted currency is rounded up correctly to 2 decimal places
	 * @throws Exception
	 */
	@Test
	public void testRoundUp() throws Exception {

		byte[] accountValue = getBytes(1234.56);
		Mockito.when(accountContainer.get()).thenReturn(accountValue);
		byte[] rateValue = getBytes(0.82);
		Mockito.when(rateContainer.get()).thenReturn(rateValue);
		String convertedCurrency = currencyConverter.convertCurrency("CURRENT");
		
		// value is 1012.3392, check this is rounded up to 1012.34
		assertEquals("1012.34", convertedCurrency);
	}
	
	/**
	 * Test that if the JCICSX calls were to throw a CICS Condition Exception, that this is passed out 
	 * @throws Exception
	 */
	@Test
	public void testCICSConditionExceptionThrown() throws Exception {
		
		Mockito.when(accountContainer.get()).thenReturn(getBytes(1234.56));
		Mockito.when(rateContainer.get()).thenThrow(new CICSConditionException(RespCode.CONTAINERERR, 5));
		
		try {
			currencyConverter.convertCurrency("CURRENT");
			
			fail("Exception should have been thrown");
		} catch (CICSConditionException e) {
			// pass, we expected the exception to be thrown
			// check the exception thrown contains the expected resp and resp2 codes
			assertEquals(RespCode.CONTAINERERR, e.getRespCode());
			assertEquals(5, e.getResp2());
		}
	}
	
	/**
	 * Test that if the conversion rate were to come back as a negative value, that the exception is thrown
	 * @throws Exception
	 */
	@Test
	public void testNegativeConversionRate() throws Exception {
		
		Mockito.when(accountContainer.get()).thenReturn(getBytes(1234.56));
		Mockito.when(rateContainer.get()).thenReturn(getBytes(-5));
		
		try {
			currencyConverter.convertCurrency("CURRENT");
			
			fail("Exception should have been thrown");
		} catch (InvalidConversionRateException e) {
			// pass, we expected the exception to be thrown, check it had the correct message
			assertEquals("Conversion rate not valid", e.getMessage());
		}
	}
	
	
	private byte[] getBytes(double value) {
		return ByteBuffer.allocate(8).putDouble(value).array();
	}
	
}
