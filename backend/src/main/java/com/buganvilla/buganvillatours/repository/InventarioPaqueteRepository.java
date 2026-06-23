package com.buganvilla.buganvillatours.repository;

import com.buganvilla.buganvillatours.model.entity.InventarioPaquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioPaqueteRepository extends JpaRepository<InventarioPaquete, Long> {

    // Buscar inventario por paquete
    List<InventarioPaquete> findByPaqueteIdPaquete(Long idPaquete);

    // Buscar inventario por fecha de salida
    List<InventarioPaquete> findByFechaSalida(LocalDate fechaSalida);

    // Buscar inventario por rango de fechas
    List<InventarioPaquete> findByFechaSalidaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Buscar inventario con cupo disponible
    List<InventarioPaquete> findByCupoDisponibleGreaterThan(Integer cupoMinimo);

    // Buscar inventario por paquete y fecha
    Optional<InventarioPaquete> findByPaqueteIdPaqueteAndFechaSalida(Long idPaquete, LocalDate fechaSalida);

    // Buscar inventario disponible por paquete
    @Query("SELECT ip FROM InventarioPaquete ip WHERE ip.paquete.idPaquete = :idPaquete AND ip.cupoDisponible > 0 AND ip.fechaSalida >= CURRENT_DATE")
    List<InventarioPaquete> findInventarioDisponibleByPaquete(@Param("idPaquete") Long idPaquete);

    // Buscar inventario disponible por fechas
    @Query("SELECT ip FROM InventarioPaquete ip WHERE ip.cupoDisponible > 0 AND ip.fechaSalida BETWEEN :fechaInicio AND :fechaFin")
    List<InventarioPaquete> findInventarioDisponibleByFechas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // Verificar disponibilidad
    @Query("SELECT ip.cupoDisponible FROM InventarioPaquete ip WHERE ip.idInventario = :idInventario")
    Integer findCupoDisponibleById(@Param("idInventario") Long idInventario);

    // Buscar próximas salidas con cupo disponible
    @Query("SELECT ip FROM InventarioPaquete ip WHERE ip.cupoDisponible > 0 AND ip.fechaSalida >= CURRENT_DATE ORDER BY ip.fechaSalida ASC")
    List<InventarioPaquete> findProximasSalidasDisponibles();
}

