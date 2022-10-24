package com.starter.fullstack.rest;

import com.starter.fullstack.api.Inventory;
import com.starter.fullstack.dao.InventoryDAO;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inventory Controller.
 */
@RestController
public class InventoryController {
  private final InventoryDAO inventoryDAO;

  /**
   * Default Constructor.
   * @param inventoryDAO inventoryDAO.
   */
  public InventoryController(InventoryDAO inventoryDAO) {
    Assert.notNull(inventoryDAO, "Inventory DAO must not be null.");
    this.inventoryDAO = inventoryDAO;
  }

  /**
   * Find Inventories.
   * @return List of Inventory.
   */
  @GetMapping("/inventories")
  public List<Inventory> findInventories() {
    return this.inventoryDAO.findAll();
  }

  /**
   * Create Inventory.
   *
   * @param inventory inventory.
   * @return Inventory.
   */
  @PostMapping("/inventories")
  public Inventory createInventory(@Valid @RequestBody Inventory inventory) {
    return this.inventoryDAO.create(inventory);
  }

  /**
   * Update Inventory By Id.
   * @param inventory inventory.
   * @param id id.
   * @return Inventory.
   */
  @PutMapping("/inventories/{id}")
  public Optional<Inventory> updateInventory(@PathVariable("id") String id, @Valid @RequestBody Inventory inventory) {
    return this.inventoryDAO.update(id, inventory);
  }

  /**
   * Delete Inventories By Ids
   *
   * @param ids ids.
   */
  @DeleteMapping("/inventories")
  public void deleteInventory(@RequestBody List<String> ids) {
    Assert.notEmpty(ids, "Inventory Ids were not provided");
    this.inventoryDAO.delete(ids);
  }
}


