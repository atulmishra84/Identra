package com.identra.factory.web;

import com.identra.factory.model.ConnectorGenerationRequest;
import com.identra.factory.model.ConnectorPackage;
import com.identra.factory.service.ConnectorFactoryService;
import jakarta.validation.Valid;
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
@RequestMapping("/v1/ai/connector-factory")
public class ConnectorFactoryController {

    private final ConnectorFactoryService factoryService;

    public ConnectorFactoryController(ConnectorFactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @PostMapping("/runs")
    public ResponseEntity<ConnectorPackage> generate(@Valid @RequestBody ConnectorGenerationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(factoryService.generate(request));
    }

    @GetMapping("/runs")
    public List<ConnectorPackage> list() {
        return factoryService.list();
    }

    @GetMapping("/runs/{id}")
    public ConnectorPackage get(@PathVariable UUID id) {
        ConnectorPackage pkg = factoryService.get(id);
        if (pkg == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Run not found");
        }
        return pkg;
    }
}
