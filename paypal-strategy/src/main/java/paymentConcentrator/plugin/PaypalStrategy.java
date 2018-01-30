package paymentConcentrator.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import paymentConcentrator.plugin.config.PayPalConfig;
import paymentConcentrator.plugin.rest.ResponseDTO;
import paymentConcentrator.plugin.rest.RestClientExceptionInfo;

@Component
@ComponentScan(basePackages = {"paymentConcentrator"})
public class PaypalStrategy implements PaymentStrategy {


	@Value("${paymentConcentrator.url}")
	private String paymentConcentratorUrl; 
	
	@Value("${error.origin.name}")
	private String errorOriginName; 
	
	@Autowired
	PayPalConfig payPalConfig;
	
	@Autowired
	RestTemplate rt;
	
	private final Log logger = LogFactory.getLog(this.getClass());



	public boolean supportsVrstaPlacanja(VrstaPlacanja vrstaPlacanja) {
		return vrstaPlacanja == VrstaPlacanja.PayPal;
	}

	public InitiatePaymentResponse initiatePayment(PCNewPayment payment) {
		logger.info("Requested new payment, initiating PayPal payment," + payment);
		InitiatePaymentResponse response = new InitiatePaymentResponse();
		response.setPaymentID(payment.getMerchantOrderId());
		//gadjaj sam sebe rest template completeResponse da se upise u bazu da je uspesno placeno!!!
		
		String url = "https://" + payPalConfig.getPaymentConcentratorURL() + "/paymentConcentratorMain/completePaymentResponse";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		ResponseDTO dto = new ResponseDTO(); 
		dto.setSuccess(true);
		dto.setTransactionIdMerchant(payment.getMerchantOrderId());
		
		HttpEntity<ResponseDTO> requestProcessPayment = new HttpEntity<>(dto);

		ResponseEntity<String> processPaymentResponse = rt.postForEntity(url, requestProcessPayment,
				String.class);
		
		
		return response;
	}
		
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> exceptionHandlerHttpError(HttpClientErrorException ex) {
		String body = ex.getResponseBodyAsString();
		RestClientExceptionInfo info = new RestClientExceptionInfo(); 
		
		
		if(RestClientExceptionInfo.parse(body) == null) {
			//ova aplikacija je uzrok exceptiona
			//priprema se exception za propagiranje dalje i loguje se
			info.setOrigin(errorOriginName);
			info.setInfo(body);
		}
		else {
			info.setOrigin(RestClientExceptionInfo.parse(body).getOrigin() );
			info.setInfo(RestClientExceptionInfo.parse(body).getInfo() );
		}
		logger.error("HttpClientErrorException, info:" + RestClientExceptionInfo.toJSON(info));
		
		
		return ResponseEntity.status(ex.getStatusCode()).body(RestClientExceptionInfo.toJSON(info));
	}
	
}
