package com.example.pos.repository;

import com.example.pos.dto.request.table.GetTablesRequest;
import com.example.pos.dto.response.common.PaginationOutput;
import com.example.pos.entity.table.RestaurantTable;
import com.example.pos.util.RequestHelper;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;

import java.util.UUID;

@ApplicationScoped
public class RestaurantTableRepository
        implements PanacheRepositoryBase<RestaurantTable, UUID> {

    public Uni<RestaurantTable> lockById(UUID tableId) {
        return find("id", tableId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResult();
    }

    public PanacheQuery<RestaurantTable> findAllSorted() {
        return findAll(Sort.by("status").and("tableCode"));
    }

    public Uni<PaginationOutput<RestaurantTable>> findByStatus(GetTablesRequest request) {
        var query = "SELECT r "
            + "FROM RestaurantTable "
            + "WHERE r.status = :status";

        Parameters parameters = new Parameters()
            .and("status", request.getStatus());

        var pq = find(query, parameters);
        var page = RequestHelper.toPage(request);
        return Uni
            .combine()
            .all()
            .unis(pq.page(page).list()
                , pq.count()
            )
            .with((list, count) -> {
                var out = new PaginationOutput<RestaurantTable>();
                out.setData(list);
                out.setTotal(count);
                out.setPage(page.index);
                out.setSize(request.getSize());
                return out;
            });
    }
}

