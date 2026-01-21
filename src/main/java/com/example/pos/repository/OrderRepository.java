package com.example.pos.repository;

import com.example.pos.entity.order.Order;
import com.example.pos.enums.order.OrderStatus;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<Order, UUID> {
    public Uni<Order> findOpenOrderByTable(UUID tableId) {
        return find(
                "tableId = ?1 and status = ?2",
                tableId,
                OrderStatus.OPEN
        ).firstResult();
    }

    public Uni<Order> findActiveOrderByTable(UUID tableId) {
        return find(
                "tableId = ?1 and status in (?2, ?3)",
                tableId,
                OrderStatus.OPEN,
                OrderStatus.CONFIRMED
        ).firstResult();
    }

    public PanacheQuery<Order> findOrders(
            OrderStatus status,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        if (fromDate != null) {
            query.append(" and createdAt >= :fromDate");
            params.put("fromDate", fromDate.atStartOfDay());
        }

        if (toDate != null) {
            query.append(" and createdAt < :toDate");
            params.put("toDate", toDate.plusDays(1).atStartOfDay());
        }

        return find(
                query + " order by createdAt desc",
                params
        );
    }
}
