
SET NAMES utf8mb4;
USE tienda_bicicletas;


-- Cliente de prueba
INSERT INTO cliente (documento, nombre, telefono, email, direccion)
VALUES ('123456789', 'Juan Perez', '3001234567', 'juan@email.com', 'Calle 1');

-- Proveedor de prueba
INSERT INTO proveedor (nombre, telefono, email, frecuencia_entrega)
VALUES ('BikeCorp Colombia', '3001234567', 'ventas@bikecorp.com', 'mensual');

-- Bicicleta de prueba
INSERT INTO bicicleta (marca, modelo, tipo, precio_costo, precio_venta, cantidad, stock_minimo, stock_maximo, descripcion)
VALUES ('GW', 'MTB 29', 'Montaña', 1200000.00, 1800000.00, 10, 5, 50, 'Bicicleta de montaña');