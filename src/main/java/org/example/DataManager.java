// DataManager.java
package org.example; // Adjust this to match your project’s package structure

import java.util.ArrayList;
import java.util.List;

public class DataManager<T> {
    private List<T> items;

    public DataManager() {
        items = new ArrayList<>();
    }

    // Add an item to the list
    public void addItem(T item) {
        items.add(item);
    }

    // Remove an item from the list
    public void removeItem(T item) {
        items.remove(item);
    }

    // Get an item by index
    public T getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null; // Handle invalid index
    }

    // Get all items in the list
    public List<T> getAllItems() {
        return new ArrayList<>(items); // Return a copy to preserve encapsulation
    }
}
