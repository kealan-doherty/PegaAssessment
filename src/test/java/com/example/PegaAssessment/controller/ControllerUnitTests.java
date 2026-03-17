package com.example.PegaAssessment.controller;

import com.example.PegaAssessment.model.ReadingListItem;
import com.example.PegaAssessment.repository.ReadingListItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerUnitTests {

    @Mock
    private ReadingListItemRepository repository;

    @InjectMocks
    private Controller controller;

    @Test
    void getById_whenFound_returnsOkAndBody() {
        ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
        item.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<ReadingListItem> response = controller.getById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dune", response.getBody().getTitle());
    }

    @Test
    void getById_whenMissing_returnsNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<ReadingListItem> response = controller.getById(999L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void add_whenTitleExists_returnsConflictAndSkipsInsert() {
        when(repository.findByTitle("Dune"))
                .thenReturn(Optional.of(new ReadingListItem("Dune", "Frank Herbert", "", false)));

        ResponseEntity<Void> response = controller.add("Dune", "Frank Herbert", "note", false);

        assertEquals(409, response.getStatusCode().value());
        verify(repository, never()).save(any(ReadingListItem.class));
    }

    @Test
    void add_whenTitleMissing_returnsCreatedAndInserts() {
        when(repository.findByTitle("Dune")).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.add("Dune", "Frank Herbert", "note", false);

        assertEquals(201, response.getStatusCode().value());
        verify(repository).save(any(ReadingListItem.class));
    }

    @Test
    void updateNotes_whenMissing_returnsNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.updateNotes(1L, "new notes");

        assertEquals(404, response.getStatusCode().value());
        verify(repository, never()).save(any(ReadingListItem.class));
    }

    @Test
    void updateNotes_whenPresent_returnsOkAndCallsUpdateNotes() {
        ReadingListItem item = new ReadingListItem("Dune", "Frank Herbert", "Classic", false);
        item.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Void> response = controller.updateNotes(1L, "new notes");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("new notes", item.getNotes());
        verify(repository).save(item);
    }
}
