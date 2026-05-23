package com.findit.lostfoundsystem.service;

import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.model.Item;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;
    private final MatchService matchService;

    // CREATE
    public Item createItem(Item item, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        item.setUser(user);
        item.setStatus(ItemStatus.OPEN);

        Item savedItem = itemRepository.save(item);

        //Trigger matching automatically!
        matchService.findMatches(savedItem);

        return savedItem;
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

    //UPDATE
    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));


        existingItem.setTitle(updatedItem.getTitle());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setLocation(updatedItem.getLocation());
        existingItem.setDate(updatedItem.getDate());
        existingItem.setItemImageUrl(updatedItem.getItemImageUrl());

        return itemRepository.save(existingItem);
    }

    public List<Item> getItemsByEmail(String email) {
        return itemRepository.findByUserEmail(email);
    }


    // SEARCH
    public List<Item> searchItems(ItemType type, String category, String location) {
        // Convert enum to String, pass null if type is null
        String typeStr = (type != null) ? type.name() : null;
        return itemRepository.searchItems(typeStr, category, location);
    }

    //ADMIN - DELETE
    public void deleteItem(Long id) {
        Item existing = getItemById(id);

        // Delete related records first
        matchRepository.deleteByLostItemIdOrFoundItemId(id, id);
        claimRepository.deleteByItemId(id);
        notificationRepository.deleteByRelatedItemId(id);

        itemRepository.delete(existing);
    }

    //ADMIN - Manually update item status
    public Item updateItemStatus(Long id, ItemStatus status) {
        Item existing = getItemById(id);
        existing.setStatus(status);
        return itemRepository.save(existing);
    }
}
