package paymentConcentrator.plugin;


public interface PaymentStrategy {
	
	InitiatePaymentResponse initiatePayment(PCNewPayment payment);
	
	boolean supportsVrstaPlacanja(VrstaPlacanja vrstaPlacanja); 
}
