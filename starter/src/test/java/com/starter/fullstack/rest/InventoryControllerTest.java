package com.starter.fullstack.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.fullstack.api.Inventory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class InventoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  private Inventory inventory;

  @Before
  public void setup() throws Throwable {
    this.inventory = new Inventory();
    this.inventory.setId("ID");
    this.inventory.setName("NAME_TEST");
    this.inventory.setProductType("TYPE_TEST");
    // Sets the Mongo ID for us
    this.inventory = this.mongoTemplate.save(this.inventory);
  }

  @After
  public void teardown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }

  /**
   * Test findAll endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void findAll() throws Throwable {
    this.mockMvc.perform(get("/inventories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[" + this.objectMapper.writeValueAsString(inventory) + "]"));
  }

  /**
   * Test create endpoint.
   * @throws Throwable see MockMvc
   */
  @Test
  public void create() throws Throwable {
    this.inventory = new Inventory();
    this.inventory.setId("ID2");
    this.inventory.setName("NAME_TEST2");
    this.inventory.setProductType("TYPE_TEST2");
    this.mockMvc.perform(post("/inventories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(this.inventory)))
        .andExpect(status().isOk());

    Assert.assertEquals(2, this.mongoTemplate.findAll(Inventory.class).size());
  }

  /**
   * Test update endpoint
   * @throws Throwable see MockMvc
   */
  @Test
  public void update() throws Throwable {
    // Save id of test inventory so we can update it
    String id = this.inventory.getId();
    // Create new inventory using existing test inventory id and change some values
    this.inventory = new Inventory();
    this.inventory.setId(id);
    this.inventory.setName("plesiosaurus");
    this.inventory.setProductType("ichthyosaur");
    this.inventory.setDescription("ichthyosaurs were technically not dinosaurs");
    this.mockMvc.perform(put("/inventories/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(this.objectMapper.writeValueAsString(this.inventory)))
        .andExpect(status().isOk());
    // Retrieve updated inventory to check if values have been changed as intended
    Inventory updatedInventory = this.mongoTemplate.findById(id, Inventory.class);
    Assert.assertEquals("plesiosaurus", updatedInventory.getName());
    Assert.assertEquals("ichthyosaur", updatedInventory.getProductType());
    Assert.assertEquals("ichthyosaurs were technically not dinosaurs", updatedInventory.getDescription());
  }

  /**
   * Test remove endpoint with valid id.
   * @throws Throwable see MockMvc
   */
  @Test
  public void remove() throws Throwable {
    this.mockMvc.perform(delete("/inventories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content("[\"" + this.inventory.getId() + "\"]"))
        .andExpect(status().isOk());

    Assert.assertEquals(0, this.mongoTemplate.findAll(Inventory.class).size());
  }

  /**
   * Test remove endpoint with invalid id.
   * @throws Throwable see MockMvc
   */
  @Test
  public void removeAndProvideInvalidId() throws Throwable {
    this.mockMvc.perform(delete("/inventories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content("[\"" + "wombat" + "\"]"))
        .andExpect(status().isOk());

    Assert.assertEquals(1, this.mongoTemplate.findAll(Inventory.class).size());
  }
}
