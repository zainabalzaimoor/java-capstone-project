package com.findit.lostfoundsystem.service;

import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.model.Item;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.repository.ItemRepository;
import com.findit.lostfoundsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    // CREATE
    public Item createItem(Item item, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        item.setUser(user);
        item.setStatus(ItemStatus.OPEN);

        return itemRepository.save(item);
    }

    //READ
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public List<Item> getItemsByType(ItemType type) {
        return itemRepository.findByType(type);
    }

//    public List<Item> getItemsByUser(Long userId) {
//        return itemRepository.findByUserId(userId);
//    }

    //UPDATE
    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

//        if(!existingItem.getUser().getEmail().equals(updatedItem.getUser().getEmail())) {}

        existingItem.setTitle(updatedItem.getTitle());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setLocation(updatedItem.getLocation());
        existingItem.setDate(updatedItem.getDate());
        existingItem.setItemImageUrl(updatedItem.getItemImageUrl());

        return itemRepository.save(existingItem);
    }

    //ADMIN - DELETE
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> getItemsByEmail(String email) {
        return itemRepository.findByUserEmail(email);
    }

    //ADMIN - Manually change item status
    public Item updateItemStatus(Long id, ItemStatus status) {
        Item existing = getItemById(id);
        existing.setStatus(status);
        return itemRepository.save(existing);
    }
}
