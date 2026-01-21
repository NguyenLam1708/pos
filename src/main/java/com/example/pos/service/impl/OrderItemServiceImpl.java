package com.example.pos.service.impl;

import com.example.pos.dto.request.AddOrderItemRequest;
import com.example.pos.dto.response.OrderDetailResponse;
import com.example.pos.dto.response.OrderItemResponse;
import com.example.pos.entity.inventory.InventoryReservation;
import com.example.pos.entity.order.Order;
import com.example.pos.entity.order.OrderItem;
import com.example.pos.entity.product.Product;
import com.example.pos.enums.inventory.ReservationStatus;
import com.example.pos.enums.order.OrderStatus;
import com.example.pos.enums.order.OrderItemStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.*;
import com.example.pos.service.OrderItemService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderItemServiceImpl implements OrderItemService {

    @Inject OrderRepository orderRepository;
    @Inject ProductRepository productRepository;
    @Inject InventoryRepository inventoryRepository;
    @Inject OrderItemRepository orderItemRepository;
    @Inject InventoryReservationRepository inventoryReservationRepository;

    @Override
    @WithTransaction
    public Uni<OrderDetailResponse> addItem(UUID orderId, AddOrderItemRequest req) {

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(order -> {

                    if (order.getStatus() != OrderStatus.OPEN
                            && order.getStatus() != OrderStatus.CONFIRMED) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Order cannot add item"));
                    }

                    int batchNo = order.getCurrentBatchNo();
                    int qty = req.getQuantity();

                    return productRepository.findActiveById(req.getProductId())
                            .onItem().ifNull().failWith(
                                    new BusinessException(410, "Product is no longer available")
                            )
                            .flatMap(product ->
                                    inventoryRepository.lockByProductId(req.getProductId())
                                            .onItem().ifNull().failWith(
                                                    new BusinessException(404, "Inventory not found")
                                            )
                                            .flatMap(inventory -> {

                                                if (inventory.getAvailableQuantity() < qty) {
                                                    return Uni.createFrom()
                                                            .failure(new BusinessException(409, "Out of stock"));
                                                }

                                                inventory.setAvailableQuantity(
                                                        inventory.getAvailableQuantity() - qty
                                                );

                                                return orderItemRepository
                                                        .findByOrderProductBatchAndNotes(
                                                                orderId,
                                                                req.getProductId(),
                                                                batchNo,
                                                                req.getNotes()
                                                        )
                                                        .flatMap(existingItem -> {

                                                            Uni<OrderItem> itemUni;

                                                            if (existingItem != null) {
                                                                existingItem.setQuantity(
                                                                        existingItem.getQuantity() + qty
                                                                );
                                                                itemUni = orderItemRepository.persist(existingItem);
                                                            } else {
                                                                OrderItem item = new OrderItem();
                                                                item.setOrderId(orderId);
                                                                item.setProductId(req.getProductId());
                                                                item.setQuantity(qty);
                                                                item.setUnitPrice(product.getPrice());
                                                                item.setBatchNo(batchNo);
                                                                item.setNotes(req.getNotes());
                                                                itemUni = orderItemRepository.persist(item);
                                                            }

                                                            InventoryReservation r = new InventoryReservation();
                                                            r.setOrderId(orderId);
                                                            r.setProductId(req.getProductId());
                                                            r.setQuantity(qty);
                                                            r.setStatus(ReservationStatus.RESERVED);
                                                            r.setExpiresAt(LocalDateTime.now().plusMinutes(15));

                                                            return itemUni
                                                                    .flatMap(v -> inventoryReservationRepository.persist(r))
                                                                    .flatMap(v -> orderRepository.findById(orderId))
                                                                    .flatMap(this::buildOrderDetail);
                                                        });
                                            })
                            );
                });
    }

    @Override
    @WithTransaction
    public Uni<OrderDetailResponse> cancelItem(UUID orderId, UUID orderItemId) {

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(order -> {

                    if (order.getStatus() != OrderStatus.OPEN) {
                        return Uni.createFrom().failure(
                                new BusinessException(400, "Order is not open")
                        );
                    }

                    return orderItemRepository
                            .findByOrderAndId(orderId, orderItemId)
                            .onItem().ifNull().failWith(
                                    new BusinessException(404, "Order item not found")
                            )
                            .flatMap(item -> {

                                if (item.getStatus() == OrderItemStatus.CANCELLED) {
                                    return Uni.createFrom().failure(
                                            new BusinessException(400, "Item already cancelled")
                                    );
                                }

                                int qty = item.getQuantity();
                                UUID productId = item.getProductId();

                                item.setStatus(OrderItemStatus.CANCELLED);
                                item.setCancelledAt(LocalDateTime.now());

                                return inventoryRepository
                                        .lockByProductId(productId)
                                        .flatMap(inventory -> {

                                            inventory.setAvailableQuantity(
                                                    inventory.getAvailableQuantity() + qty
                                            );

                                            return inventoryReservationRepository
                                                    .findActiveByOrderAndProduct(orderId, productId)
                                                    .flatMap(reservation -> {
                                                        if (reservation != null) {
                                                            reservation.setStatus(
                                                                    ReservationStatus.RELEASED
                                                            );
                                                        }
                                                        return buildOrderDetail(order); // ✅ QUAN TRỌNG
                                                    });
                                        });
                            });
                });
    }

    private Uni<OrderDetailResponse> buildOrderDetail(Order order) {

        return orderItemRepository
                .findActiveItemsWithProduct(order.getId())
                .map(rows -> {

                    List<OrderItemResponse> items = rows.stream()
                            .map(row -> {
                                OrderItem item = (OrderItem) row[0];
                                Product product = (Product) row[1];
                                return OrderItemResponse.from(item, product);
                            })
                            .toList();

                    OrderDetailResponse res = OrderDetailResponse.from(order);

                    res.setItems(items);
                    res.setTotalItems(items.size());
                    res.setTotalQuantity(
                            items.stream()
                                    .mapToInt(OrderItemResponse::getQuantity)
                                    .sum()
                    );
                    res.setTotalAmount(
                            items.stream()
                                    .mapToLong(OrderItemResponse::getTotalPrice)
                                    .sum()
                    );

                    return res;
                });
    }
}
