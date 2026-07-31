package com.ismael.ticketfu.controller;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/categories")
@RequiredArgsConstructor

public class CategoryController {


    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAll();
        return  ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    //PostMapping sirve para manejar peticiones HTTP Post, CREAR NUEVOS
    //RECRUSOS Y RECIBIR DATOS EN EL CURSO DE LA SOLICITUD
    @PostMapping
    //RequestBody agarra los datos en formado JSON y los pasa  Objeto java
    public  ResponseEntity<Category> createCategory(@Valid @RequestBody Category category){
        Category newCategory = categoryService.create(category);
        //
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }



    @PostMapping("/bulk")
    public  ResponseEntity<List<Category>>  createCategories(@RequestBody List<Category> categories){
        List<Category> newCategories = categoryService.createAll(categories);
        return   ResponseEntity.status(HttpStatus.CREATED).body(newCategories);
    }

    //PUT es para actualizar o reemplazar un recurso que ya existe en el server
    @PutMapping("/{id}")
    public ResponseEntity<Category> putCategory(@PathVariable Long id, @RequestBody Category category){
        Category newCategory = categoryService.update(id, category);
        return ResponseEntity.ok(newCategory);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        Category temp = categoryService.getById(id);
        categoryService.delete(id);
        return ResponseEntity.ok(temp);
    }

}
