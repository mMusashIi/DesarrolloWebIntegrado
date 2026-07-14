// Crea este archivo: src/test/java/com/buganvilla/buganvillatours/SimpleTest.java
package com.buganvilla.buganvillatours;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {

    @Test
    void testSumaBasica() {
        assertEquals(5, 2 + 3);
    }

    @Test
    void testUsuarioBuilder() {
        // Probamos que nuestras entidades se crean correctamente
        var usuario = com.buganvilla.buganvillatours.model.entity.Usuario.builder()
                .nombre("Juan")
                .email("juan@test.com")
                .build();

        assertNotNull(usuario);
        assertEquals("Juan", usuario.getNombre());
    }
}
