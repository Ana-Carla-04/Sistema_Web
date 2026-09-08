package br.edu.ufersa.SIPA;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SipaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SipaApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {
		@Value("${var1:valor default na classe SipaApplication}")
		String teste;

		public void run(ApplicationArguments args) throws Exception {
			System.out.println("Rodou corretamente! com " + teste);
		}
	}
}