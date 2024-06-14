package br.com.joao.customerspanel;

import br.com.joao.customerspanel.infra.s3.S3Buckets;
import br.com.joao.customerspanel.infra.s3.S3Service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(
            S3Service s3Service,
            S3Buckets s3Buckets
    ) {
        return args -> {
            // testUploadAndDownloadS3(s3Service, s3Buckets);
        };
    }

    private static void testUploadAndDownloadS3(S3Service s3Service, S3Buckets s3Buckets) throws IOException {
        s3Service.putObject(
                s3Buckets.getCustomer(),
                "foo",
                "Hello, world!".getBytes());

        byte[] obj = s3Service.getObject(s3Buckets.getCustomer(), "foo");

        System.out.printf("Hooray: " + new String(obj));
    }

}
