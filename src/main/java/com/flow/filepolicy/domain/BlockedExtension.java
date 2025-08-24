package com.flow.filepolicy.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blocked_extension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockedExtension {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ext_value", length = 20, nullable = false, unique = true)
  private String value;

  @Enumerated(EnumType.STRING)
  @Column(name = "ext_type", length = 10, nullable = false)
  private ExtensionType type;

  @Column(nullable = false)
  private boolean enabled;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @Builder
  public BlockedExtension(Long id, String value, ExtensionType type, boolean enabled) {
    this.id = id;
    this.value = value;
    this.type = type;
    this.enabled = enabled;
  }

}
