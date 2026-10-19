package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {

        return ResponseEntity.ok(
                itemService.getAllItems()
        );
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemById(
            @PathVariable Integer itemId) {

        Item item =
                itemService.getItemById(itemId);

        if (item == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found.");
        }

        return ResponseEntity.ok(item);
    }

    @GetMapping("/package/{packageId}")
    public ResponseEntity<List<Item>>
    getItemsByPackageId(
            @PathVariable Integer packageId) {

        return ResponseEntity.ok(
                itemService.getItemsByPackageId(packageId)
        );
    }

    @PostMapping
    public ResponseEntity<Item> createItem(
            @RequestBody Item item) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.createItem(item)
                );
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Integer itemId,
            @RequestBody Item item) {

        Item updated =
                itemService.updateItem(
                        itemId,
                        item
                );

        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found.");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Integer itemId) {

        boolean deleted =
                itemService.deleteItem(itemId);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Item not found.");
        }

        return ResponseEntity.ok(
                "Item deleted successfully."
        );
    }
}
