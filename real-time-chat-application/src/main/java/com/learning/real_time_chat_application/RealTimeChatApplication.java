package com.learning.real_time_chat_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RealTimeChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeChatApplication.class, args);
	}

}
