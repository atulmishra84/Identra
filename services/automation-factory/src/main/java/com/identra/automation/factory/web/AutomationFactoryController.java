package com.identra.automation.factory.web;

import com.identra.automation.AutomationGenerationRequest;
import com.identra.automation.AutomationPack;
import com.identra.automation.factory.service.AutomationFactoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/ai/automation-factory")
public class AutomationFactoryController {

    private final AutomationFactoryService factoryService;

    public AutomationFactoryController(AutomationFactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @PostMapping("/runs")
    public ResponseEntity<AutomationPack> generate(@RequestBody AutomationGenerationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(factoryService.generate(request));
    }

    @GetMapping("/runs")
    public List<AutomationPack> list() {
        return factoryService.list();
    }

    @GetMapping("/runs/{id}")
    public AutomationPack get(@PathVariable UUID id) {
        AutomationPack pack = factoryService.get(id);
        if (pack == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Automation pack not found");
        }
        return pack;
    }
}
