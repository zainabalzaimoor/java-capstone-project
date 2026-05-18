package com.findit.lostfoundsystem.repository;

import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByType(ItemType type);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByTypeAndStatus(ItemType type, ItemStatus status);

    List<Item> findByCategory(String category);

    List<Item> findByUserId(Long id);

    @Query("SELECT i FROM Item i WHERE i.user.email = :email")
    List<Item> findByUserEmail(String email);

    List<Item> findByTypeAndStatusAndCategoryAndLocation(ItemType type,
                                                         ItemStatus status,
                                                         String category,
                                                         String location);
}
