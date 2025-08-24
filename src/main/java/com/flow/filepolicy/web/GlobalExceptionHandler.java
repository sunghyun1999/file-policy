package com.flow.filepolicy.web;

import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NoSuchElementException.class})
  public String handleDomain(RuntimeException ex, RedirectAttributes ra) {
    ra.addAttribute("message", ex.getMessage());
    return "redirect:/extensions";
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public String handleValidation(MethodArgumentNotValidException ex, RedirectAttributes ra) {
    var msg = ex.getBindingResult().getAllErrors().stream()
        .findFirst().map(e -> e.getDefaultMessage()).orElse("입력값이 올바르지 않습니다.");
    ra.addAttribute("message", msg);
    return "redirect:/extensions";
  }

  @ExceptionHandler(Exception.class)
  public String handleEtc(Exception ex, RedirectAttributes ra) {
    log.warn("Unhandled exception", ex);
    ra.addAttribute("message", "처리 중 오류가 발생했습니다.");
    return "redirect:/extensions";
  }

}
