package josefa.webbuppgift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"josefa.webbuppgift.controller",
		"josefa.webbuppgift.service",
		"josefa.webbuppgift.repository",
		"josefa.webbuppgift.security"
})
public class WebbuppgiftApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebbuppgiftApplication.class, args);
		System.out.println("WebbuppgiftApplication is running!");
	}
}
