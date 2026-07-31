package com.ismael.ticketfu.service;

import com.ismael.ticketfu.entity.Category;
import com.ismael.ticketfu.exception.ResourceNotFoundException;
import com.ismael.ticketfu.repository.CategoryRepository;
import com.ismael.ticketfu.security.JWTAuthenticationFilter;
import com.ismael.ticketfu.security.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Cerebro/logica del tickets
//htoma los datos, aplica reglas y valida todo y decide que hacer
@Service
@RequiredArgsConstructor
public class CategoryService {


    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    //Le indica a Spring que busque un objeto (bean) del tipo requerido en
    // su contenedor y lo inyecte donde se necesite, evitando que tengas
    // que crear instancias manualmente usando la palabra clave

    private final CategoryRepository categoryRepository;
    //Tansactional, es que todo dentro de este metodo debe ejecutarse como una sola transaccion, si falla algo todo de nuevo
    @Transactional
    public Category create(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("La categoria ya exist");
        }
        return categoryRepository.save(category);
    }

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id está vacío");
        }
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoría con id: " + id));
    }
    @Transactional
    public Category update(Long id, Category category){
            if (id == null || category == null){
                throw  new IllegalArgumentException("Parametros nulos");
            }else {
                Category nuevo = categoryRepository.findById(id).orElseThrow(()
                        -> new ResourceNotFoundException("Categoría no encontrada"));
                nuevo.setName(category.getName());
                nuevo.setDescription(category.getDescription());
                return  categoryRepository.save(nuevo);
            }
    }
    @Transactional
    public void delete(Long id){
        if (id== null){
            throw  new IllegalArgumentException("EL id esta vacia");
        }
        if(!categoryRepository.existsById(id)){
            throw  new ResourceNotFoundException("No existe la categoria con ese ID");
        }
        categoryRepository.deleteById(id);
    }
    @Transactional
    public List<Category> createAll(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new IllegalArgumentException("La lista de categorías no puede estar vacía");
        }

        // saveAll guarda toda la lista de un solo golpe en la BD
        return categoryRepository.saveAll(categories);
    }
}
