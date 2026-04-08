-- ==========================================
-- 1. PROVEEDOR
-- ==========================================
CREATE TABLE IF NOT EXISTS proveedor (
                                         id INT PRIMARY KEY AUTO_INCREMENT,
                                         nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    frecuencia_entrega VARCHAR(50)
    );

-- ==========================================
-- 2. BICICLETA
-- ==========================================
CREATE TABLE IF NOT EXISTS bicicleta (
                                         codigo INT PRIMARY KEY AUTO_INCREMENT,
                                         marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    tipo VARCHAR(50),
    precio_costo DECIMAL(10,2) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    cantidad INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    stock_maximo INT DEFAULT 50,
    descripcion TEXT,
    imagen_url VARCHAR(500)
    );

-- ==========================================
-- 3. USUARIO
-- ==========================================
CREATE TABLE IF NOT EXISTS usuario (
                                       id INT PRIMARY KEY AUTO_INCREMENT,
                                       nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
    );

-- ==========================================
-- 4. CLIENTE
-- ==========================================
CREATE TABLE IF NOT EXISTS cliente (
                                       documento VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(200)
    );

-- ==========================================
-- 5. PEDIDO
-- ==========================================
CREATE TABLE IF NOT EXISTS pedido (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      id_proveedor INT NOT NULL,
                                      id_usuario INT NOT NULL,
                                      fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                                      estado VARCHAR(20) DEFAULT 'pendiente',
    CONSTRAINT fk_pedido_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)
    );

-- ==========================================
-- 6. DETALLE_PEDIDO
-- ==========================================
CREATE TABLE IF NOT EXISTS detalle_pedido (
                                              id INT PRIMARY KEY AUTO_INCREMENT,
                                              id_pedido INT NOT NULL,
                                              codigo_bicicleta INT NOT NULL,
                                              cantidad INT NOT NULL,
                                              precio_costo_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    CONSTRAINT fk_detalle_pedido_bicicleta FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
    );

-- ==========================================
-- 7. VENTA
-- ==========================================
CREATE TABLE IF NOT EXISTS venta (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     documento_cliente VARCHAR(20) NOT NULL,
    id_usuario INT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,
    forma_pago VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'completada',
    CONSTRAINT fk_venta_cliente FOREIGN KEY (documento_cliente) REFERENCES cliente(documento),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id)
    );

-- ==========================================
-- 8. DETALLE_VENTA
-- ==========================================
CREATE TABLE IF NOT EXISTS detalle_venta (
                                             id INT PRIMARY KEY AUTO_INCREMENT,
                                             id_venta INT NOT NULL,
                                             codigo_bicicleta INT NOT NULL,
                                             cantidad INT NOT NULL,
                                             precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2),
    CONSTRAINT fk_detalle_venta_venta FOREIGN KEY (id_venta) REFERENCES venta(id),
    CONSTRAINT fk_detalle_venta_bicicleta FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
    );

-- ==========================================
-- 9. USUARIO ADMIN INICIAL
-- ==========================================
    INSERT IGNORE INTO usuario (nombre, email, password, rol) VALUES ('Administrador', 'admin@tienda.com', '$2a$10$AYXdkba1eB/LmJSVKwsjQeF/5F4T2ew1PKA6t7cYTOKspPfzgyiUm', 'ADMIN')$$
