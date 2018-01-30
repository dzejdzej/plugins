package paymentConcentrator2.plugin.config;

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
public class AcquirerConfig {

	@Value("${acquirer.url}")
	private String acquirerURL;

	@Value("${test}")
	private String test;

	private final Log logger = LogFactory.getLog(this.getClass());

	@PostConstruct
	public void init() {
		for (int i = 0; i < 100; i++) {
			logger.error("ACQUIRER CONF" + test);
		}
	}

	public String getAcquirerURL() {
		return acquirerURL;
	}

}

/*
 * @Component public class AcquirerConfig {
 * 
 * private String acquirerURL;
 * 
 * @PostConstruct public void initProps() throws IOException { Properties
 * properties = new Properties();
 * properties.load(AcquirerStrategy.class.getResourceAsStream(
 * "/acquirer.properties")); acquirerURL =
 * properties.getProperty("acquirer.url"); }
 * 
 * 
 * public String getAcquirerURL() { return acquirerURL; }
 * 
 * }
 */
