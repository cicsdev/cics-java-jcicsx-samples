package sample;

public class InvalidConversionRateException extends Exception {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getMessage() {
		return "Conversion rate not valid";
	}

}
