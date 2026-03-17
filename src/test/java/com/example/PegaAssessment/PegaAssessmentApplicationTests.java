package com.example.PegaAssessment;

import com.example.PegaAssessment.model.ReadingListItem;
import com.example.PegaAssessment.repository.ReadingListItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class PegaAssessmentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ReadingListItemRepository readingListItemRepository;

	@BeforeEach
	void cleanDatabase() {
		readingListItemRepository.deleteAllInBatch();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testGetAll_whenDatabaseHasItems_returnsItemsArray() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));
		readingListItemRepository.save(new ReadingListItem("1984", "George Orwell", "Dystopian", true));

		mockMvc.perform(MockMvcRequestBuilders.get("/getAll"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	void testGetById_whenItemExists_returns200AndItem() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.get("/getById").param("id", saved.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(saved.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Dune"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.author").value("Frank Herbert"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.notes").value("Sci-fi classic"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.read_status").value(false));
	}

	@Test
	void testGetById_whenItemMissing_returns404() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getById").param("id", "999999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void testGetById_whenIdMissing_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getById"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void testAddReadingItem_whenTitleIsNew_returns201AndPersistsItem() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/addReadingItem")
				.param("title", "The Hobbit")
				.param("author", "J.R.R. Tolkien")
				.param("notes", "Fantasy")
				.param("readStatus", "false"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		ReadingListItem inserted = readingListItemRepository.findByTitle("The Hobbit").orElseThrow();
		assertEquals("J.R.R. Tolkien", inserted.getAuthor());
		assertEquals("Fantasy", inserted.getNotes());
		assertEquals(false, inserted.getReadStatus());
	}

	@Test
	void testAddReadingItem_whenTitleExists_returns409AndDoesNotDuplicate() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));

		mockMvc.perform(MockMvcRequestBuilders.post("/addReadingItem")
				.param("title", "Dune")
				.param("author", "Frank Herbert")
				.param("notes", "Duplicate")
				.param("readStatus", "true"))
			.andExpect(MockMvcResultMatchers.status().isConflict());

		assertEquals(1, readingListItemRepository.findAll().size());
	}

	@Test
	void testAddReadingItem_whenNotesMissing_returns201AndStoresNullNotes() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/addReadingItem")
				.param("title", "Foundation")
				.param("author", "Isaac Asimov")
				.param("readStatus", "false"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		ReadingListItem inserted = readingListItemRepository.findByTitle("Foundation").orElseThrow();
		assertEquals("Isaac Asimov", inserted.getAuthor());
		assertEquals(null, inserted.getNotes());
		assertEquals(false, inserted.getReadStatus());
	}

	@Test
	void testUpdateNotes_whenItemExists_returns200AndUpdatesNotes() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Old notes", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateNotes")
				.param("id", saved.getId().toString())
				.param("newNotes", "Updated notes"))
			.andExpect(MockMvcResultMatchers.status().isOk());

		ReadingListItem updated = readingListItemRepository.findById(saved.getId()).orElseThrow();
		assertEquals("Updated notes", updated.getNotes());
	}

	@Test
	void testUpdateNotes_whenItemMissing_returns404() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateNotes")
				.param("id", "999999")
				.param("newNotes", "Updated notes"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void testDelete_whenItemExists_returns200AndRemovesItem() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.delete("/delete").param("id", saved.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk());

		assertFalse(readingListItemRepository.findById(saved.getId()).isPresent());
	}

	@Test
	void testDelete_whenItemMissing_returns404() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/delete").param("id", "999999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());

		assertTrue(readingListItemRepository.findAll().isEmpty());
	}

	@Test
	void testUpdateReadStatus_whenItemExists_returns200AndUpdatesStatus() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateReadStatus")
				.param("id", saved.getId().toString())
				.param("newReadStatus", "true"))
			.andExpect(MockMvcResultMatchers.status().isOk());

		ReadingListItem updated = readingListItemRepository.findById(saved.getId()).orElseThrow();
		assertEquals(true, updated.getReadStatus());
	}

}
