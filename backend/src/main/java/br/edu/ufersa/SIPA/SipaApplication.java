package br.edu.ufersa.SIPA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SipaApplication {

	private static final Logger log = LoggerFactory.getLogger(SipaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SipaApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {
		@Value("${var1:valor default na classe SipaApplication}")
		String teste;

		@Override
		public void run(ApplicationArguments args) {
			log.info("SIPA iniciado corretamente com configuração: {}", teste);
		}
	}
}
