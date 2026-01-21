package com.example.pos.service.impl;

import com.example.pos.dto.response.CategoryResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.CategoryRepository;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.CategoryService;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CategoryServiceImpl implements CategoryService {
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    ProductRepository productRepository;

    @Override
    public Uni<PageResponse<CategoryResponse>> getCategories(int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = categoryRepository.findAll();

        Uni<Long> countUni = query.count();

        Uni<List<CategoryResponse>> itemsUni =
                query.page(Page.of(safePage, safeSize))
                        .list()
                        .map(list -> list.stream()
                                .map(CategoryResponse::from)
                                .toList());

        return Uni.combine().all().unis(itemsUni, countUni)
                .asTuple()
                .map(tuple -> {
                    long totalItems = tuple.getItem2();
                    int totalPages = (int) Math.ceil((double) totalItems / safeSize);

                    return PageResponse.<CategoryResponse>builder()
                            .items(tuple.getItem1())
                            .page(safePage)
                            .size(safeSize)
                            .totalItems(totalItems)
                            .totalPages(totalPages)
                            .hasNext(safePage < totalPages - 1)
                            .hasPrevious(safePage > 0)
                            .build();
                });
    }

    @Override
    public Uni<PageResponse<ProductResponse>> getProductsByCategoryId(
            UUID categoryId,
            int page,
            int size
    ) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        // 1️⃣ Check category tồn tại
        return categoryRepository.findById(categoryId)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "Category not found"))
                .flatMap(category -> {

                    var query = productRepository.find(
                            "categoryId = ?1",  // đúng field trong entity
                            categoryId
                    );

                    Uni<Long> countUni = query.count();

                    Uni<List<ProductResponse>> itemsUni =
                            query.page(Page.of(safePage, safeSize))
                                    .list()
                                    .map(list -> list.stream()
                                            .map(ProductResponse::from)
                                            .toList());

                    return Uni.combine().all().unis(itemsUni, countUni)
                            .asTuple()
                            .map(tuple -> {
                                long totalItems = tuple.getItem2();
                                int totalPages =
                                        (int) Math.ceil((double) totalItems / safeSize);

                                return PageResponse.<ProductResponse>builder()
                                        .items(tuple.getItem1())
                                        .page(safePage)
                                        .size(safeSize)
                                        .totalItems(totalItems)
                                        .totalPages(totalPages)
                                        .hasNext(safePage < totalPages - 1)
                                        .hasPrevious(safePage > 0)
                                        .build();
                            });
                });
    }

}
