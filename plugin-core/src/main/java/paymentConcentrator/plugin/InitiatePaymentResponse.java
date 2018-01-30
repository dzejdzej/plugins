package paymentConcentrator.plugin;

public class InitiatePaymentResponse {

	private String paymentURL;
	private int paymentID;
	
	public InitiatePaymentResponse() {
		
	}

	public String getPaymentURL() {
		return paymentURL;
	}

	public void setPaymentURL(String paymentURL) {
		this.paymentURL = paymentURL;
	}

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}	
}
