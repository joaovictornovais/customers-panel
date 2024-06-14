package br.com.joao.customerspanel;

import br.com.joao.customerspanel.infra.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(
            S3Service s3Service
    ) {
        return args -> {
            s3Service.putObject(
                    "customers-panel",
                    "foo",
                    "Hello, world!".getBytes());

            byte[] obj = s3Service.getObject("customers-panel", "foo");

            System.out.printf("Hooray: " + new String(obj));
        };
    }

}
