package com.flow.filepolicy.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.flow.filepolicy.domain.ExtensionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class BlockedExtensionRepositoryTest {

  @Autowired
  BlockedExtensionRepository repo;

  @Test
  void seed_고정확장자_8개존재_타입정렬조회() {
    var fixed = repo.findAllByTypeOrderByValueAsc(ExtensionType.FIXED);
    assertThat(fixed).hasSize(8);
    assertThat(fixed).isSortedAccordingTo((a,b) -> a.getValue().compareTo(b.getValue()));
  }

  @Test
  void 고정확장자_enable_toggle_업데이트_1행() {
    var affected = repo.updateFixedEnabled("exe", true);
    assertThat(affected).isEqualTo(1);

    var fixed = repo.findAllByTypeOrderByValueAsc(ExtensionType.FIXED);
    var exe = fixed.stream().filter(f -> f.getValue().equals("exe")).findFirst().orElseThrow();
    assertThat(exe.isEnabled()).isTrue();
  }

}
