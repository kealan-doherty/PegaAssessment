package com.example.PegaAssessment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.PegaAssessment.model.ReadingListItem;

//import com.example.PegaAssessment.model.*;

@RestController
public class Controller {

    @GetMapping("/")
    public String hello() {// simple test get route 
        return "hello there";
    }

    @GetMapping("/test")
    public ReadingListItem cool(@RequestParam String title, @RequestParam String author,@RequestParam String notes, @RequestParam Boolean readStatus) {
        ReadingListItem input = new ReadingListItem(title, author, notes, readStatus);
        return input;
    }

    @GetMapping("/getAll")
    public String getAll() {
        return "basic setup for get all route";
    }

    @GetMapping("/getByTitle")
    public String getByTitle(@RequestParam String title) {
        return "Fetching reading item with title: " + title;
    }

    @PostMapping("/addReadingItem")
    public void add(@RequestParam String title, @RequestParam String author, @RequestParam String notes, @RequestParam Boolean readStatus) {

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
