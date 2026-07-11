package com.identra.governance.web;

import com.identra.ai.GovernanceScorecard;
import com.identra.governance.service.GovernanceScoringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/governance")
public class GovernanceController {

    private final GovernanceScoringService scoringService;

    public GovernanceController(GovernanceScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/score")
    public GovernanceScorecard score(@RequestBody Map<String, Object> body) {
        return scoringService.score(body);
    }
}
