CREATE TABLE cliente (
    documento VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(200)
);

CREATE TABLE bicicleta (
    codigo INT PRIMARY KEY AUTO_INCREMENT,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    tipo VARCHAR(50),
    precio_venta DECIMAL(10,2) NOT NULL,
    descripcion TEXT,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventario (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_bicicleta INT NOT NULL,
    cantidad INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    stock_maximo INT DEFAULT 50,
    ubicacion VARCHAR(50),
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo),
    UNIQUE KEY (codigo_bicicleta)
);

CREATE TABLE venta (
    id INT PRIMARY KEY AUTO_INCREMENT,
    documento_cliente VARCHAR(20) NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,
    forma_pago VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'completada',
    FOREIGN KEY (documento_cliente) REFERENCES cliente(documento)
);

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

CREATE TABLE alertas_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_bicicleta INT NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (codigo_bicicleta) REFERENCES bicicleta(codigo)
);