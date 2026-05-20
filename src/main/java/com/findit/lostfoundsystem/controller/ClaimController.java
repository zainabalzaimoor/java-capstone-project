package com.findit.lostfoundsystem.controller;


import com.findit.lostfoundsystem.dto.ClaimRequestDTO;
import com.findit.lostfoundsystem.dto.ClaimResponseDTO;
import com.findit.lostfoundsystem.enums.ClaimStatus;
import com.findit.lostfoundsystem.mapper.ClaimMapper;
import com.findit.lostfoundsystem.model.Claim;
import com.findit.lostfoundsystem.service.ClaimService;
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
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;
    private final ClaimMapper claimMapper;

    // POST /api/claims
    // User files a claim
    @PostMapping
    public ResponseEntity<ClaimResponseDTO> createClaim(
            @RequestBody ClaimRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Claim claim = claimService.createClaim(request, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimMapper.toResponseDTO(claim));
    }

    // GET /api/claims/my-claims
    // User sees their own claims
    @GetMapping("/my-claims")
    public ResponseEntity<List<ClaimResponseDTO>> getMyClaims(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<ClaimResponseDTO> response = claimService.getMyClaims(email)
                .stream()
                .map(claimMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/claims
    // Admin sees all claims
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaims(
            @RequestParam(required = false) ClaimStatus status) {

        List<Claim> claims = (status != null)
                ? claimService.getClaimsByStatus(status)
                : claimService.getAllClaims();

        List<ClaimResponseDTO> response = claims.stream()
                .map(claimMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // GET /api/claims/{id}
    // Admin sees one claim
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponseDTO> getClaimById(
            @PathVariable Long id) {
        Claim claim = claimService.getClaimById(id);
        return ResponseEntity.ok(claimMapper.toResponseDTO(claim));
    }

    // PATCH /api/claims/{id}/status
    // Admin approves or rejects
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClaimResponseDTO> updateClaimStatus(
            @PathVariable Long id,
            @RequestParam ClaimStatus status) {

        Claim updated = claimService.updateClaimStatus(id, status);
        return ResponseEntity.ok(claimMapper.toResponseDTO(updated));
    }
}
