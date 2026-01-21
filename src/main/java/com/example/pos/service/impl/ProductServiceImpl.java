package com.example.pos.service.impl;

import com.example.pos.dto.request.CreateProductRequest;
import com.example.pos.dto.request.ImageUploadForm;
import com.example.pos.dto.request.UpdateProductRequest;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.dto.response.ProductResponse;
import com.example.pos.entity.product.Product;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.ProductRepository;
import com.example.pos.service.ProductService;
import com.example.pos.service.impl.image.FileStorageService;
import com.example.pos.service.impl.image.ImageResizeService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    FileStorageService fileStorageService;

    @Inject
    ImageResizeService imageResizeService;

    @Override
    @WithTransaction
    public Uni<ProductResponse> createProduct(CreateProductRequest req) {

        Product product = new Product();
        product.setName(req.getName());
        product.setCategoryId(req.getCategoryId());
        product.setPrice(req.getPrice());

        return productRepository.persist(product)
                .map(ProductResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<ProductResponse> updateProduct(UUID productId, UpdateProductRequest req) {

        return productRepository.findById(productId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Product not found")
                )
                .map(product -> {

                    if (product.isDeleted()) {
                        throw new BusinessException(400, "Product is deleted");
                    }

                    if (req.getName() != null)
                        product.setName(req.getName());

                    if (req.getCategoryId() != null)
                        product.setCategoryId(req.getCategoryId());

                    if (req.getPrice() != null)
                        product.setPrice(req.getPrice());

                    return product;
                })
                .map(ProductResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteProduct(UUID id) {
        return productRepository.findActiveById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "Product not found"))
                .flatMap(productRepository::softDelete);
    }

    @Override
    public Uni<ProductResponse> getProductById(UUID id) {
        return productRepository.findActiveById(id)
                .onItem().ifNull()
                .failWith(() -> new BusinessException(404, "Product not found"))
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

    @Override
    public Uni<ProductResponse> uploadProductImage(
            UUID productId,
            ImageUploadForm form
    ) {

        return productRepository.findById(productId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Product not found")
                )
                .flatMap(product ->

                        Uni.createFrom().item(() -> {
                                    try {

                                        byte[] original = Files.readAllBytes(
                                                form.file.uploadedFile()
                                        );

                                        byte[] imageWebp =
                                                imageResizeService.resizeToWebp(original, 1024, 1024);

                                        byte[] thumbWebp =
                                                imageResizeService.resizeToWebp(original, 300, 300);

                                        return Map.of(
                                                "image", imageWebp,
                                                "thumb", thumbWebp
                                        );

                                    } catch (IOException e) {
                                        throw new RuntimeException("Cannot read uploaded image file", e);
                                    }

                                }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool())

                                .flatMap(map ->
                                        fileStorageService.upload(
                                                        map.get("image"),
                                                        "products/" + productId + "/image.webp",
                                                        "image/webp"
                                                )
                                                .flatMap(imageUrl ->
                                                        fileStorageService.upload(
                                                                        map.get("thumb"),
                                                                        "products/" + productId + "/thumbnail.webp",
                                                                        "image/webp"
                                                                )
                                                                .invoke(thumbUrl -> {
                                                                    product.setImageUrl(imageUrl);
                                                                    product.setThumbnailUrl(thumbUrl);
                                                                })
                                                )
                                )

                                .flatMap(v ->
                                        productRepository.persist(product)
                                )
                )
                .map(ProductResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteProductImage(UUID productId) {

        return productRepository.findById(productId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Product not found")
                )
                .invoke(product -> {
                    product.setImageUrl(null);
                    product.setThumbnailUrl(null);
                })
                .replaceWithVoid();
    }


}
