package com.example.pos.service.impl;

import com.example.pos.dto.request.CreateProductRequest;
import com.example.pos.dto.request.UpdateProductRequest;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import com.example.pos.entity.inventory.Inventory;
import com.example.pos.entity.product.Product;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.InventoryRepository;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.ProductService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
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

    @Inject
    InventoryRepository inventoryRepository;

    @Override
    @WithTransaction
    public Uni<ProductResponse> createProduct(CreateProductRequest req) {

        Product product = new Product();
        product.setName(req.getName());
        product.setCategoryId(req.getCategoryId());
        product.setPrice(req.getPrice());
        product.setImageUrl(null);

        return productRepository.persist(product)
                .flatMap(p -> {

                    Inventory inventory = new Inventory();
                    inventory.setProductId(p.getId());
                    inventory.setTotalQuantity(0);
                    inventory.setAvailableQuantity(0);

                    return inventoryRepository.persist(inventory)
                            .replaceWith(p);
                })
                .map(ProductResponse::from);
    }


    @Override
    @WithTransaction
    public Uni<ProductResponse> updateProduct(UUID productId, UpdateProductRequest req) {

        return productRepository.findById(productId)
                .onItem().ifNull()
                .failWith(new BusinessException(404, "Product not found"))
                .invoke(product -> {

                    if (product.isDeleted()) {
                        throw new BusinessException(400, "Product is deleted");
                    }

                    if (req.getName() != null) {
                        product.setName(req.getName());
                    }

                    if (req.getCategoryId() != null) {
                        product.setCategoryId(req.getCategoryId());
                    }

                    if (req.getPrice() != null) {
                        product.setPrice(req.getPrice());
                    }
                })
                .map(ProductResponse::from);
    }


    @Override
    @WithTransaction
    public Uni<ProductResponse> updateProductImage(UUID productId, String imageUrl) {

        return productRepository.findById(productId)
                .onItem().ifNull()
                .failWith(new BusinessException(404, "Product not found"))
                .invoke(product -> product.setImageUrl(imageUrl))
                .map(ProductResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteProductImage(UUID productId) {

        return productRepository.findById(productId)
                .onItem().ifNull()
                .failWith(new BusinessException(404, "Product not found"))
                .invoke(product -> product.setImageUrl(null))
                .replaceWithVoid();
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteProduct(UUID productId) {

        return productRepository.findActiveById(productId)
                .onItem().ifNull()
                .failWith(new BusinessException(404, "Product not found"))
                .flatMap(productRepository::softDelete);
    }

    @Override
    public Uni<ProductResponse> getProductById(UUID productId) {

        return productRepository.findActiveById(productId)
                .onItem().ifNull()
                .failWith(new BusinessException(404, "Product not found"))
                .map(ProductResponse::from);
    }

    @Override
    public Uni<PageResponse<ProductResponse>> getProducts(int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = productRepository.findActive();

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
