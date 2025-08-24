package com.flow.filepolicy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FilePolicyApplication {

  public static void main(String[] args) {
    SpringApplication.run(FilePolicyApplication.class, args);
  }

}
