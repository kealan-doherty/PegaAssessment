package com.example.PegaAssessment.controller;

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

    @GetMapping("/getByTitle")
    public String getByTitle(@RequestParam String title) {
        return "Fetching reading item with title: " + title;
    }

    @PostMapping("/addReadingItem")
    @Transactional
    public void add(@RequestParam String title, @RequestParam String author, @RequestParam String notes, @RequestParam Boolean readStatus) {
        service.addReadingListItem(title, author, notes, readStatus);
    }

    @PutMapping("/updateTitle")
    public void updateTitle(@RequestParam String oldTitle, @RequestParam String newTitle) {

    }

    @PutMapping("/updateAuthor")
    public void updateAuthor(@RequestParam String title, @RequestParam String newAuthor) {

    }

    @PutMapping("/updateNotes")
    public void updateNotes(@RequestParam String title, @RequestParam String newNotes) {    

    }

    @PutMapping("/updateReadStatus")
    public void updateReadStatus(@RequestParam String title) {

    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam String title) { 

    }

}
