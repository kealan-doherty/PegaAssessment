package com.example.PegaAssessment.model;

import jakarta.persistence.*;

// Represents the ReadingListItem entity used by the service.

@Entity
@Table(name = "reading_list_items")
public class ReadingListItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(nullable = true)
  private String notes;

  @Column(name = "read_status")
  private Boolean readStatus;

  public ReadingListItem() {}

  public ReadingListItem(String title, String author, String notes, Boolean readStatus) {
    this.title = title;
    this.author = author;
    this.notes = notes;
    this.readStatus = readStatus;
  }

  // Getters and setters...
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Boolean getReadStatus() {
    return readStatus;
  }

  public void setReadStatus(Boolean readStatus) {
    this.readStatus = readStatus;
  }
}
