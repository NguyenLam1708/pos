package com.example.pos.service.impl;

import com.example.pos.dto.response.OrderDetailResponse;
import com.example.pos.dto.response.OrderItemResponse;
import com.example.pos.dto.response.OrderResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.entity.inventory.InventoryReservation;
import com.example.pos.entity.order.Order;
import com.example.pos.entity.order.OrderItem;
import com.example.pos.entity.product.Product;
import com.example.pos.enums.inventory.ReservationStatus;
import com.example.pos.enums.order.OrderStatus;
import com.example.pos.enums.order.OrderItemStatus;
import com.example.pos.enums.table.TableStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.*;
import com.example.pos.service.OrderService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderItemRepository orderItemRepository;

    @Inject
    InventoryReservationRepository inventoryReservationRepository;

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    RestaurantTableRepository restaurantTableRepository;

    @Inject
    RestaurantTableRepository reservationRestaurantTableRepository;

    @Override
    @WithTransaction
    public Uni<OrderResponse> openTable(UUID tableId) {

        return reservationRestaurantTableRepository.lockById(tableId)

                .onItem().ifNull().failWith(
                        () -> new BusinessException(404, "Table not found")
                )

                .invoke(table -> {
                    if (table.getStatus() != TableStatus.AVAILABLE) {
                        throw new BusinessException(400, "Table is not available");
                    }
                    table.setStatus(TableStatus.OCCUPIED);
                })

                .flatMap(table ->
                        orderRepository.findOpenOrderByTable(tableId)
                                .flatMap(existingOrder -> {
                                    if (existingOrder != null) {
                                        return Uni.createFrom().failure(
                                                new BusinessException(409, "Table already has an open order")
                                        );
                                    }

                                    Order order = new Order();
                                    order.setTableId(tableId);
                                    order.setStatus(OrderStatus.OPEN);

                                    return orderRepository.persist(order);
                                })
                )


                .map(OrderResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<OrderResponse> confirmOrder(UUID orderId) {

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(order -> {

                    if (order.getStatus() == OrderStatus.PAID
                            || order.getStatus() == OrderStatus.CANCELLED) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Order cannot be confirmed"));
                    }

                    int currentBatch = order.getCurrentBatchNo();

                    return inventoryReservationRepository
                            .findActiveByOrderIdAndBatch(orderId, currentBatch)
                            .flatMap(reservations -> {

                                if (reservations.isEmpty()) {
                                    return Uni.createFrom()
                                            .failure(new BusinessException(400, "No items to confirm"));
                                }

                                boolean expired = reservations.stream()
                                        .anyMatch(r -> r.getExpiresAt().isBefore(LocalDateTime.now()));

                                if (expired) {
                                    return Uni.createFrom()
                                            .failure(new BusinessException(409, "Reservation expired"));
                                }

                                // 1️⃣ Confirm reservations
                                reservations.forEach(r ->
                                        r.setStatus(ReservationStatus.CONFIRMED)
                                );

                                return orderItemRepository
                                        .findOrderedItemsByOrderAndBatch(orderId, currentBatch)
                                        .flatMap(items -> {

                                            LocalDateTime now = LocalDateTime.now();

                                            long totalAmount = order.getTotalAmount();
                                            int totalQuantity = order.getTotalQuantity();

                                            // 2️⃣ Confirm items
                                            for (OrderItem item : items) {
                                                item.setStatus(OrderItemStatus.CONFIRMED);
                                                item.setConfirmedAt(now);
                                                item.setTotalPrice(
                                                        item.getUnitPrice() * item.getQuantity()
                                                );

                                                totalAmount += item.getTotalPrice();
                                                totalQuantity += item.getQuantity();
                                            }

                                            // 3️⃣ Snapshot order
                                            order.setConfirmedAt(now);
                                            order.setTotalAmount(totalAmount);
                                            order.setTotalQuantity(totalQuantity);
                                            order.setStatus(OrderStatus.CONFIRMED);
                                            order.setCurrentBatchNo(currentBatch + 1);

                                            return Uni.createFrom()
                                                    .item(OrderResponse.from(order));
                                        });
                            });
                });
    }

    @Override
    @WithTransaction
    public Uni<OrderResponse> payOrder(UUID orderId) {

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(order -> {

                    if (order.getStatus() != OrderStatus.CONFIRMED) {
                        return Uni.createFrom().failure(
                                new BusinessException(400, "Only confirmed orders can be paid")
                        );
                    }

                    order.setStatus(OrderStatus.PAID);

                    return restaurantTableRepository.findById(order.getTableId())
                            .onItem().ifNull().failWith(
                                    new BusinessException(404, "Table not found")
                            )
                            .invoke(table -> table.setStatus(TableStatus.AVAILABLE))
                            .replaceWith(order);
                })
                .map(OrderResponse::from);
    }

    @Override
    @WithTransaction
    public Uni<OrderResponse> cancelOrder(UUID orderId) {

        LocalDateTime now = LocalDateTime.now();

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(order -> {

                    if (order.getStatus() == OrderStatus.PAID
                            || order.getStatus() == OrderStatus.CANCELLED) {
                        return Uni.createFrom().failure(
                                new BusinessException(400, "Order cannot be cancelled")
                        );
                    }

                    int currentBatch = order.getCurrentBatchNo();

                    return orderItemRepository
                            .findOrderedItemsByOrderAndBatch(orderId, currentBatch)
                            .flatMap(items -> {

                                if (items.isEmpty() && currentBatch > 0) {
                                    return Uni.createFrom().failure(
                                            new BusinessException(400, "Confirmed items cannot be cancelled")
                                    );
                                }

                                items.forEach(item -> {
                                    item.setStatus(OrderItemStatus.CANCELLED);
                                    item.setCancelledAt(now);
                                });

                                return inventoryReservationRepository
                                        .findActiveByOrderIdAndBatch(orderId, currentBatch)
                                        .flatMap(this::releaseReservations)
                                        .flatMap(v -> {

                                            order.setStatus(OrderStatus.CANCELLED);
                                            order.setCancelledAt(now);

                                            return restaurantTableRepository
                                                    .findById(order.getTableId())
                                                    .invoke(table ->
                                                            table.setStatus(TableStatus.AVAILABLE)
                                                    )
                                                    .replaceWith(OrderResponse.from(order));
                                        });
                            });
                });
    }

    @Override
    public Uni<OrderDetailResponse> getOrderDetail(UUID orderId) {

        return orderRepository.findById(orderId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Order not found")
                )
                .flatMap(this::buildOrderDetail);
    }

    @Override
    public Uni<OrderDetailResponse> getActiveOrderByTable(UUID tableId) {

        return orderRepository.findActiveOrderByTable(tableId)
                .onItem().ifNull().failWith(
                        new BusinessException(404, "Table has no active order")
                )
                .flatMap(this::buildOrderDetail);
    }

    private Uni<Void> releaseReservations(List<InventoryReservation> reservations) {

        return Multi.createFrom().iterable(reservations)
                .onItem().transformToUniAndMerge(r ->
                        inventoryRepository
                                .lockByProductId(r.getProductId())
                                .invoke(inv ->
                                        inv.setAvailableQuantity(
                                                inv.getAvailableQuantity() + r.getQuantity()
                                        )
                                )
                                .invoke(() ->
                                        r.setStatus(ReservationStatus.RELEASED)
                                )
                )
                .collect().asList()
                .replaceWithVoid();
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

    @Override
    public Uni<PageResponse<OrderResponse>> getOrders(
            OrderStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        var query = orderRepository.findOrders(status, fromDate, toDate);

        Uni<Long> countUni = query.count();

        Uni<List<OrderResponse>> itemsUni =
                query.page(Page.of(safePage, safeSize))
                        .list()
                        .map(list -> list.stream()
                                .map(OrderResponse::from)
                                .toList());

        return Uni.combine().all().unis(itemsUni, countUni)
                .asTuple()
                .map(tuple -> {

                    long totalItems = tuple.getItem2();
                    int totalPages =
                            (int) Math.ceil((double) totalItems / safeSize);

                    return PageResponse.<OrderResponse>builder()
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
