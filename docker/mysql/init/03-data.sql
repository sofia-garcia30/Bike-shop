USE tienda_bicicletas;

INSERT INTO cliente (documento, nombre, telefono, email, direccion)
VALUES ('123456789', 'Juan Perez', '3001234567', 'juan@email.com', 'Calle 1');

INSERT INTO bicicleta (marca, modelo, tipo, precio_venta, descripcion)
VALUES ('GW', 'MTB 29', 'Montaña', 1800000.00, 'Bicicleta de montaña');

INSERT INTO inventario (codigo_bicicleta, cantidad, stock_minimo, stock_maximo, ubicacion)
VALUES (1, 10, 5, 50, 'Bodega A');