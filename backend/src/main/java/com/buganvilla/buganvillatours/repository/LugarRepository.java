package com.buganvilla.buganvillatours.repository;

import com.buganvilla.buganvillatours.model.entity.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {

    // Buscar lugar por nombre exacto
    Optional<Lugar> findByNombreLugar(String nombreLugar);

    // Buscar lugares por ciudad
    List<Lugar> findByCiudad(String ciudad);

    // Buscar lugares por nombre (case insensitive)
    List<Lugar> findByNombreLugarContainingIgnoreCase(String nombreLugar);

    // Buscar lugares por ciudad (case insensitive)
    List<Lugar> findByCiudadContainingIgnoreCase(String ciudad);

    // Buscar lugares por nombre o ciudad
    @Query("SELECT l FROM Lugar l WHERE LOWER(l.nombreLugar) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(l.ciudad) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Lugar> findByNombreLugarOrCiudadContainingIgnoreCase(@Param("termino") String termino);

    // Verificar si existe un lugar por nombre
    boolean existsByNombreLugar(String nombreLugar);
}
