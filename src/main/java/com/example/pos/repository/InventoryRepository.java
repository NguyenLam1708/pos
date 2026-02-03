package com.example.pos.repository;

import com.example.pos.dto.request.GetInventoriesRequest;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.inventory.Inventory;
import com.example.pos.util.RequestHelper;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.persistence.LockModeType;
import java.util.UUID;

@ApplicationScoped
public class InventoryRepository implements PanacheRepositoryBase<Inventory, UUID> {

    public Uni<Inventory> lockByProductId(UUID productId) {
        return find("productId", productId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }

    public Uni<PaginationOutput<Inventory>> findInventories(GetInventoriesRequest request) {

        String query;
        Parameters params = new Parameters();

        if (request.getProductId() != null) {
            query = "productId = :productId";
            params.and("productId", request.getProductId());
        } else {
            query = "1=1";
        }

        query += " order by id desc";

        var pq = find(query, params);
        var page = RequestHelper.toPage(request);

        return Uni.combine().all().unis(
                pq.page(page).list(),
                pq.count()
        ).with((list, count) -> {
            var out = new PaginationOutput<Inventory>();
            out.setData(list);
            out.setTotal(count);
            out.setPage(page.index);
            out.setSize(request.getSize());
            return out;
        });
    }

}
