package com.flow.filepolicy;

import com.flow.filepolicy.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootSmokeRunner implements CommandLineRunner {

  private final ExtensionService service;

  @Override
  public void run(String... args) throws Exception {
    var map = service.getAllForView();
    System.out.println("[SMOKE] fixed=" + ((java.util.List<?>) map.get("fixed")).size()
        + ", custom=" + ((java.util.List<?>) map.get("custom")).size()
        + ", limit=" + map.get("customLimit"));
  }

}
