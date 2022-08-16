package com.example.blognpc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.blognpc.mapper")
@EnableScheduling
public class BlogNpcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogNpcApplication.class, args);
	}

}
