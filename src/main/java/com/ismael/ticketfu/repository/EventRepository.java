package com.ismael.ticketfu.repository;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByName(String name);


    List<Event> findByCategoryId(Long categoryId);

    // Candado Pesimista a nivel de Base de Datos
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    //Bloque al aDB hasta que la transaccion guarde los datos o haga rollback

    Optional<Event> findByIdWithLock(@Param("id") Long id);
}