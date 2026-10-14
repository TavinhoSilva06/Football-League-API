package com.example.Football_League_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballLeagueApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FootballLeagueApiApplication.class, args);
	}

}
