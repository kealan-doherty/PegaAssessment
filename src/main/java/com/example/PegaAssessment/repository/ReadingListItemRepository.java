package com.example.PegaAssessment.repository;

import org.springframework.stereotype.Repository;
import com.example.PegaAssessment.model.ReadingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, Long> {
    // this will house the SQL queries for the CRUD operations on the ReadingListItem entity

    @Query(value = "SELECT * FROM reading_list_items WHERE author = :author", nativeQuery = true)
    List<ReadingListItem> findByAuthor(@Param("author") String author);

    @Modifying
    @Query(value = "INSERT INTO reading_list_items (title, author, notes, read_status) VALUES (:title, :author, :notes, :readStatus)", nativeQuery = true)
    void addReadingListItem(@Param("title") String title, @Param("author") String author, @Param("notes") String notes, @Param("readStatus") Boolean readStatus);

    @Query(value = "SELECT r FROM reading_list_items r WHERE r.id = :id", nativeQuery = true)
    Optional<ReadingListItem> findById(@Param("id") Long id);

    @Query(value = "SELECT * FROM reading_list_items WHERE title = :title", nativeQuery = true)
    Optional<ReadingListItem> findByTitle(@Param("title") String title);

    @Modifying
    @Query(value = "UPDATE reading_list_items SET title = :newTitle WHERE id = :id", nativeQuery = true)
    void updateTitle(@Param("id") Long id, @Param("newTitle") String newTitle);

    @Modifying
    @Query(value = "UPDATE reading_list_items SET author = :newAuthor WHERE id = :id", nativeQuery = true)
    void updateAuthor(@Param("id") Long id, @Param("newAuthor") String newAuthor);

    @Modifying
    @Query(value = "UPDATE reading_list_items SET notes =: newNotes WHERE id = :id", nativeQuery = true)
    void updateNotes(@Param("id") Long id, @Param("newNotes") String newNotes);
}