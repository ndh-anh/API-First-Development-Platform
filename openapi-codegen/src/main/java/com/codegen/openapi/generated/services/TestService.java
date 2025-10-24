package com.dev.hrm_api.generated.Test.services;

import java.util.*;

public interface TestServiceService {

    /**
     * Get items with filter
     * 
     */
    List<Item> getItems(
        String name,
        String name1

    );
    /**
     * Create an item
     * 
     */
    Item createItem(
ItemCreate body
    );
    /**
     * Get item by ID
     * 
     */
    Item getItemById(
        Integer id

    );
    /**
     * Update an item
     * 
     */
    Item updateItem(
        Integer id
, ItemCreate body
    );
    /**
     * Delete an item
     * 
     */
     deleteItem(
        Integer id

    );
    /**
     * Partially update an item
     * 
     */
    Item patchItem(
        Integer id
, Object body
    );

}
