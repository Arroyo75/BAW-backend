package com.baw.dog_service.controller;

import com.baw.dog_service.dto.DogDTO;
import com.baw.dog_service.repository.DogRepository;
import com.baw.dog_service.request.CreateDogRequest;
import com.baw.dog_service.request.DogFilterRequest;
import com.baw.dog_service.service.IDogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dogs")
public class DogController {

    private final IDogService dogService;

    @GetMapping("/{id}")
    public ResponseEntity<DogDTO> getDogById(@PathVariable UUID id) {
        DogDTO dog = dogService.getDogById(id);
        return ResponseEntity.ok(dog);
    }

    @GetMapping()
    public ResponseEntity<Page<DogDTO>> getDogs(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        DogFilterRequest filter = new DogFilterRequest();
        filter.setSearch(search);
        filter.setPage(page);
        filter.setSize(size);

        Page<DogDTO> dogs = dogService.getDogs(filter);
        return ResponseEntity.ok(dogs);
    }

    @GetMapping("/{ownerId}/owner")
    public ResponseEntity<Page<DogDTO>> getDogsByOwnerId(
            @PathVariable UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DogDTO> dogs = dogService.getDogsByOwnerId(ownerId, page, size);
        return ResponseEntity.ok(dogs);
    }

    @PostMapping
    public ResponseEntity<DogDTO> createDog(
            @Valid @RequestBody CreateDogRequest request,
            Authentication authentication
    ) {
        UUID ownerId = UUID.fromString(authentication.getName());
        DogDTO dog = dogService.createDog(request, ownerId);
        return new ResponseEntity<>(dog, HttpStatus.CREATED);
    }

    @PreAuthorize("@dogSecurity.isOwner(#id, authentication.name) || hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DogDTO> updateDog(@Valid @RequestBody CreateDogRequest request, @PathVariable UUID id) {
        DogDTO dog = dogService.updateDog(request, id);
        return ResponseEntity.ok(dog);
    }

    @PreAuthorize("@dogSecurity.isOwner(#id, authentication.name) || hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDog(@PathVariable UUID id) {
        dogService.deleteDog(id);
        return ResponseEntity.ok(Map.of("message", "Dog deactivated successfully"));
    }
}
