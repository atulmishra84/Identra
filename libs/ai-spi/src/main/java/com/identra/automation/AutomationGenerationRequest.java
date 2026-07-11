package com.identra.automation;

import java.util.List;
import java.util.Map;

public record AutomationGenerationRequest(
        String applicationName,
        AutomationTargetType targetType,
        AutomationToolchain toolchain,
        List<String> actions,
        Map<String, Object> hints
) {
}
