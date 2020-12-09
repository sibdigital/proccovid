package ru.sibdigital.proccovid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProccovidApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProccovidApplication.class, args);
	}

}
