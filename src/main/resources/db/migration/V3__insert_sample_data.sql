-- ===================== CATEGORY =====================
INSERT INTO categories (id, name) VALUES
                                      ('11111111-1111-1111-1111-111111111111', 'Food'),
                                      ('22222222-2222-2222-2222-222222222222', 'Drink')
ON CONFLICT (id) DO NOTHING;

-- ===================== PRODUCT (FOOD) =====================
INSERT INTO products (id, name, category_id, price) VALUES
                                                        (gen_random_uuid(), 'Fried Chicken', '11111111-1111-1111-1111-111111111111', 35000),
                                                        (gen_random_uuid(), 'Cheeseburger', '11111111-1111-1111-1111-111111111111', 45000),
                                                        (gen_random_uuid(), 'Pizza Margherita', '11111111-1111-1111-1111-111111111111', 120000),
                                                        (gen_random_uuid(), 'Spaghetti Bolognese', '11111111-1111-1111-1111-111111111111', 90000),
                                                        (gen_random_uuid(), 'Grilled Pork Rice', '11111111-1111-1111-1111-111111111111', 40000),
                                                        (gen_random_uuid(), 'Beef Steak', '11111111-1111-1111-1111-111111111111', 180000),
                                                        (gen_random_uuid(), 'Seafood Fried Rice', '11111111-1111-1111-1111-111111111111', 70000),
                                                        (gen_random_uuid(), 'French Fries', '11111111-1111-1111-1111-111111111111', 30000),
                                                        (gen_random_uuid(), 'Chicken Wings', '11111111-1111-1111-1111-111111111111', 60000),
                                                        (gen_random_uuid(), 'Hot Dog', '11111111-1111-1111-1111-111111111111', 35000);

-- ===================== PRODUCT (DRINK) =====================
INSERT INTO products (id, name, category_id, price) VALUES
                                                        (gen_random_uuid(), 'Coca Cola', '22222222-2222-2222-2222-222222222222', 15000),
                                                        (gen_random_uuid(), 'Pepsi', '22222222-2222-2222-2222-222222222222', 15000),
                                                        (gen_random_uuid(), 'Orange Juice', '22222222-2222-2222-2222-222222222222', 25000),
                                                        (gen_random_uuid(), 'Lemon Tea', '22222222-2222-2222-2222-222222222222', 20000),
                                                        (gen_random_uuid(), 'Milk Tea', '22222222-2222-2222-2222-222222222222', 30000),
                                                        (gen_random_uuid(), 'Black Coffee', '22222222-2222-2222-2222-222222222222', 20000),
                                                        (gen_random_uuid(), 'Latte', '22222222-2222-2222-2222-222222222222', 30000),
                                                        (gen_random_uuid(), 'Cappuccino', '22222222-2222-2222-2222-222222222222', 35000),
                                                        (gen_random_uuid(), 'Green Tea', '22222222-2222-2222-2222-222222222222', 18000),
                                                        (gen_random_uuid(), 'Mineral Water', '22222222-2222-2222-2222-222222222222', 10000);
