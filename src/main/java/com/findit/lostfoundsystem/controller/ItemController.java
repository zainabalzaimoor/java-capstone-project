package com.findit.lostfoundsystem.controller;

import com.findit.lostfoundsystem.dto.ItemRequestDTO;
import com.findit.lostfoundsystem.dto.ItemResponseDTO;
import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.mapper.ItemMapper;
import com.findit.lostfoundsystem.model.Item;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    // POST /api/items
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(
            @RequestBody ItemRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Item item = itemMapper.toEntity(request);
        Item saved = itemService.createItem(item, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemMapper.toResponseDTO(saved));
    }

    // GET /api/items
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems(
            @RequestParam(required = false) ItemType type) {

        List<Item> items = (type != null)
                ? itemService.getItemsByType(type)
                : itemService.getAllItems();

        List<ItemResponseDTO> response = items.stream()
                .map(itemMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // GET /api/items/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long id) {
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(itemMapper.toResponseDTO(item));
    }

    // GET /api/items/my-items
    @GetMapping("/my-items")
    public ResponseEntity<List<ItemResponseDTO>> getMyItems(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<ItemResponseDTO> response = itemService.getItemsByEmail(email)
                .stream()
                .map(itemMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // PUT /api/items/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(
            @PathVariable Long id,
            @RequestBody ItemRequestDTO request) {

        Item updated = itemMapper.toEntity(request);
        Item saved = itemService.updateItem(id, updated);
        return ResponseEntity.ok(itemMapper.toResponseDTO(saved));
    }

    // DELETE /api/items/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponseDTO> updateItemStatus(
            @PathVariable Long id,
            @RequestParam ItemStatus status) {
        Item updated = itemService.updateItemStatus(id,status);
        return ResponseEntity.ok(itemMapper.toResponseDTO(updated));
    }
}
