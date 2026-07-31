package com.ismael.ticketfu.controller;



import com.ismael.ticketfu.entity.Event;
import com.ismael.ticketfu.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/events")
@RequiredArgsConstructor

public class EventController {


    private final EventService eventService;


    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(){
        List<Event> events = eventService.getAll();
        return  ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getById(id);
        return ResponseEntity.ok(event);
    }

    //PostMapping sirve para manejar peticiones HTTP Post, CREAR NUEVOS
    //RECRUSOS Y RECIBIR DATOS EN EL CURSO DE LA SOLICITUD
    @PostMapping
    //RequestBody agarra los datos en formado JSON y los pasa  Objeto java
    public  ResponseEntity<Event> createEvent(@Valid @RequestBody Event event){
        Event newEvent = eventService.create(event);
        //
        return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
    }



    @PostMapping("/bulk")
    public  ResponseEntity<List<Event>>  createEvents(@RequestBody List<Event> events){
        List<Event> newEvents = eventService.createAll(events);
        return   ResponseEntity.status(HttpStatus.CREATED).body(newEvents);
    }

    //PUT es para actualizar o reemplazar un recurso que ya existe en el server
    @PutMapping("/{id}")
    public ResponseEntity<Event> putEvent(@PathVariable Long id, @Valid @RequestBody Event event){
        Event newevent = eventService.update(id, event);
        return ResponseEntity.ok(newevent);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Event> deleteEevent(@PathVariable Long id) {
        Event temp = eventService.getById(id);
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
