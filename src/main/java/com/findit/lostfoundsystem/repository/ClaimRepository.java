package com.findit.lostfoundsystem.repository;

import com.findit.lostfoundsystem.enums.ClaimStatus;
import com.findit.lostfoundsystem.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    // All claims for a specific item
    List<Claim> findByItemId(Long itemId);

    // All claims made by a specific user
    List<Claim> findByClaimantId(Long claimantId);

    // All claims by status
    List<Claim> findByStatus(ClaimStatus status);

    // Check if user already claimed this item
    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
}
