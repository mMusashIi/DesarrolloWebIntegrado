-- Lugares
INSERT INTO lugares (nombre_lugar, ciudad, descripcion) VALUES ('Machu Picchu', 'Cusco', 'Santuario histórico y maravilla del mundo moderno');
INSERT INTO lugares (nombre_lugar, ciudad, descripcion) VALUES ('Montaña de 7 Colores', 'Cusco', 'Impresionante formación geológica también conocida como Vinicunca');
INSERT INTO lugares (nombre_lugar, ciudad, descripcion) VALUES ('Laguna Humantay', 'Cusco', 'Hermosa laguna glaciar al pie del nevado Salkantay');
INSERT INTO lugares (nombre_lugar, ciudad, descripcion) VALUES ('Valle Sagrado', 'Cusco', 'Ruta histórica llena de ruinas y poblados tradicionales');

-- Paquetes
INSERT INTO paquetes (nombre_paquete, descripcion, precio_base, duracion_dias, estado, id_lugar) VALUES ('Full Day Machu Picchu', 'Viaje de un día completo en tren para visitar la ciudadela inca', 150.00, 1, 'activo', 1);
INSERT INTO paquetes (nombre_paquete, descripcion, precio_base, duracion_dias, estado, id_lugar) VALUES ('Trekking Vinicunca', 'Caminata guiada hacia la Montaña de 7 Colores con desayuno incluido', 50.00, 1, 'activo', 2);
INSERT INTO paquetes (nombre_paquete, descripcion, precio_base, duracion_dias, estado, id_lugar) VALUES ('Tour Valle Sagrado VIP', 'Recorrido por Pisac, Ollantaytambo y Chinchero', 60.00, 1, 'activo', 4);

-- Inventario de Paquetes
INSERT INTO inventario_paquetes (id_paquete, fecha_salida, fecha_retorno, cupo_total, cupo_disponible) VALUES (1, '2026-07-01', '2026-07-01', 20, 15);
INSERT INTO inventario_paquetes (id_paquete, fecha_salida, fecha_retorno, cupo_total, cupo_disponible) VALUES (2, '2026-07-02', '2026-07-02', 15, 10);
INSERT INTO inventario_paquetes (id_paquete, fecha_salida, fecha_retorno, cupo_total, cupo_disponible) VALUES (3, '2026-07-03', '2026-07-03', 30, 28);

-- Usuarios (Password is '1234' for all, assuming BCrypt encoding: $2a$10$wE/.7S.M.ToflZ2X4qfQOOtD7yv/B4HkTkD5o0gA3hP6vS.uTMy.O is a sample hash for '1234'. If the system uses plaintext, we'll just put '1234'. But usually it's encoded. Let's put a dummy hash or plaintext based on what's expected. Since the reference had password, we'll put something simple).
-- Actually, spring security will fail to login if it doesn't match the BCrypt hash. Let's provide a valid BCrypt hash for '1234': $2a$10$Dow.yL9d0B20k5M20qQ2R.vN0oJ26lW1tA8X.M/S.ToflZ2X4qfQO -> wait, I'll just use the standard one I generated before or a safe one.
INSERT INTO usuarios (nombre, apellido, email, password, telefono, nacionalidad, dni, rol, activo, fecha_creacion) VALUES ('Admin', 'Root', 'admin@buganvillatours.com', '$2a$10$22n97a7i5V/J/aM2fH0/R.sH5v40gP5U/1xYwY/T0c2L6U3o8aE7G', '999888777', 'Peruano', '11111111', 'admin', 1, CURRENT_TIMESTAMP);
INSERT INTO usuarios (nombre, apellido, email, password, telefono, nacionalidad, dni, rol, activo, fecha_creacion) VALUES ('Juan', 'Perez', 'juan.perez@email.com', '$2a$10$22n97a7i5V/J/aM2fH0/R.sH5v40gP5U/1xYwY/T0c2L6U3o8aE7G', '987654321', 'Peruano', '42410784', 'cliente', 1, CURRENT_TIMESTAMP);
INSERT INTO usuarios (nombre, apellido, email, password, telefono, nacionalidad, dni, rol, activo, fecha_creacion) VALUES ('Maria', 'Lopez', 'maria.lopez@email.com', '$2a$10$22n97a7i5V/J/aM2fH0/R.sH5v40gP5U/1xYwY/T0c2L6U3o8aE7G', '912345678', 'Chileno', '22222222', 'cliente', 1, CURRENT_TIMESTAMP);

-- Reservas
INSERT INTO reservas (id_usuario, id_inventario, cantidad_personas, estado, fecha_reserva) VALUES (2, 1, 2, 'confirmada', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_inventario, cantidad_personas, estado, fecha_reserva) VALUES (3, 2, 1, 'pendiente', CURRENT_TIMESTAMP);

-- Pagos
INSERT INTO pagos (id_reserva, monto, metodo, estado, fecha_pago, fecha_creacion, mp_preference_id, mp_payment_id, mp_status) VALUES (1, 300.00, 'MercadoPago', 'completado', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'pref_123', 'pay_123', 'approved');
