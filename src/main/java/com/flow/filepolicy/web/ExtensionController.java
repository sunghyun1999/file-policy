package com.flow.filepolicy.web;

import com.flow.filepolicy.service.ExtensionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

record ExtensionForm(@NotBlank(message = "값을 입력하세요.") @Size(max = 20, message = "최대 20자입니다.") String value) {}

@Controller
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {

  private final ExtensionService service;

  @GetMapping
  public String page(Model model, @RequestParam(required = false) String message) {
    model.addAllAttributes(service.getAllForView());
    model.addAttribute("message", message);
    return "extensions";
  }

  @PostMapping("/fixed/toggle")
  public String toggleFixed(@RequestParam String value,
      @RequestParam(defaultValue = "false") boolean enabled) {
    service.toggleFixed(value, enabled);
    return "redirect:/extensions";
  }

  @PostMapping("/custom/add")
  public String addCustom(ExtensionForm  form, RedirectAttributes ra) {
    service.addCustom(form.value());
    ra.addAttribute("message", "커스텀 확장자 추가 완료");
    return "redirect:/extensions";
  }

  @PostMapping("/custom/delete")
  public String deleteCustom(@RequestParam String value) {
    service.removeCustom(value);
    return "redirect:/extensions";
  }

  @PostMapping("/validate-filename")
  @ResponseBody
  public Object validateFilename(@RequestParam String filename) {
    return java.util.Map.of("filename", filename, "blocked", service.shouldBlockFilename(filename));
  }

}
