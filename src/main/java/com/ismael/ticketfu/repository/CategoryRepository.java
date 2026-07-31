package com.ismael.ticketfu.repository;

import com.ismael.ticketfu.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsById(Long id);
}
