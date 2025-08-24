package com.flow.filepolicy.repo;

import com.flow.filepolicy.domain.BlockedExtension;
import com.flow.filepolicy.domain.ExtensionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BlockedExtensionRepository extends JpaRepository<BlockedExtension, Long> {

  Optional<BlockedExtension> findByValue(String value);

  long countByType(ExtensionType type);

  List<BlockedExtension> findAllByTypeOrderByValueAsc(ExtensionType type);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update BlockedExtension b set b.enabled = :enabled where b.value = :value and b.type = 'FIXED'")
  int updateFixedEnabled(String value, boolean enabled);

}
