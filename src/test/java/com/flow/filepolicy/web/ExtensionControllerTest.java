package com.flow.filepolicy.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.flow.filepolicy.service.ExtensionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExtensionControllerTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ExtensionService service;

  @Test
  void 페이지_로딩() throws Exception {
    mvc.perform(get("/extensions"))
        .andExpect(status().isOk())
        .andExpect(view().name("extensions"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("파일 확장자 차단 관리")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("고정 확장자")))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("커스텀 확장자")));
  }

  @Test
  void 고정확장자_토글_후_JSON_검증() throws Exception {
    // exe 허용 상태에서 → 차단으로
    mvc.perform(post("/extensions/fixed/toggle")
            .param("value","exe")
            .param("enabled","true"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/extensions"));

    // 파일명 검사
    mvc.perform(post("/extensions/validate-filename")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("filename","a.exe"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.blocked").value(true));
  }

  @Test
  void 커스텀_추가_삭제_후_JSON_검증() throws Exception {
    // 추가
    mvc.perform(post("/extensions/custom/add")
            .param("value","jar"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/extensions"));

    // 차단 확인
    mvc.perform(post("/extensions/validate-filename")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("filename","x.jar"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.blocked").value(true));

    // 삭제
    mvc.perform(post("/extensions/custom/delete")
            .param("value","jar"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/extensions"));

    // 허용 확인
    var blocked = service.shouldBlockFilename("x.jar");
    assertThat(blocked).isFalse();
  }

}
