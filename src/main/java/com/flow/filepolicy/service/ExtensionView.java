package com.flow.filepolicy.service;

import java.util.List;

public record ExtensionView(
    List<BlockedExtensionView> fixed,
    List<BlockedExtensionView> custom,
    int customCount,
    int customLimit
) {}
