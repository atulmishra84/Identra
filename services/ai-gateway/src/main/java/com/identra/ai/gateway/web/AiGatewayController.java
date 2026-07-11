package com.identra.ai.gateway.web;

import com.identra.ai.LlmRequest;
import com.identra.ai.LlmResponse;
import com.identra.ai.gateway.service.AiRouterService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/ai")
public class AiGatewayController {

    private final AiRouterService router;

    public AiGatewayController(AiRouterService router) {
        this.router = router;
    }

    @PostMapping("/completions")
    public LlmResponse complete(@Valid @RequestBody LlmRequest request) {
        return router.complete(request);
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return router.status();
    }
}
