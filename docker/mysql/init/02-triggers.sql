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