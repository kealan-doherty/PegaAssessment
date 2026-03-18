package com.example.PegaAssessment.controller;

import com.example.PegaAssessment.model.ReadingListItem;
import com.example.PegaAssessment.repository.ReadingListItemRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  private final ReadingListItemRepository service;

  public Controller(ReadingListItemRepository service) {
    this.service = service;
  }

  // these are helper methods to reduce code duplication and improve readability in the code base.
  private String normalizeFilter(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private ResponseEntity<?> validateId(Long id) {
    if (id == null || id < 1) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "id",
                  "message", "ID is required and must be greater than 0"));
    }
    return null;
  }

  private ResponseEntity<?> validateItemExists(Long id) {
    if (service.findById(id).isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              Map.of(
                  "field", "id",
                  "message", "No item found with the provided ID"));
    }
    return null;
  }

  private ResponseEntity<?> validateTitle(String title) {
    if (title == null || title.isBlank()) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "title",
                  "message", "Title is required and cannot be empty"));
    }
    return null;
  }

  private ResponseEntity<?> validateAuthor(String author) {
    if (author == null || author.isBlank()) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "author",
                  "message", "Author is required and cannot be empty"));
    }
    return null;
  }

  private ResponseEntity<?> validateReadStatus(Boolean readStatus) {
    if (readStatus == null) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "readStatus",
                  "message", "Read status is required and cannot be null"));
    }
    return null;
  }

  @GetMapping("/items")
  @Transactional
  public List<ReadingListItem> getItems(
      @RequestParam(required = false) String title, @RequestParam(required = false) String author) {
    String normalizedTitle = normalizeFilter(title);
    String normalizedAuthor = normalizeFilter(author);

    if (normalizedTitle == null && normalizedAuthor == null) {
      return service.findAll();
    }

    return service.findByTitleAndAuthor(normalizedTitle, normalizedAuthor);
  }

  @GetMapping("/getById")
  @Transactional
    public ResponseEntity<?> getById(@RequestParam Long id) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }
    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }
    return ResponseEntity.ok(service.findById(id).get());
  }

  @PostMapping("/add")
  @Transactional
  public ResponseEntity<?> add(
      @RequestParam String title,
      @RequestParam String author,
      @RequestParam(required = false) String notes,
      @RequestParam Boolean readStatus) {
    ResponseEntity<?> titleValidationResponse = validateTitle(title);
    if (titleValidationResponse != null) {
      return titleValidationResponse;
    }

    ResponseEntity<?> authorValidationResponse = validateAuthor(author);
    if (authorValidationResponse != null) {
      return authorValidationResponse;
    }

    ResponseEntity<?> readStatusValidationResponse = validateReadStatus(readStatus);
    if (readStatusValidationResponse != null) {
      return readStatusValidationResponse;
    }

    String normalizedNotes = normalizeFilter(notes);

    service.addReadingListItem(title.trim(), author.trim(), normalizedNotes, readStatus);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/updateTitle")
  @Transactional
  public ResponseEntity<?> updateTitle(@RequestParam Long id, @RequestParam String newTitle) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }

    ResponseEntity<?> titleValidationResponse = validateTitle(title);
    if (titleValidationResponse != null) {
      retrun titleValidationResponse;
    }

    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }

    service.updateTitle(id, newTitle);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/updateAuthor")
  @Transactional
  public ResponseEntity<?> updateAuthor(@RequestParam Long id, @RequestParam String newAuthor) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }
    ReponseEntity<?> authorValidationResponse = validateAuthor(author);
    if ( authorValidationResponse != null) {
      return authorValidationResponse;
    }
    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }
    service.updateAuthor(id, newAuthor);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/updateNotes")
  @Transactional
  public ResponseEntity<?> updateNotes(@RequestParam Long id, @RequestParam String newNotes) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }

    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }

    if (newNotes == null || newNotes.isBlank()) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "newNotes",
                  "message", "New notes cannot be empty"));
    }
    service.updateNotes(id, newNotes);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/updateReadStatus")
  @Transactional
  public ResponseEntity<?> updateReadStatus(
      @RequestParam Long id, @RequestParam Boolean newReadStatus) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }
    if (newReadStatus == null) {
      return ResponseEntity.badRequest()
          .body(
              Map.of(
                  "field", "newReadStatus",
                  "message",
                      "New read status is required and cannot be null and must be a Boolean value"));
    }
    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }

    service.updateReadStatus(id, newReadStatus);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/delete")
  @Transactional
  public ResponseEntity<?> delete(@RequestParam Long id) {
    ResponseEntity<?> validationResponse = validateId(id);
    if (validationResponse != null) {
      return validationResponse;
    }
    ResponseEntity<?> itemExistsResponse = validateItemExists(id);
    if (itemExistsResponse != null) {
      return itemExistsResponse;
    }
    service.deleteItem(id);
    return ResponseEntity.ok().build();
  }
}
