CREATE DATABASE IF NOT EXISTS tienda_bicicletas;
USE tienda_bicicletas;

-- 1. PROVEEDOR
CREATE TABLE proveedor (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           nombre VARCHAR(100) NOT NULL,
                           telefono VARCHAR(20),
                           email VARCHAR(100),
                           frecuencia_entrega VARCHAR(50)
);

-- 2. BICICLETA
CREATE TABLE bicicleta (
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

-- 3. USUARIO (lo pongo antes para evitar errores de orden)
CREATE TABLE usuario (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(100) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',
                         activo BOOLEAN DEFAULT TRUE,
                         fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. CLIENTE
CREATE TABLE cliente (
                         documento VARCHAR(20) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         telefono VARCHAR(20),
                         email VARCHAR(100),
                         direccion VARCHAR(200)
);

-- 5. PEDIDO (con CONSTRAINT explícitos)
CREATE TABLE pedido (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        id_proveedor INT NOT NULL,
                        id_usuario INT NOT NULL,
                        fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                        estado VARCHAR(20) DEFAULT 'pendiente',
                        CONSTRAINT fk_pedido_proveedor
                            FOREIGN KEY (id_proveedor) REFERENCES proveedor(id),
                        CONSTRAINT fk_pedido_usuario
                            FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

-- 6. DETALLE_PEDIDO
CREATE TABLE detalle_pedido (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                id_pedido INT NOT NULL,
                                codigo_bicicleta INT NOT NULL,
                                cantidad INT NOT NULL,
                                precio_costo_unitario DECIMAL(10,2) NOT NULL,
                                CONSTRAINT fk_detalle_pedido_pedido
                                    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
                                CONSTRAINT fk_detalle_pedido_bicicleta
                                    FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
);

-- 7. VENTA
CREATE TABLE venta (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       documento_cliente VARCHAR(20) NOT NULL,
                       id_usuario INT NOT NULL,
                       fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                       total DECIMAL(10,2) DEFAULT 0,
                       forma_pago VARCHAR(50),
                       estado VARCHAR(20) DEFAULT 'completada',
                       CONSTRAINT fk_venta_cliente
                           FOREIGN KEY (documento_cliente) REFERENCES cliente(documento),
                       CONSTRAINT fk_venta_usuario
                           FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

-- 8. DETALLE_VENTA
CREATE TABLE detalle_venta (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               id_venta INT NOT NULL,
                               codigo_bicicleta INT NOT NULL,
                               cantidad INT NOT NULL,
                               precio_unitario DECIMAL(10,2) NOT NULL,
                               subtotal DECIMAL(10,2),
                               CONSTRAINT fk_detalle_venta_venta
                                   FOREIGN KEY (id_venta) REFERENCES venta(id),
                               CONSTRAINT fk_detalle_venta_bicicleta
                                   FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
);

-- 9. USUARIO ADMIN INICIAL
INSERT INTO usuario (nombre, email, password, rol)
VALUES (
           'Administrador',
           'admin@tienda.com',
           '$2a$10$AYXdkba1eB/LmJSVKwsjQeF/5F4T2ew1PKA6t7cYTOKspPfzgyiUm',
           'ADMIN'
       );

-- NOTAS:
-- La contraseña 'Admin123*' en BCrypt es:
-- $2a$10$AYXdkba1eB/LmJSVKwsjQeF/5F4T2ew1PKA6t7cYTOKspPfzgyiUm
-- Cambiarla en producción desde el endpoint PUT /api/usuarios/{id}

USE tienda_bicicletas;

-- Eliminar triggers si existen (para recrearlos limpios)
DROP TRIGGER IF EXISTS check_stock;
DROP TRIGGER IF EXISTS precio_automatico;
DROP TRIGGER IF EXISTS calc_subtotal;
DROP TRIGGER IF EXISTS update_stock;
DROP TRIGGER IF EXISTS update_venta_total;
DROP TRIGGER IF EXISTS actualizar_stock_pedido;
DROP TRIGGER IF EXISTS devolver_stock_cancelacion;

-- T1: Validar stock
DELIMITER //
CREATE TRIGGER check_stock
    BEFORE INSERT ON detalle_venta
    FOR EACH ROW
BEGIN
    DECLARE stock_actual INT;
    SELECT cantidad INTO stock_actual FROM bicicleta WHERE codigo = NEW.codigo_bicicleta;
    IF stock_actual < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error: Stock insuficiente';
END IF;
END; //
DELIMITER ;

-- T2: Precio automático (SIEMPRE asigna desde bicicleta)
DELIMITER //
CREATE TRIGGER precio_automatico
    BEFORE INSERT ON detalle_venta
    FOR EACH ROW
BEGIN
    SET NEW.precio_unitario = (SELECT precio_venta FROM bicicleta WHERE codigo = NEW.codigo_bicicleta);
END; //
DELIMITER ;

-- T3: Calcular subtotal
DELIMITER //
CREATE TRIGGER calc_subtotal
    BEFORE INSERT ON detalle_venta
    FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END; //
DELIMITER ;

-- T4: Descontar stock
DELIMITER //
CREATE TRIGGER update_stock
    AFTER INSERT ON detalle_venta
    FOR EACH ROW
BEGIN
    UPDATE bicicleta SET cantidad = cantidad - NEW.cantidad WHERE codigo = NEW.codigo_bicicleta;
END; //
DELIMITER ;

-- T5: Actualizar total de venta
DELIMITER //
CREATE TRIGGER update_venta_total
    AFTER INSERT ON detalle_venta
    FOR EACH ROW
BEGIN
    UPDATE venta SET total = (
        SELECT SUM(subtotal) FROM detalle_venta WHERE id_venta = NEW.id_venta
    ) WHERE id = NEW.id_venta;
END; //
DELIMITER ;

-- T6: Sumar stock al recibir pedido
DELIMITER //
CREATE TRIGGER actualizar_stock_pedido
    AFTER UPDATE ON pedido
    FOR EACH ROW
BEGIN
    IF NEW.estado = 'recibido' AND OLD.estado = 'pendiente' THEN
    UPDATE bicicleta b
        INNER JOIN detalle_pedido dp ON dp.codigo_bicicleta = b.codigo
        SET b.cantidad = b.cantidad + dp.cantidad
    WHERE dp.id_pedido = NEW.id;
END IF;
END; //
DELIMITER ;

-- T7: Devolver stock al cancelar venta
DELIMITER //
CREATE TRIGGER devolver_stock_cancelacion
    AFTER UPDATE ON venta
    FOR EACH ROW
BEGIN
    IF NEW.estado = 'devuelta' AND OLD.estado = 'completada' THEN
    UPDATE bicicleta b
        INNER JOIN detalle_venta dv ON dv.codigo_bicicleta = b.codigo
        SET b.cantidad = b.cantidad + dv.cantidad
    WHERE dv.id_venta = NEW.id;
END IF;
END; //
DELIMITER ;