package com.example.PegaAssessment.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PegaAssessment.model.ReadingListItem;
import com.example.PegaAssessment.repository.ReadingListItemRepository;

import java.util.List;

@RestController
public class Controller {

    private final ReadingListItemRepository service;

    public Controller(ReadingListItemRepository service) {
        this.service = service;
    }


    @GetMapping("/getAll")
    public List<ReadingListItem> getAll() {
        return service.findAll();
    }

    @GetMapping("/getById")
    public ResponseEntity<ReadingListItem> getById(@RequestParam Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/addReadingItem")
    @Transactional
    public ResponseEntity<Void> add(@RequestParam String title, @RequestParam String author, @RequestParam(required = false) String notes, @RequestParam Boolean readStatus) {
        if(service.findByTitle(title).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        service.addReadingListItem(title, author, notes, readStatus);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/updateTitle")
    @Transactional
    public ResponseEntity<Void> updateTitle(@RequestParam Long id, @RequestParam String newTitle) {
        if(service.findById(id).isPresent() == false) {
            return ResponseEntity.notFound().build();
        }
        service.updateTitle(id, newTitle);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/updateAuthor")
    @Transactional
    public ResponseEntity<Void> updateAuthor(@RequestParam long id, @RequestParam String newAuthor) {
        if(service.findById(id).isPresent() == false){
            return ResponseEntity.notFound().build();
        }
        service.updateAuthor(id, newAuthor);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/updateNotes")
    @Transactional
    public ResponseEntity<Void> updateNotes(@RequestParam Long id, @RequestParam String newNotes) {
        if(service.findById(id).isPresent() == false){
            return ResponseEntity.notFound().build();
        }
        service.updateNotes(id, newNotes);
        return ResponseEntity.ok().build();    

    }

    @PutMapping("/updateReadStatus")
    @Transactional
    public ResponseEntity<Void> updateReadStatus(@RequestParam Long id, @RequestParam Boolean newReadStatus) {
        if(service.findById(id).isPresent() == false){
            ResponseEntity.notFound().build();
        }

        service.updateReadStatus(id, newReadStatus);
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<Void> delete(@RequestParam Long id) { 
        if(service.findById(id).isPresent() == false) {
            ResponseEntity.notFound().build();
        } 

        service.deleteItem(id);
        return ResponseEntity.ok().build();

    }

}
