package com.findit.lostfoundsystem.repository;

import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByType(ItemType type);

    List<Item> findByTypeAndStatus(ItemType type, ItemStatus status);

    @Query("SELECT i FROM Item i WHERE i.user.email = :email")
    List<Item> findByUserEmail(String email);

    @Query(value = "SELECT * FROM items WHERE " +
            "(CAST(:type AS VARCHAR) IS NULL OR type = CAST(:type AS VARCHAR)) AND " +
            "(:category IS NULL OR LOWER(category) = LOWER(:category)) AND " +
            "(:location IS NULL OR LOWER(location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "status = 'OPEN'",
            nativeQuery = true)
    List<Item> searchItems(
            @Param("type") String type,
            @Param("category") String category,
            @Param("location") String location
    );
}
