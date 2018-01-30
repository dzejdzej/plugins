package paymentConcentrator.plugin.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:acquirer.properties")
// @ConfigurationProperties(prefix = "mail")
@Configuration
public class PayPalConfig {

	@Value("${paymentConcentrator.url}")
	private String paymentConcentratorURL;

	
	public String getPaymentConcentratorURL() {
		return paymentConcentratorURL;
	}
}