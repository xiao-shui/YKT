-- 创建数据库
CREATE DATABASE IF NOT EXISTS hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE hub;

-- 产品表
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    brand VARCHAR(50),
    description TEXT,
    base_price DECIMAL(10,2),
    status VARCHAR(10) DEFAULT '上架',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_brand (brand),
    INDEX idx_status (status),
    CONSTRAINT chk_base_price CHECK (base_price > 0),
    CONSTRAINT chk_status CHECK (status IN ('上架', '下架'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 产品SKU表
CREATE TABLE IF NOT EXISTS product_skus (
    sku_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    color VARCHAR(30) NOT NULL,
    size VARCHAR(10) NOT NULL,
    cost DECIMAL(10,2),
    safety_stock INT DEFAULT 10,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_color_size (product_id, color, size),
    INDEX idx_product_id (product_id),
    INDEX idx_color (color),
    INDEX idx_size (size),
    CONSTRAINT fk_product_skus_product 
        FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE,
    CONSTRAINT chk_safety_stock CHECK (safety_stock >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id INT PRIMARY KEY AUTO_INCREMENT,
    warehouse_name VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(200),
    manager VARCHAR(50),
    capacity INT,
    used_capacity INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_warehouse_name (warehouse_name),
    CONSTRAINT chk_capacity CHECK (capacity >= 0),
    CONSTRAINT chk_used_capacity CHECK (used_capacity >= 0 AND used_capacity <= capacity),
    CONSTRAINT chk_status_values CHECK (status IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id INT NOT NULL,
    sku_id INT NOT NULL,
    quantity INT DEFAULT 0,
    allocated_qty INT DEFAULT 0,
    available_qty INT AS (quantity - allocated_qty) STORED,
    last_check_time TIMESTAMP NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_warehouse_sku (warehouse_id, sku_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_available_qty (available_qty),
    CONSTRAINT fk_inventory_warehouse 
        FOREIGN KEY (warehouse_id) 
        REFERENCES warehouses(warehouse_id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_inventory_sku 
        FOREIGN KEY (sku_id) 
        REFERENCES product_skus(sku_id) 
        ON DELETE CASCADE,
    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_allocated_qty CHECK (allocated_qty >= 0 AND allocated_qty <= quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 采购订单表
CREATE TABLE IF NOT EXISTS purchase_orders (
    po_id INT PRIMARY KEY AUTO_INCREMENT,
    po_number VARCHAR(30) NOT NULL UNIQUE,
    warehouse_id INT NOT NULL,
    total_amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT '新建',
    remark TEXT,
    create_user VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status),
    INDEX idx_po_number (po_number),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_purchase_orders_warehouse 
        FOREIGN KEY (warehouse_id) 
        REFERENCES warehouses(warehouse_id),
    CONSTRAINT chk_total_amount CHECK (total_amount >= 0),
    CONSTRAINT chk_status_values CHECK (
        status IN ('新建', '审核中', '已审核', '部分入库', '已完成', '已取消')
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 库存日志表
CREATE TABLE IF NOT EXISTS inventory_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id INT NOT NULL,
    sku_id INT NOT NULL,
    change_type VARCHAR(20) NOT NULL,
    change_qty INT NOT NULL,
    before_qty INT NOT NULL,
    after_qty INT NOT NULL,
    related_id INT,
    related_no VARCHAR(30),
    remark VARCHAR(500),
    operator VARCHAR(50),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_warehouse_sku (warehouse_id, sku_id),
    INDEX idx_create_time (create_time),
    INDEX idx_change_type (change_type),
    INDEX idx_related_info (related_id, related_no),
    CONSTRAINT fk_inventory_log_warehouse 
        FOREIGN KEY (warehouse_id) 
        REFERENCES warehouses(warehouse_id),
    CONSTRAINT fk_inventory_log_sku 
        FOREIGN KEY (sku_id) 
        REFERENCES product_skus(sku_id),
    CONSTRAINT chk_change_type CHECK (
        change_type IN ('采购入库', '退货入库', '库存调整', '库存盘点')
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建触发器：更新库存时更新product_skus的库存统计（可选）
DELIMITER $$
CREATE TRIGGER after_inventory_update
AFTER UPDATE ON inventory
FOR EACH ROW
BEGIN
    DECLARE total_stock INT;
    
    -- 计算该SKU在所有仓库的总库存
    SELECT COALESCE(SUM(quantity), 0) INTO total_stock
    FROM inventory 
    WHERE sku_id = NEW.sku_id;
    
    -- 更新product_skus表的库存数量
    UPDATE product_skus 
    SET stock_quantity = total_stock,
        update_time = CURRENT_TIMESTAMP
    WHERE sku_id = NEW.sku_id;
END$$
DELIMITER ;

-- 创建触发器：插入库存时更新product_skus的库存统计（可选）
DELIMITER $$
CREATE TRIGGER after_inventory_insert
AFTER INSERT ON inventory
FOR EACH ROW
BEGIN
    DECLARE total_stock INT;
    
    -- 计算该SKU在所有仓库的总库存
    SELECT COALESCE(SUM(quantity), 0) INTO total_stock
    FROM inventory 
    WHERE sku_id = NEW.sku_id;
    
    -- 更新product_skus表的库存数量
    UPDATE product_skus 
    SET stock_quantity = total_stock,
        update_time = CURRENT_TIMESTAMP
    WHERE sku_id = NEW.sku_id;
END$$
DELIMITER ;