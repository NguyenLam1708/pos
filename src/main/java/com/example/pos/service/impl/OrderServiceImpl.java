package com.example.pos.service.impl;

import com.example.pos.dto.response.OrderDetailResponse;
import com.example.pos.dto.response.OrderItemResponse;
import com.example.pos.dto.response.OrderResponse;
import com.example.pos.dto.response.PageResponse;
import com.example.pos.entity.order.Order;
import com.example.pos.entity.order.OrderItem;
import com.example.pos.entity.product.Product;
import com.example.pos.enums.inventory.ReservationStatus;
import com.example.pos.enums.order.OrderStatus;
import com.example.pos.enums.table.TableStatus;
import com.example.pos.exception.BusinessException;
import com.example.pos.repository.*;
import com.example.pos.service.OrderService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Page;
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
    RestaurantTableRepository restaurantTableRepository;

    @Inject
    ProductRepository productRepository;

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

                    if (order.getStatus() != OrderStatus.OPEN
                            && order.getStatus() != OrderStatus.CONFIRMED) {
                        return Uni.createFrom()
                                .failure(new BusinessException(400, "Order cannot add item"));
                    }

                    return inventoryReservationRepository.findByOrderId(orderId)
                            .flatMap(reservations -> {

                                if (reservations.isEmpty()) {
                                    return Uni.createFrom()
                                            .failure(new BusinessException(400, "Order has no items"));
                                }

                                boolean expired = reservations.stream()
                                        .anyMatch(r ->
                                                r.getExpiresAt().isBefore(LocalDateTime.now())
                                        );

                                if (expired) {
                                    return Uni.createFrom()
                                            .failure(new BusinessException(409, "Reservation expired"));
                                }

                                // 1️⃣ Confirm reservations
                                reservations.forEach(r ->
                                        r.setStatus(ReservationStatus.CONFIRMED)
                                );

                                // 2️⃣ Snapshot OrderItem + Order
                                return orderItemRepository.findByOrderId(orderId)
                                        .flatMap(items -> {

                                            LocalDateTime now = LocalDateTime.now();

                                            long totalAmount = 0;
                                            int totalQuantity = 0;

                                            for (OrderItem item : items) {
                                                item.setConfirmedAt(now);
                                                item.setTotalPrice(
                                                        item.getUnitPrice() * item.getQuantity()
                                                );

                                                totalAmount += item.getTotalPrice();
                                                totalQuantity += item.getQuantity();
                                            }

                                            order.setConfirmedAt(now);
                                            order.setTotalAmount(totalAmount);
                                            order.setTotalQuantity(totalQuantity);
                                            order.setStatus(OrderStatus.CONFIRMED);
                                            order.setCurrentBatchNo(order.getCurrentBatchNo() + 1);

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
