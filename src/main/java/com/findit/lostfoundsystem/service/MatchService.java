package com.findit.lostfoundsystem.service;


import com.findit.lostfoundsystem.enums.ItemStatus;
import com.findit.lostfoundsystem.enums.ItemType;
import com.findit.lostfoundsystem.enums.MatchStatus;
import com.findit.lostfoundsystem.enums.NotificationType;
import com.findit.lostfoundsystem.model.Item;
import com.findit.lostfoundsystem.model.Match;
import com.findit.lostfoundsystem.repository.ItemRepository;
import com.findit.lostfoundsystem.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    // Will be called everytime new item is posted
    public void findMatches(Item newItem) {

        // If new item is LOST → search for FOUND items
        // If new item is FOUND → search for LOST items
        ItemType oppositeType = (newItem.getType() == ItemType.LOST)
                ? ItemType.FOUND
                : ItemType.LOST;

        // Get all OPEN items of the opposite type
        List<Item> candidates = itemRepository
                .findByTypeAndStatus(oppositeType, ItemStatus.OPEN);

        for (Item candidate : candidates) {
            double score = calculateScore(newItem, candidate);

            // Only create a match if score is good enough
            if (score >= 0.5) {

                // Avoid duplicate matches
                boolean alreadyExists = isAlreadyMatched(newItem, candidate, oppositeType);
                if (alreadyExists) continue;

                // Create the match
                Item lostItem  = (newItem.getType() == ItemType.LOST) ? newItem : candidate;
                Item foundItem = (newItem.getType() == ItemType.FOUND) ? newItem : candidate;

                Match match = Match.builder()
                        .lostItem(lostItem)
                        .foundItem(foundItem)
                        .matchScore(score)
                        .status(MatchStatus.PENDING)
                        .build();

                matchRepository.save(match);

                // Update both items status to MATCHED
                lostItem.setStatus(ItemStatus.MATCHED);
                foundItem.setStatus(ItemStatus.MATCHED);
                itemRepository.save(lostItem);
                itemRepository.save(foundItem);

                // ─── NOTIFY LOST ITEM OWNER ───────────────────────────
                notificationService.sendNotification(
                        lostItem.getUser(),
                        "We found a possible match for your lost item: " + lostItem.getTitle(),
                        NotificationType.MATCH_FOUND,
                        lostItem.getId()
                );
                emailService.sendMatchFoundEmail(
                        lostItem.getUser().getEmail(),
                        lostItem.getUser().getName(),
                        lostItem.getTitle()
                );

                // ─── NOTIFY FOUND ITEM OWNER ──────────────────────────
                notificationService.sendNotification(
                        foundItem.getUser(),
                        "The item you found (" + foundItem.getTitle() + ") may belong to someone!",
                        NotificationType.MATCH_FOUND,
                        foundItem.getId()
                );
                emailService.sendMatchFoundEmail(
                        foundItem.getUser().getEmail(),
                        foundItem.getUser().getName(),
                        foundItem.getTitle()
                );
            }
        }
    }

    //Scoring logic
    private double calculateScore(Item a, Item b) {
        double score = 0.0;

        // Same category = +0.5 (most important)
        if (a.getCategory() != null &&
                a.getCategory().equalsIgnoreCase(b.getCategory())) {
            score += 0.5;
        }

        // Same location = +0.3
        if (a.getLocation() != null &&
                a.getLocation().equalsIgnoreCase(b.getLocation())) {
            score += 0.3;
        }

        // Date is reasonable = +0.2
        // found date should be same or after lost date
        if (a.getDate() != null && b.getDate() != null) {
            Item lostItem  = (a.getType() == ItemType.LOST) ? a : b;
            Item foundItem = (a.getType() == ItemType.FOUND) ? a : b;
            if (!foundItem.getDate().isBefore(lostItem.getDate())) {
                score += 0.2;
            }
        }

        return score;
    }

    //Avoid duplicates
    private boolean isAlreadyMatched(Item newItem, Item candidate, ItemType oppositeType) {
        if (oppositeType == ItemType.LOST) {
            return matchRepository.existsByLostItemIdAndFoundItemId(
                    candidate.getId(), newItem.getId());
        } else {
            return matchRepository.existsByLostItemIdAndFoundItemId(
                    newItem.getId(), candidate.getId());
        }
    }

    //Get All
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    //Get all pending matches
    public List<Match> getPendingMatches() {
        return matchRepository.findByStatus(MatchStatus.PENDING);
    }


    //Get match by id
    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }

    //ADMIN - CONFIRM OR REJECT a match
    public Match updateMatchStatus(Long matchId, MatchStatus newStatus) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setStatus(newStatus);

        if (newStatus == MatchStatus.CONFIRMED) {
            // Reopen both items → OPEN (or keep MATCHED, up to you)
            emailService.sendMatchConfirmedEmail(
                    match.getLostItem().getUser().getEmail(),
                    match.getLostItem().getUser().getName(),
                    match.getLostItem().getTitle()
            );
            emailService.sendMatchConfirmedEmail(
                    match.getFoundItem().getUser().getEmail(),
                    match.getFoundItem().getUser().getName(),
                    match.getFoundItem().getTitle()
            );

        } else if (newStatus == MatchStatus.REJECTED) {
            match.getLostItem().setStatus(ItemStatus.OPEN);
            match.getFoundItem().setStatus(ItemStatus.OPEN);
            itemRepository.save(match.getLostItem());
            itemRepository.save(match.getFoundItem());

            emailService.sendMatchRejectedEmail(
                    match.getLostItem().getUser().getEmail(),
                    match.getLostItem().getUser().getName(),
                    match.getLostItem().getTitle()
            );
            emailService.sendMatchRejectedEmail(
                    match.getFoundItem().getUser().getEmail(),
                    match.getFoundItem().getUser().getName(),
                    match.getFoundItem().getTitle()
            );
        }

        return matchRepository.save(match);
    }


}
