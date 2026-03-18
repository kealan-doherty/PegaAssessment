package com.example.PegaAssessment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.PegaAssessment.model.ReadingListItem;
import com.example.PegaAssessment.repository.ReadingListItemRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

// Unit tests for Controller behavior in isolation from external dependencies.

@ExtendWith(MockitoExtension.class)
class ControllerUnitTests {

  @Mock private ReadingListItemRepository repository;

  @InjectMocks private Controller controller;

  @Test
  void testGetById_whenFound_returnsOkAndBody() {
    ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
    item.setId(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.getById(1L);
    ReadingListItem body = (ReadingListItem) response.getBody();

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Dune", body.getTitle());
  }

  @Test
  void testGetById_whenMissing_returnsNotFound() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.getById(999L);
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    assertEquals("No item found with the provided ID", body.get("message"));
  }

  @Test
  void testAdd_whenTitleBlank_returnsBadRequestAndSkipsInsert() {
    ResponseEntity<?> response = controller.add("   ", "Frank Herbert", "note", false);
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(400, response.getStatusCode().value());
    assertEquals("title", body.get("field"));
    verify(repository, never()).addReadingListItem(any(), any(), any(), any());
  }

  @Test
  void testAdd_whenInputValid_returnsCreatedAndInserts() {
    ResponseEntity<?> response = controller.add("Dune", "Frank Herbert", "note", false);

    assertEquals(201, response.getStatusCode().value());
    verify(repository).addReadingListItem("Dune", "Frank Herbert", "note", false);
  }

  @Test
  void testAdd_whenNotesBlank_insertsNullNotes() {
    ResponseEntity<?> response = controller.add("Dune", "Frank Herbert", "   ", false);

    assertEquals(201, response.getStatusCode().value());
    verify(repository).addReadingListItem("Dune", "Frank Herbert", null, false);
  }

  @Test
  void testUpdateNotes_whenMissing_returnsNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.updateNotes(1L, "new notes");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    verify(repository, never()).updateNotes(any(), any());
  }

  @Test
  void testUpdateNotes_whenBlankInput_returnsBadRequest() {
    ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
    item.setId(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.updateNotes(1L, "  ");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(400, response.getStatusCode().value());
    assertEquals("newNotes", body.get("field"));
    verify(repository, never()).updateNotes(any(), any());
  }

  @Test
  void testUpdateNotes_whenPresent_returnsOkAndCallsUpdateNotes() {
    ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
    item.setId(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.updateNotes(1L, "new notes");

    assertEquals(200, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(repository).updateNotes(1L, "new notes");
  }

  @Test
  void testUpdateTitle_whenBlank_returnsBadRequest() {
    ResponseEntity<?> response = controller.updateTitle(1L, "   ");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(400, response.getStatusCode().value());
    assertEquals("newTitle", body.get("field"));
    verify(repository, never()).updateTitle(any(), any());
  }

  @Test
  void testUpdateTitle_whenMissing_returnsNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.updateTitle(1L, "New Title");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    verify(repository, never()).updateTitle(any(), any());
  }

  @Test
  void testUpdateTitle_whenPresent_returnsOkAndCallsUpdateTitle() {
    ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic Sc-fi", false);
    item.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.updateTitle(1L, "Dune Messiah");

    assertEquals(200, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(repository).updateTitle(1L, "Dune Messiah");
  }

  @Test
  void testUpdateAuthor_whenBlank_returnsBadRequest() {
    ResponseEntity<?> response = controller.updateAuthor(1L, "  ");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(400, response.getStatusCode().value());
    assertEquals("newAuthor", body.get("field"));
    verify(repository, never()).updateAuthor(any(), any());
  }

  @Test
  void testUpdateAuthor_whenMissing_returnsNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.updateAuthor(1L, "F. Herbert");
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    verify(repository, never()).updateAuthor(any(), any());
  }

  @Test
  void testUpdateAuthor_whenPresent_returnsOkAndCallsUpdateAuthor() {
    ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
    item.setId(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.updateAuthor(1L, "F. Herbert");

    assertEquals(200, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(repository).updateAuthor(1L, "F. Herbert");
  }

  @Test
  void testUpdateReadStatus_whenMissing_returnsNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.updateReadStatus(1L, true);
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    verify(repository, never()).updateReadStatus(any(), any());
  }

  @Test
  void testUpdateReadStatus_whenNullValue_returnsBadRequest() {
    ResponseEntity<?> response = controller.updateReadStatus(1L, null);
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(400, response.getStatusCode().value());
    assertEquals("newReadStatus", body.get("field"));
    verify(repository, never()).updateReadStatus(any(), any());
  }

  @Test
  void testUpdateReadStatus_whenItem_returnsOk() {
    ReadingListItem item = new ReadingListItem("Programming", "linus", "Java is cool", false);
    item.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.updateReadStatus(1L, true);

    assertEquals(200, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(repository).updateReadStatus(1L, true);
  }

  @Test
  void testDelete_whenItemPresent_andReturnsOk() {
    ReadingListItem item = new ReadingListItem("PegaSystems", "chess", "hello there", true);
    item.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(item));

    ResponseEntity<?> response = controller.delete(1L);

    assertEquals(200, response.getStatusCode().value());
    assertNull(response.getBody());
    verify(repository).deleteItem(1L);
  }

  @Test
  void testDelete_whenItemMissing_returns404() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controller.delete(1L);
    Map<?, ?> body = (Map<?, ?>) response.getBody();

    assertEquals(404, response.getStatusCode().value());
    assertEquals("id", body.get("field"));
    verify(repository, never()).deleteItem(any());
  }
}
