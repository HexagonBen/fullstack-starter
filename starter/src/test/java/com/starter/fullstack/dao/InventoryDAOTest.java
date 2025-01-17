package com.starter.fullstack.dao;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.config.EmbedMongoClientOverrideConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test Inventory DAO.
 */
@ContextConfiguration(classes = {EmbedMongoClientOverrideConfig.class})
@DataMongoTest
@RunWith(SpringRunner.class)
public class InventoryDAOTest {
  @Resource
  private MongoTemplate mongoTemplate;
  private InventoryDAO inventoryDAO;
  private static final String NAME = "Amber";
  private static final String PRODUCT_TYPE = "hops";

  @Before
  public void setup() {
    this.inventoryDAO = new InventoryDAO(this.mongoTemplate);
  }

  @After
  public void tearDown() {
    this.mongoTemplate.dropCollection(Inventory.class);
  }

  /**
   * Test Find All method.
   */
  @Test
  public void findAll() {
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    this.mongoTemplate.save(inventory);
    List<Inventory> actualInventory = this.inventoryDAO.findAll();
    Assert.assertFalse(actualInventory.isEmpty());
  }

  /**
   * Test Create method.
   */
  @Test
  public void create() {
    List<Inventory> existingInventory = this.mongoTemplate.findAll(Inventory.class);
    Inventory inventory = new Inventory();
    inventory.setId("wombat");
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    Inventory returnedInventory = this.inventoryDAO.create(inventory);
    List<Inventory> inventoryPostInsertion = this.mongoTemplate.findAll(Inventory.class);
    Inventory createdInventory = this.mongoTemplate.findById(returnedInventory.getId(), Inventory.class); 
    Assert.assertTrue(inventoryPostInsertion.size() == existingInventory.size() + 1);
    Assert.assertTrue(createdInventory.getId().equals(returnedInventory.getId()));
  }

  /**
   * Test Update method
   */
  @Test
  public void update() {
    // Put an inventory in the database so there's something to update
    Inventory originalInventory = new Inventory();
    originalInventory.setName(NAME);
    originalInventory.setProductType(PRODUCT_TYPE);
    originalInventory = this.mongoTemplate.save(originalInventory);
    // Create inventory with new variables to update existing inventory in database
    Inventory newInventory = new Inventory();
    newInventory.setName("plesiosaurus");
    newInventory.setProductType("ichthyosaur");
    newInventory.setDescription("ichthyosaurs were technically not dinosaurs");
    // Update originalInventory with values from newInventory
    Optional<Inventory> returnedInventory = this.inventoryDAO.update(originalInventory.getId(), newInventory);
    // Check that update operation was successful
    Assert.assertTrue(returnedInventory.get().getName().equals("plesiosaurus"));
    Assert.assertTrue(returnedInventory.get().getProductType().equals("ichthyosaur"));
    Assert.assertTrue(returnedInventory.get().getDescription().equals("ichthyosaurs were technically not dinosaurs"));
  }

  /**
   * Test Delete method with valid id.
   */
  @Test
  public void delete() {
    // Put an inventory in the databse so there's something to delete
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    // Save the id upon insertion to check against when we delete it
    Inventory savedInventory = mongoTemplate.save(inventory);
    String targetId = savedInventory.getId();
    // InventoryDAO delete accepts a List, so we put the target id in a List
    List<String> idsToDelete = new ArrayList<>();
    idsToDelete.add(targetId);
    // Count number of inventories in collection to make sure it's 1 less after a deletion
    List<Inventory> existingInventories = this.mongoTemplate.findAll(Inventory.class);
    // Delete an inventory, and check id of deleted inventory against the id we saved earlier
    Optional<List<Inventory>> deletedInventories = this.inventoryDAO.delete(idsToDelete);
    // Check size of collection post deletion
    List<Inventory> inventoryPostDeletion = this.mongoTemplate.findAll(Inventory.class);
    // Check that id of deleted inventory matches our target id
    Optional<Inventory> deletedInventory = deletedInventories.map(inventories -> inventories.get(0));
    Assert.assertTrue(deletedInventory.map(Inventory::getId).orElse("default").equals(targetId));
    // Check that size of collection has decreased by 1
    Assert.assertTrue(inventoryPostDeletion.size() == existingInventories.size() - 1);
  }

  /**
   * Test Delete method with invalid id
   */
  @Test
  public void deleteAndProvideInvalidId() {
    // Put an inventory in the collection so there's something to delete
    Inventory inventory = new Inventory();
    inventory.setName(NAME);
    inventory.setProductType(PRODUCT_TYPE);
    Inventory savedInventory = mongoTemplate.save(inventory);
    // Count number of inventories in collection to make sure it's unchanged after this deletion attempt
    List<Inventory> existingInventories = this.mongoTemplate.findAll(Inventory.class);
    // Attempt deletion but provide invalid id
    List<String> invalidId = new ArrayList<>();
    invalidId.add("wombat");
    Optional<List<Inventory>> deletedInventory = this.inventoryDAO.delete(invalidId);
    // Check that size of collection is unchanged
    List<Inventory> inventoryPostDeletion = this.mongoTemplate.findAll(Inventory.class);
    Assert.assertTrue(inventoryPostDeletion.size() == existingInventories.size());
  }
}
