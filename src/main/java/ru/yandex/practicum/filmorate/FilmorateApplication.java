package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilmorateApplication {
    @lombok.Setter
    @lombok.Getter
    static ConfigurableApplicationContext run;

    public static void main(String[] args) {
        setRun(SpringApplication.run(FilmorateApplication.class, args));
    }
}
