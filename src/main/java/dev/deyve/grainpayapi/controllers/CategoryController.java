package dev.deyve.grainpayapi.controllers;

import dev.deyve.grainpayapi.dtos.CategoryResponse;
import dev.deyve.grainpayapi.dtos.CreateCategoryRequest;
import dev.deyve.grainpayapi.dtos.Response;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.services.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Response> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        logger.info("GRAIN-API: Find categories page={} size={} sort={}", page, size, sort);

        Page<CategoryResponse> categories = categoryService.findAll(user, pageable);
        return new ResponseEntity<>(new Response(categories.getContent(), OK.value(), "List of categories"), OK);
    }

    @PostMapping
    public ResponseEntity<Response> post(
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Save category: {}", request.name());
        CategoryResponse saved = categoryService.save(request, user);
        return new ResponseEntity<>(new Response(saved, CREATED.value(), "Category created"), CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Get category by id {}", id);
        CategoryResponse category = categoryService.findById(id, user);
        return new ResponseEntity<>(new Response(category, OK.value(), "Category found"), OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> put(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Update category by id {}", id);
        CategoryResponse updated = categoryService.updateById(id, request, user);
        return new ResponseEntity<>(new Response(updated, OK.value(), "Category updated"), OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        logger.info("GRAIN-API: Delete category by id {}", id);
        categoryService.deleteById(id, user);
        return new ResponseEntity<>(new Response(null, NO_CONTENT.value(), "Category deleted"), NO_CONTENT);
    }
}
