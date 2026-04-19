package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.CreateGoalRequest;
import dev.deyve.grainpayapi.dtos.GoalResponse;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.dtos.UpdateGoalRequest;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.GoalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(@AuthenticationPrincipal User user) {
        logger.info("GRAIN-API: Find all goals");
        List<GoalResponse> goals = goalService.findAll(user);
        return new ResponseEntity<>(new Response(goals, OK.value(), "List of goals"), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Find goal id={}", id);
        GoalResponse goal = goalService.findById(id, user);
        return new ResponseEntity<>(new Response(goal, OK.value(), "Goal found"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateGoalRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save goal name={}", request.name());
        GoalResponse saved = goalService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Goal created"), CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update goal id={}", id);
        GoalResponse updated = goalService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Goal updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete goal id={}", id);
        goalService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Goal deleted"), NO_CONTENT);
    }
}
