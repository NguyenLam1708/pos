-- =========================================================
-- V3 - INSERT INVENTORY + RESTAURANT TABLES
-- Database: PostgreSQL
-- =========================================================

-- =====================
-- INVENTORY
-- =====================

-- Tạo unique index để đảm bảo 1 product = 1 inventory
CREATE UNIQUE INDEX IF NOT EXISTS uk_inventory_product
    ON inventory (product_id);

-- Insert inventory cho product chưa có inventory
INSERT INTO inventory (id, product_id, total_quantity, available_quantity)
SELECT
    gen_random_uuid(),
    p.id,
    100,    -- total_quantity
    100     -- available_quantity
FROM products p
WHERE NOT EXISTS (
    SELECT 1
    FROM inventory i
    WHERE i.product_id = p.id
);

-- Đảm bảo table_code unique
CREATE UNIQUE INDEX IF NOT EXISTS uk_restaurant_table_code
    ON restaurant_tables (table_code);

INSERT INTO restaurant_tables (id, table_code, capacity, status) VALUES
                                                                     (gen_random_uuid(), 'T01', 2, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T02', 2, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T03', 4, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T04', 4, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T05', 4, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T06', 6, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T07', 6, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T08', 8, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'T09', 8, 'AVAILABLE'),
                                                                     (gen_random_uuid(), 'VIP01', 10, 'AVAILABLE')
ON CONFLICT (table_code) DO NOTHING;

