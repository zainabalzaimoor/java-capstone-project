package com.findit.lostfoundsystem.service;

import com.findit.lostfoundsystem.dto.ClaimRequestDTO;
import com.findit.lostfoundsystem.enums.ClaimStatus;
import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.model.Claim;
import com.findit.lostfoundsystem.model.Item;
import com.findit.lostfoundsystem.model.User;
import com.findit.lostfoundsystem.repository.ClaimRepository;
import com.findit.lostfoundsystem.repository.ItemRepository;
import com.findit.lostfoundsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //USER - file a claim
    public Claim createClaim(ClaimRequestDTO request, String email) {

        User claimant = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Only FOUND items can be claimed
        if (item.getType() != ItemType.FOUND) {
            throw new RuntimeException("You can only claim a FOUND item");
        }

        // Can't claim your own item
        if (item.getUser().getId().equals(claimant.getId())) {
            throw new RuntimeException("You cannot claim your own item");
        }

        // Can't claim the same item twice
        if (claimRepository.existsByItemIdAndClaimantId(
                item.getId(), claimant.getId())) {
            throw new RuntimeException("You already filed a claim for this item");
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimant(claimant)
                .message(request.message())
                .proofAttachmentUrl(request.proofAttachmentUrl())
                .status(ClaimStatus.PENDING)
                .build();

        // Update item status to CLAIMED
        item.setStatus(ItemStatus.CLAIMED);
        itemRepository.save(item);

        return claimRepository.save(claim);
    }

    //USER - Get my claims
    public List<Claim> getMyClaims(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return claimRepository.findByClaimantId(user.getId());

    }

    //ADMIN - Get All
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByStatus(ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
    }

    //ADMIN - Approve or reject a claim
    public Claim updateClaimStatus(Long claimId, ClaimStatus newStatus) {
        Claim claim = getClaimById(claimId);
        claim.setStatus(newStatus);

        if (newStatus == ClaimStatus.APPROVED) {
            // Close the item — it has been returned!
            claim.getItem().setStatus(ItemStatus.CLOSED);
            itemRepository.save(claim.getItem());
        } else if (newStatus == ClaimStatus.REJECTED) {
            // Reopen the item
            claim.getItem().setStatus(ItemStatus.OPEN);
            itemRepository.save(claim.getItem());
        }

        return claimRepository.save(claim);
    }


}
