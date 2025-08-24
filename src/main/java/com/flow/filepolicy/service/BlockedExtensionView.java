package com.flow.filepolicy.service;

import com.flow.filepolicy.domain.BlockedExtension;
import com.flow.filepolicy.domain.ExtensionType;
import java.time.Instant;
import java.time.LocalDateTime;

public record BlockedExtensionView(
    Long id,
    String value,
    ExtensionType type,
    boolean enabled,
    Instant createdAt
) {
  public static BlockedExtensionView from(BlockedExtension e) {
    return new BlockedExtensionView(
        e.getId(),
        e.getValue(),
        e.getType(),
        e.isEnabled(),
        e.getCreatedAt()
    );
  }
}
