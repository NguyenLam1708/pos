package com.example.pos.service.impl;

import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import com.example.pos.exception.BusinessException;
import com.example.pos.reponsitory.ProductRepository;
import com.example.pos.service.ProductService;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    @Inject
    ProductRepository productRepository;

    @Override
    public Uni<ProductResponse> getProductById(UUID id) {
        return productRepository.findById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "Product not found"))
                .map(ProductResponse::from);
    }

    @Override
    public Uni<PageResponse<ProductResponse>> getProducts(int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = productRepository.findAll();

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
                    int totalPages = (int) Math.ceil((double) totalItems / safeSize);

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
    }
}
