package br.com.caelum.ichat.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="br.com.caelum")
public class SpringBootStartup extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootStartup.class);
    }
    
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringBootStartup.class, args);
	}
	
}
