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
                           descripcion TEXT

);

-- 4. PEDIDO
CREATE TABLE pedido (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        id_proveedor INT NOT NULL,
                        fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                        estado VARCHAR(20) DEFAULT 'pendiente',
                        FOREIGN KEY (id_proveedor) REFERENCES proveedor(id)
);

-- 5. DETALLE_PEDIDO
CREATE TABLE detalle_pedido (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                id_pedido INT NOT NULL,
                                codigo_bicicleta INT NOT NULL,
                                cantidad INT NOT NULL,
                                precio_costo_unitario DECIMAL(10,2) NOT NULL,
                                FOREIGN KEY (id_pedido) REFERENCES pedido(id),
                                FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
);

-- 6. CLIENTE
CREATE TABLE cliente (
                         documento VARCHAR(20) PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         telefono VARCHAR(20),
                         email VARCHAR(100),
                         direccion VARCHAR(200)
);

-- 7. VENTA
CREATE TABLE venta (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       documento_cliente VARCHAR(20) NOT NULL,
                       fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
                       total DECIMAL(10,2) DEFAULT 0,
                       forma_pago VARCHAR(50),
                       estado VARCHAR(20) DEFAULT 'completada',
                       FOREIGN KEY (documento_cliente) REFERENCES cliente(documento)
);

-- 8. DETALLE_VENTA
CREATE TABLE detalle_venta (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               id_venta INT NOT NULL,
                               codigo_bicicleta INT NOT NULL,
                               cantidad INT NOT NULL,
                               precio_unitario DECIMAL(10,2) NOT NULL,
                               subtotal DECIMAL(10,2),
                               FOREIGN KEY (id_venta) REFERENCES venta(id),
                               FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
);



-- 1. TABLA USUARIO
CREATE TABLE usuario (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         nombre VARCHAR(100) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         rol VARCHAR(20) NOT NULL DEFAULT 'VENDEDOR',
                         activo BOOLEAN DEFAULT TRUE,
                         fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. AGREGAR id_usuario A VENTA
ALTER TABLE venta
    ADD COLUMN id_usuario INT,
    ADD CONSTRAINT fk_venta_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id);

-- 3. AGREGAR id_usuario A PEDIDO
ALTER TABLE pedido
    ADD COLUMN id_usuario INT,
    ADD CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id);

-- 4. USUARIO ADMIN INICIAL
-- password: Admin123* (encriptada con BCrypt)
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