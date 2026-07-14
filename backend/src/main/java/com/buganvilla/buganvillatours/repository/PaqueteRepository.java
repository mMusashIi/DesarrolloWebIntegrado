package com.buganvilla.buganvillatours.repository;

import com.buganvilla.buganvillatours.model.entity.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    // Buscar paquetes por nombre
    List<Paquete> findByNombrePaqueteContainingIgnoreCase(String nombrePaquete);

    // Buscar paquetes por estado
    List<Paquete> findByEstado(String estado);

    // Buscar paquetes activos
    List<Paquete> findByEstadoOrderByNombrePaquete(String estado);

    // Buscar paquetes por rango de precio
    List<Paquete> findByPrecioBaseBetween(BigDecimal precioMin, BigDecimal precioMax);

    // Buscar paquetes por duración
    List<Paquete> findByDuracionDias(Integer duracionDias);

    // Buscar paquetes por lugar
    List<Paquete> findByLugarIdLugar(Long idLugar);

    // Buscar paquetes por nombre y estado
    List<Paquete> findByNombrePaqueteContainingIgnoreCaseAndEstado(String nombrePaquete, String estado);

    // Buscar paquetes con precio menor o igual
    List<Paquete> findByPrecioBaseLessThanEqual(BigDecimal precioMax);

    // Buscar paquetes con precio mayor o igual
    List<Paquete> findByPrecioBaseGreaterThanEqual(BigDecimal precioMin);

    // Consulta personalizada para búsqueda avanzada
    @Query("SELECT p FROM Paquete p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombrePaquete) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:precioMin IS NULL OR p.precioBase >= :precioMin) AND " +
            "(:precioMax IS NULL OR p.precioBase <= :precioMax) AND " +
            "(:estado IS NULL OR p.estado = :estado)")
    List<Paquete> buscarPaquetesConFiltros(
            @Param("nombre") String nombre,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("estado") String estado);
}
