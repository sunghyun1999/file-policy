package com.flow.filepolicy.service;

import com.flow.filepolicy.domain.BlockedExtension;
import com.flow.filepolicy.domain.ExtensionType;
import com.flow.filepolicy.repo.BlockedExtensionRepository;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExtensionService {

  private static final Pattern VALID = Pattern.compile("^[a-z0-9]{1,20}$");
  private static final int CUSTOM_LIMIT = 200;

  private final BlockedExtensionRepository blockedExtensionRepository;

  /** 입력값 표준화 + 유효성 검사 */
  private String normalize(String raw) {
    if (raw == null) throw new IllegalArgumentException("value is required");
    var v = Normalizer.normalize(raw.trim(), Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
    if (!VALID.matcher(v).matches())
      throw new IllegalArgumentException("확장자는 영문 소문자+숫자(1~20)만 허용합니다.");
    return v;
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "extList")
  public ExtensionView getAllForView() {
    var fixedEntities  = blockedExtensionRepository.findAllByTypeOrderByValueAsc(ExtensionType.FIXED);
    var customEntities = blockedExtensionRepository.findAllByTypeOrderByValueAsc(ExtensionType.CUSTOM);

    var fixed  = fixedEntities.stream().map(BlockedExtensionView::from).toList();
    var custom = customEntities.stream().map(BlockedExtensionView::from).toList();

    return new ExtensionView(fixed, custom, custom.size(), CUSTOM_LIMIT);
  }

  /** 고정 확장자 ON/OFF */
  @CacheEvict(cacheNames = "extList", allEntries = true)
  public void toggleFixed(String rawValue, boolean enabled) {
    var value = normalize(rawValue);
    var affected = blockedExtensionRepository.updateFixedEnabled(value, enabled);
    if (affected == 0) throw new NoSuchElementException("해당 고정 확장자가 없습니다: " + value);
  }

  /** 커스텀 확장자 추가 */
  @CacheEvict(cacheNames = "extList", allEntries = true)
  public void addCustom(String rawValue) {
    var value = normalize(rawValue);

    if (blockedExtensionRepository.countByType(ExtensionType.CUSTOM) >= CUSTOM_LIMIT)
      throw new IllegalStateException("커스텀 확장자는 최대 " + CUSTOM_LIMIT + "개까지 가능합니다.");
    if (blockedExtensionRepository.findByValue(value).isPresent())
      throw new IllegalStateException("이미 존재하는 확장자입니다.");

    var e = BlockedExtension.builder()
        .value(value)
        .type(ExtensionType.CUSTOM)
        .enabled(true)
        .build();
    blockedExtensionRepository.saveAndFlush(e);
  }

  /** 커스텀 확장자 삭제 */
  @CacheEvict(cacheNames = "extList", allEntries = true)
  public void removeCustom(String rawValue) {
    var value = normalize(rawValue);
    var e = blockedExtensionRepository.findByValue(value).orElseThrow(() -> new NoSuchElementException("존재하지 않는 확장자"));
    if (e.getType() != ExtensionType.CUSTOM) throw new IllegalStateException("커스텀만 삭제할 수 있습니다.");
    blockedExtensionRepository.delete(e);
  }

  /** 파일명이 차단 대상인지 검사 */
  @Transactional(readOnly = true)
  public boolean shouldBlockFilename(String rawFilename) {
    if (rawFilename == null || rawFilename.isBlank()) return false;
    var name = Normalizer.normalize(rawFilename.trim(), Normalizer.Form.NFKC);
    var idx = name.lastIndexOf('.');
    if (idx < 0 || idx == name.length() - 1) return false;

    var ext = name.substring(idx + 1).toLowerCase(Locale.ROOT);

    var view = getAllForView();

    boolean blockedByFixed = view.fixed().stream()
        .anyMatch(f -> f.enabled() && f.value().equals(ext));
    if (blockedByFixed) return true;

    return view.custom().stream().anyMatch(c -> c.value().equals(ext));
  }

}
