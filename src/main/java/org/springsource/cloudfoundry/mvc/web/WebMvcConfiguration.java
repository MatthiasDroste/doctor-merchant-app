package org.springsource.cloudfoundry.mvc.web;

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springsource.cloudfoundry.mvc.services.config.ServicesConfiguration;

@Configuration
@EnableWebMvc
@Import(ServicesConfiguration.class)
@ComponentScan
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
	private static Log log = LogFactory.getLog("WebMvcConfiguration");

	@Bean
	public InternalResourceViewResolver internalResourceViewResolver() {
		generateToken();
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		internalResourceViewResolver.setViewClass(JstlView.class);
		internalResourceViewResolver.setPrefix("/WEB-INF/views/");
		internalResourceViewResolver.setSuffix(".jsp");
		return internalResourceViewResolver;
	}

	private void generateToken() {
		try {
			URL url = new URL("https://test.ctpe.net/frontend/GenerateToken");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			String parameters = "SECURITY.SENDER=696a8f0fabffea91517d0eb0a0bf9c33"
					+ "&TRANSACTION.CHANNEL=52275ebaf361f20a76b038ba4c806991"
					+ "&TRANSACTION.MODE=INTEGRATOR_TEST"
					+ "&USER.LOGIN=1143238d620a572a726fe92eede0d1ab"
					+ "&USER.PWD=demo"
					+ "&PAYMENT.TYPE=DB"
					+ "&PRESENTATION.AMOUNT=50.99"
					+ "&PRESENTATION.CURRENCY=EUR";

			IOUtils.write(parameters, conn.getOutputStream());

			conn.connect();

			String content = IOUtils.toString(conn.getInputStream());
			System.out.println(content);
		} catch (Exception ex) {
			log.error("error while sending the generate token: "
					+ ex.getMessage());
		}
	}

	@Bean
	public MessageSource messageSource() {
		String[] baseNames = "messages".split(",");
		ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasenames(baseNames);
		return resourceBundleMessageSource;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/web/**").addResourceLocations("/web/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("customers");
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
