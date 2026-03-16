USE tienda_bicicletas;

DELIMITER //
CREATE TRIGGER check_stock BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    DECLARE stock_actual INT;

    SELECT cantidad INTO stock_actual
    FROM inventario
    WHERE codigo_bicicleta = NEW.codigo_bicicleta;

    IF stock_actual < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: No hay suficiente stock para esta venta';
    END IF;
END; //
DELIMITER ;

DELIMITER //
CREATE TRIGGER precio_automatico BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    DECLARE precio_bici DECIMAL(10,2);

    IF NEW.precio_unitario IS NULL OR NEW.precio_unitario = 0 THEN
        SELECT precio_venta INTO precio_bici
        FROM bicicleta
        WHERE codigo = NEW.codigo_bicicleta;

        SET NEW.precio_unitario = precio_bici;
    END IF;
END; //
DELIMITER ;

DELIMITER //
CREATE TRIGGER calc_subtotal BEFORE INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END; //
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_stock AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE inventario
    SET cantidad = cantidad - NEW.cantidad
    WHERE codigo_bicicleta = NEW.codigo_bicicleta;
END; //
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_venta_total AFTER INSERT ON detalle_venta
FOR EACH ROW
BEGIN
    UPDATE venta
    SET total = (
        SELECT COALESCE(SUM(subtotal), 0)
        FROM detalle_venta
        WHERE id_venta = NEW.id_venta
    )
    WHERE id = NEW.id_venta;
END; //
DELIMITER ;

DELIMITER //
CREATE TRIGGER alerta_stock_bajo AFTER UPDATE ON inventario
FOR EACH ROW
BEGIN
    IF NEW.cantidad <= NEW.stock_minimo AND OLD.cantidad > OLD.stock_minimo THEN
        INSERT INTO alertas_stock (codigo_bicicleta, mensaje) VALUES
        (NEW.codigo_bicicleta,
         CONCAT('⚠️ Stock bajo: Bicicleta ',
                (SELECT CONCAT(marca, ' ', modelo) FROM bicicleta WHERE codigo = NEW.codigo_bicicleta),
                ' - Quedan ', NEW.cantidad, ' unidades (mínimo ', NEW.stock_minimo, ')'));
    END IF;

    IF NEW.cantidad = 0 AND OLD.cantidad > 0 THEN
        INSERT INTO alertas_stock (codigo_bicicleta, mensaje) VALUES
        (NEW.codigo_bicicleta,
         CONCAT('🔴 SIN STOCK: ',
                (SELECT CONCAT(marca, ' ', modelo) FROM bicicleta WHERE codigo = NEW.codigo_bicicleta),
                ' - Producto agotado'));
    END IF;
END; //
DELIMITER ;