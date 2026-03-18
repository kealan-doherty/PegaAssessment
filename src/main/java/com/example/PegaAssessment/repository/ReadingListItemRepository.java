package com.example.PegaAssessment.repository;

import com.example.PegaAssessment.model.ReadingListItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingListItemRepository extends JpaRepository<ReadingListItem, Long> {
  // this interface defines the data access methods for the ReadingListItem entity.

  @Query(value = "SELECT * FROM reading_list_items WHERE author = :author", nativeQuery = true)
  List<ReadingListItem> findByAuthor(@Param("author") String author);

  @Modifying
  @Query(
      value =
          "INSERT INTO reading_list_items (title, author, notes, read_status) VALUES (:title, :author, :notes, :readStatus)",
      nativeQuery = true)
  void addReadingListItem(
      @Param("title") String title,
      @Param("author") String author,
      @Param("notes") String notes,
      @Param("readStatus") Boolean readStatus);

  @Query(value = "SELECT * FROM reading_list_items WHERE title = :title", nativeQuery = true)
  Optional<ReadingListItem> findByTitle(@Param("title") String title);

  @Query(
      value =
          """
                SELECT *
                FROM reading_list_items
                WHERE (:title IS NULL OR LOWER(title) LIKE '%' || LOWER(:title) || '%')
                    AND (:author IS NULL OR LOWER(author) LIKE '%' || LOWER(:author) || '%')
                """,
      nativeQuery = true)
  List<ReadingListItem> findByTitleAndAuthor(
      @Param("title") String title, @Param("author") String author);

  @Modifying
  @Query(
      value = "UPDATE reading_list_items SET title = :newTitle WHERE id = :id",
      nativeQuery = true)
  void updateTitle(@Param("id") Long id, @Param("newTitle") String newTitle);

  @Modifying
  @Query(
      value = "UPDATE reading_list_items SET author = :newAuthor WHERE id = :id",
      nativeQuery = true)
  void updateAuthor(@Param("id") Long id, @Param("newAuthor") String newAuthor);

  @Modifying
  @Query(
      value = "UPDATE reading_list_items SET notes = :newNotes WHERE id = :id",
      nativeQuery = true)
  void updateNotes(@Param("id") Long id, @Param("newNotes") String newNotes);

  @Modifying
  @Query(
      value = "UPDATE reading_list_items SET read_status = :newReadStatus WHERE id = :id",
      nativeQuery = true)
  void updateReadStatus(@Param("id") Long id, @Param("newReadStatus") Boolean newReadStatus);

  @Modifying
  @Query(value = "DELETE FROM reading_list_items WHERE id = :id", nativeQuery = true)
  void deleteItem(@Param("id") Long id);
}
