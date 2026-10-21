CREATE TABLE app_users (id BIGSERIAL PRIMARY KEY,name VARCHAR(100) NOT NULL,email VARCHAR(190) NOT NULL UNIQUE,password_hash VARCHAR(255) NOT NULL,role VARCHAR(30) NOT NULL,enabled BOOLEAN NOT NULL DEFAULT TRUE);
CREATE TABLE api_tokens (hash VARCHAR(64) PRIMARY KEY,user_id BIGINT NOT NULL REFERENCES app_users(id),expires_at TIMESTAMPTZ NOT NULL);
CREATE TABLE menu_items (id BIGSERIAL PRIMARY KEY,restaurant_id BIGINT NOT NULL REFERENCES app_users(id),name VARCHAR(110) NOT NULL,category VARCHAR(40) NOT NULL,description VARCHAR(500) NOT NULL,emoji VARCHAR(10) NOT NULL,price NUMERIC(10,2) NOT NULL,available BOOLEAN NOT NULL DEFAULT TRUE);
CREATE TABLE customer_orders (id BIGSERIAL PRIMARY KEY,version BIGINT NOT NULL DEFAULT 0,customer_id BIGINT NOT NULL REFERENCES app_users(id),restaurant_id BIGINT NOT NULL REFERENCES app_users(id),driver_id BIGINT REFERENCES app_users(id),status VARCHAR(30) NOT NULL,address VARCHAR(250) NOT NULL,notes VARCHAR(500) NOT NULL,total NUMERIC(10,2) NOT NULL,created_at TIMESTAMPTZ NOT NULL);
CREATE TABLE order_lines (id BIGSERIAL PRIMARY KEY,order_id BIGINT NOT NULL REFERENCES customer_orders(id),menu_item_id BIGINT,name VARCHAR(110) NOT NULL,quantity INTEGER NOT NULL,unit_price NUMERIC(10,2) NOT NULL);
CREATE INDEX idx_order_customer ON customer_orders(customer_id,created_at DESC);
CREATE INDEX idx_order_restaurant ON customer_orders(restaurant_id,created_at DESC);
CREATE INDEX idx_order_driver ON customer_orders(driver_id,created_at DESC);
CREATE INDEX idx_order_status ON customer_orders(status);
