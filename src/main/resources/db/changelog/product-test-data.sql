-- Product test data
INSERT INTO product (id, description) VALUES (1, 'Mechanical Keyboard');
INSERT INTO product (id, description) VALUES (2, 'Wireless Mouse');
INSERT INTO product (id, description) VALUES (3, '27-inch Monitor');
INSERT INTO product (id, description) VALUES (4, 'USB-C Docking Station');
INSERT INTO product (id, description) VALUES (5, 'Noise Cancelling Headset');

-- Order-Product test relations
INSERT INTO order_product (order_id, product_id) VALUES (1, 1);
INSERT INTO order_product (order_id, product_id) VALUES (1, 2);
INSERT INTO order_product (order_id, product_id) VALUES (2, 3);
INSERT INTO order_product (order_id, product_id) VALUES (3, 4);
INSERT INTO order_product (order_id, product_id) VALUES (3, 5);
INSERT INTO order_product (order_id, product_id) VALUES (4, 1);
INSERT INTO order_product (order_id, product_id) VALUES (4, 3);
INSERT INTO order_product (order_id, product_id) VALUES (5, 2);
INSERT INTO order_product (order_id, product_id) VALUES (5, 5);
