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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// this class will house the integration tests for the application,
//  testing the full stack from controller to database.
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
	void testGetItems_whenDatabaseHasItems_returnsItemsArray() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));
		readingListItemRepository.save(new ReadingListItem("1984", "George Orwell", "Dystopian", true));

		mockMvc.perform(MockMvcRequestBuilders.get("/items"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
	}

	@Test
	void testGetItems_whenDatabaseEmpty_returnsEmptyArray() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/items"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

	}

	@Test
	void testGetItems_whenAuthorFilterProvided_returnsMatchingItemsOnly() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));
		readingListItemRepository.save(new ReadingListItem("Foundation", "Isaac Asimov", "Sci-fi", false));

		mockMvc.perform(MockMvcRequestBuilders.get("/items").param("author", "Frank Herbert"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value("Frank Herbert"));
	}

	@Test
	void testGetItems_whenTitleFilterProvided_returnsMatchingItemsOnly() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));
		readingListItemRepository.save(new ReadingListItem("Foundation", "Isaac Asimov", "Sci-fi", false));

		mockMvc.perform(MockMvcRequestBuilders.get("/items").param("title", "Dune"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Dune"));
	}

	@Test
	void testGetItems_whenBothFiltersProvided_returnsIntersection() throws Exception {
		readingListItemRepository.save(new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false));
		readingListItemRepository.save(new ReadingListItem("Dune", "Brian Herbert", "Continuation", false));
		readingListItemRepository.save(new ReadingListItem("Foundation", "Isaac Asimov", "Sci-fi", false));

		mockMvc.perform(MockMvcRequestBuilders.get("/items")
				.param("title", "Dune")
				.param("author", "Frank Herbert"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Dune"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].author").value("Frank Herbert"));
	}

	@Test 
	void testGetById_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/getById").param("id", "-1"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
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
			.andExpect(MockMvcResultMatchers.jsonPath("$.readStatus").value(false));
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
		mockMvc.perform(MockMvcRequestBuilders.post("/add")
				.param("title", "The Hobbit")
				.param("author", "J.R.R. Tolkien")
				.param("notes", "Fantasy")
				.param("readStatus", "false"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		ReadingListItem inserted = readingListItemRepository.findByTitle("The Hobbit").orElseThrow();
		assertEquals("J.R.R. Tolkien", inserted.getAuthor());
		assertEquals("Fantasy", inserted.getNotes());
		assertFalse(inserted.getReadStatus());
	}

	@Test
	void testAddReadingItem_whenNotesMissing_returns201AndStoresNullNotes() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/add")
				.param("title", "Foundation")
				.param("author", "Isaac Asimov")
				.param("readStatus", "false"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		ReadingListItem inserted = readingListItemRepository.findByTitle("Foundation").orElseThrow();
		assertEquals("Isaac Asimov", inserted.getAuthor());
		assertNull(inserted.getNotes());
		assertFalse(inserted.getReadStatus());
	}

	@Test
	void testAddReadingItem_whenNotesBlank_returns201AndStoresNullNotes() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/add")
				.param("title", "Hyperion")
				.param("author", "Dan Simmons")
				.param("notes", "")
				.param("readStatus", "false"))
			.andExpect(MockMvcResultMatchers.status().isCreated());

		ReadingListItem inserted = readingListItemRepository.findByTitle("Hyperion").orElseThrow();
		assertEquals("Dan Simmons", inserted.getAuthor());
		assertNull(inserted.getNotes());
		assertFalse(inserted.getReadStatus());
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
		assertTrue(updated.getReadStatus());
	}

	@Test
	void testUpdateTitle_whenItemExists_returns200AndUpdatesTitle() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateTitle")
				.param("id", saved.getId().toString())
				.param("newTitle", "Dune Messiah"))
			.andExpect(MockMvcResultMatchers.status().isOk());

		ReadingListItem updated = readingListItemRepository.findById(saved.getId()).orElseThrow();
		assertEquals("Dune Messiah", updated.getTitle());
	}

	@Test
	void testUpdateTitle_whenBlank_returns400() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("A Song of Ice and Fire", "George Martin", "fantasy", true)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateTitle")
				.param("id", saved.getId().toString())
				.param("newTitle", "   "))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("title"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Title is required and cannot be empty"));
	}

	@Test
	void testUpdateTitle_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateTitle")
				.param("id", "0")
				.param("newTitle", "New title"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
	}

	@Test
	void testUpdateAuthor_whenItemMissing_returns404() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateAuthor")
				.param("id", "999999")
				.param("newAuthor", "F. Herbert"))
			.andExpect(MockMvcResultMatchers.status().isNotFound())
        	.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        	.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
        	.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No item found with the provided ID"));
	}	

	@Test 
	void testUpdateAuthor_whenItemExists_returns200AndUpdates() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("A Song of Ice and Fire", "George Martin", "fantasy", true)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateAuthor")
				.param("id", saved.getId().toString())
				.param("newAuthor", "G. R. R. Martin"))
			.andExpect(MockMvcResultMatchers.status().isOk());

		ReadingListItem updated = readingListItemRepository.findById(saved.getId()).orElseThrow();
		assertEquals("G. R. R. Martin", updated.getAuthor());
	}

	@Test
	void testUpdateAuthor_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateAuthor")
				.param("id", "0")
				.param("newAuthor", "Any Author"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
	}

	@Test 
	void testUpdateNotes_whenBlank_returns400() throws Exception {
		ReadingListItem saved = readingListItemRepository.save(
			new ReadingListItem("Dune", "Frank Herbert", "Sci-fi classic", false)
		);

		mockMvc.perform(MockMvcRequestBuilders.put("/updateNotes")
				.param("id", saved.getId().toString())
				.param("newNotes", "   "))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("newNotes"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("New notes cannot be empty"));
	}

	@Test
	void testUpdateNotes_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateNotes")
				.param("id", "0")
				.param("newNotes", "Updated notes"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
	}

	@Test
	void testUpdateReadStatus_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/updateReadStatus")
				.param("id", "0")
				.param("newReadStatus", "true"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
	}

	@Test
	void testDelete_whenIdInvalid_returns400() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/delete")
				.param("id", "0"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.field").value("id"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("ID is required and must be greater than 0"));
	}
}


