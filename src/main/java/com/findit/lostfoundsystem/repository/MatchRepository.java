package com.findit.lostfoundsystem.repository;

import com.findit.lostfoundsystem.enums.MatchStatus;
import com.findit.lostfoundsystem.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match,Long> {

    // Get all matches by status
    List<Match> findByStatus(MatchStatus status);

    // Check if a match already exists between two items
    boolean existsByLostItemIdAndFoundItemId(
            Long lostItemId,
            Long foundItemId
    );
}
