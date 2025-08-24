package com.flow.filepolicy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flow.filepolicy.domain.ExtensionType;
import com.flow.filepolicy.repo.BlockedExtensionRepository;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ExtensionServiceTest {

  @Autowired
  ExtensionService service;

  @Autowired
  BlockedExtensionRepository repo;

  @Autowired
  CacheManager cacheManager;

  @AfterEach
  void clearCache() {
    var c = cacheManager.getCache("extList");
    if (c != null) c.clear();
  }

  @Test
  void 기본정책_및_고정확장자_토글() {
    // 초기 고정 8개(시드), 모두 disabled 가정
    var fixed = repo.findAllByTypeOrderByValueAsc(ExtensionType.FIXED);
    assertThat(fixed).hasSize(8);
    assertThat(fixed).allMatch(b -> !b.isEnabled());

    // exe 미차단
    assertThat(service.shouldBlockFilename("a.exe")).isFalse();

    // exe 켜면 차단
    service.toggleFixed("exe", true);
    assertThat(service.shouldBlockFilename("a.exe")).isTrue();

    // 다시 끄면 허용
    service.toggleFixed("exe", false);
    assertThat(service.shouldBlockFilename("a.exe")).isFalse();
  }

  @Test
  void 커스텀_추가_정규화_중복_길이() {
    service.addCustom("  JAR ");
    // 케이스 무시/정규화 후 차단
    assertThat(service.shouldBlockFilename("x.JaR")).isTrue();

    // 전역 유니크 제약(=FIXED 포함)으로 중복 불가
    assertThatThrownBy(() -> service.addCustom("jar"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("이미 존재");

    // 길이 20 초과 불가
    assertThatThrownBy(() -> service.addCustom("TOO-LONG-EXT-NAME-OVER-20"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 커스텀_삭제_후_허용으로() {
    service.addCustom("zip");
    assertThat(service.shouldBlockFilename("a.zip")).isTrue();

    service.removeCustom("zip");
    assertThat(service.shouldBlockFilename("a.zip")).isFalse();

    assertThatThrownBy(() -> service.removeCustom("zip"))
        .isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void 캐시_적중과_쓰기후_무효화() {
    // 1) 첫 조회 → 캐시 미스
    var v1 = service.getAllForView();

    // 2) 두 번째 조회 → 캐시 적중 (동일 크기 정도로 행태 확인)
    var v2 = service.getAllForView();
    assertThat(v2.fixed()).hasSameSizeAs(v1.fixed());
    assertThat(v2.custom()).hasSameSizeAs(v1.custom());

    // 3) 쓰기 발생 → 캐시 evict 후 반영 확인
    service.addCustom("jar");
    var v3 = service.getAllForView();
    assertThat(v3.custom().stream().anyMatch(e -> e.value().equals("jar"))).isTrue();
    assertThat(v3.customCount()).isEqualTo(v1.customCount() + 1);
  }

}
