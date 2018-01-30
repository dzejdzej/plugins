package paymentConcentrator2.plugin;

import javax.annotation.PostConstruct;

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

import paymentConcentrator.plugin.InitiatePaymentResponse;
import paymentConcentrator.plugin.PCNewPayment;
import paymentConcentrator.plugin.PaymentStrategy;
import paymentConcentrator.plugin.VrstaPlacanja;
import paymentConcentrator2.plugin.config.AcquirerConfig;
import paymentConcentrator2.plugin.rest.RestClientExceptionInfo;



@Component
@ComponentScan(basePackages = {"paymentConcentrator2"})
public class AcquirerStrategy implements PaymentStrategy {

	@Value("${acquirer.url}")
	private String acquirerUrl; 
	
	@Value("${error.origin.name}")
	private String errorOriginName; 
	
	@Autowired
	AcquirerConfig acquirerConfig;
	
	@Autowired
	RestTemplate rt;
	
	@PostConstruct
	public void init() {
		for(int i = 0; i < 100; i++) {
			logger.error("ACQUIRER STRATEGY" + acquirerConfig);
		}
	}
	
	private final Log logger = LogFactory.getLog(this.getClass());

	public InitiatePaymentResponse initiatePayment(PCNewPayment payment) {
		String url = "https://" + acquirerConfig.getAcquirerURL() + "/acquirerMain/requestPayment";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<PCNewPayment> requestProcessPayment = new HttpEntity<>(payment);

		ResponseEntity<InitiatePaymentResponse> processPaymentResponse = rt.postForEntity(url, requestProcessPayment,
				InitiatePaymentResponse.class);
		return processPaymentResponse.getBody();

	}


	public boolean supportsVrstaPlacanja(VrstaPlacanja vrstaPlacanja) {
		return vrstaPlacanja == VrstaPlacanja.Banka;
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
