package dev.deyve.grainpayapi.services;

import dev.deyve.grainpayapi.dtos.CategoryResponse;
import dev.deyve.grainpayapi.dtos.CreateCategoryRequest;
import dev.deyve.grainpayapi.exceptions.CategoryNotFoundException;
import dev.deyve.grainpayapi.mappers.CategoryMapper;
import dev.deyve.grainpayapi.models.Category;
import dev.deyve.grainpayapi.models.User;
import dev.deyve.grainpayapi.repositories.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public Page<CategoryResponse> findAll(User user, Pageable pageable) {
        return categoryRepository.findAllByUserId(user.getId(), pageable)
                .map(categoryMapper::toResponse);
    }

    public CategoryResponse save(CreateCategoryRequest request, User user) {
        Category category = categoryMapper.toEntity(request);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        logger.debug("GRAIN-API: Category saved: {}", saved.getId());

        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse findById(Long id, User user) {
        Category category = categoryRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + id));

        return categoryMapper.toResponse(category);
    }

    public CategoryResponse updateById(Long id, CreateCategoryRequest request, User user) {
        Category existing = categoryRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + id));

        existing.setName(request.name());
        existing.setType(request.type());
        existing.setIcon(request.icon());
        existing.setColor(request.color());

        Category updated = categoryRepository.save(existing);
        logger.debug("GRAIN-API: Category updated: {}", updated.getId());

        return categoryMapper.toResponse(updated);
    }

    public void deleteById(Long id, User user) {
        categoryRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + id));

        logger.debug("GRAIN-API: Category deleted: {}", id);
        categoryRepository.deleteById(id);
    }
}
