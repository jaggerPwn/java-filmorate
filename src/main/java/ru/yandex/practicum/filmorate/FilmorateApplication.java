package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilmorateApplication {
	static ConfigurableApplicationContext run;


	public static ConfigurableApplicationContext getRun() {
		return run;
	}

	public static void setRun(ConfigurableApplicationContext run) {
		FilmorateApplication.run = run;
	}

	public static void main(String[] args) {
		setRun(SpringApplication.run(FilmorateApplication.class, args));

	}
}
