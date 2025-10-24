package com.dev.hrm_api.generated.Test.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RequestMapping
public interface TestController {

    /**
     * Get items with filter
     * 
     */
    @GetMapping("/items")
    ResponseEntity<List<Item>> getItems(
        @RequestParam(
             defaultValue = "Book"
        ) String name,
        @RequestParam(
            
        ) String name1
    );
    /**
     * Create an item
     * 
     */
    @PostMapping("/items")
    ResponseEntity<Item> createItem(

        @RequestBody ItemCreate body
    );
    /**
     * Get item by ID
     * 
     */
    @GetMapping("/items/{id}")
    ResponseEntity<Item> getItemById(
        @PathVariable(
            required = true   
        ) Integer id
    );
    /**
     * Update an item
     * 
     */
    @PutMapping("/items/{id}")
    ResponseEntity<Item> updateItem(
        @PathVariable(
            required = true   
        ) Integer id
,
        @RequestBody ItemCreate body
    );
    /**
     * Delete an item
     * 
     */
    @DeleteMapping("/items/{id}")
    ResponseEntity<> deleteItem(
        @PathVariable(
            required = true   
        ) Integer id
    );
    /**
     * Partially update an item
     * 
     */
    @PatchMapping("/items/{id}")
    ResponseEntity<Item> patchItem(
        @PathVariable(
            required = true   
        ) Integer id
,
        @RequestBody Object body
    );

}
