package com.findit.lostfoundsystem.controller;


import com.findit.lostfoundsystem.dto.MatchResponseDTO;
import com.findit.lostfoundsystem.enums.MatchStatus;
import com.findit.lostfoundsystem.mapper.MatchMapper;
import com.findit.lostfoundsystem.model.Match;
import com.findit.lostfoundsystem.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchMapper  matchMapper;


    // Admin sees all matches
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponseDTO>> getAllMatches() {
        List<MatchResponseDTO> response = matchService.getAllMatches()
                .stream()
                .map(matchMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Admin sees only pending matches
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MatchResponseDTO>> getPendingMatches() {
        List<MatchResponseDTO> response = matchService.getPendingMatches()
                .stream()
                .map(matchMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Admin sees one match details
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchResponseDTO> getMatchById(@PathVariable Long id) {
        Match match = matchService.getMatchById(id);
        return ResponseEntity.ok(matchMapper.toResponseDTO(match));
    }

    // Admin confirms or rejects a match
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MatchResponseDTO> updateMatchStatus(
            @PathVariable Long id,
            @RequestParam MatchStatus status) {

        Match updated = matchService.updateMatchStatus(id, status);
        return ResponseEntity.ok(matchMapper.toResponseDTO(updated));
    }

}
