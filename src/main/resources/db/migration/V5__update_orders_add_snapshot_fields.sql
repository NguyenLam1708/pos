-- ================================
-- Add snapshot & lifecycle fields to orders
-- ================================

ALTER TABLE orders
    ADD COLUMN total_amount BIGINT DEFAULT 0 NOT NULL,
    ADD COLUMN total_quantity INTEGER DEFAULT 0 NOT NULL,
    ADD COLUMN confirmed_at TIMESTAMP,
    ADD COLUMN cancelled_at TIMESTAMP;

-- ================================
-- Ensure status has default value
-- ================================

ALTER TABLE orders
    ALTER COLUMN status SET DEFAULT 'OPEN';
