package com.baw.dog_service.controller;

import com.baw.dog_service.dto.DogDTO;
import com.baw.dog_service.request.CreateDogRequest;
import com.baw.dog_service.service.IDogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<DogDTO> createDog(@Valid @RequestBody CreateDogRequest request) {
        DogDTO dog = dogService.createDog(request);
        return new ResponseEntity<>(dog, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DogDTO> updateDog(@Valid @RequestBody CreateDogRequest request, @PathVariable UUID id) {
        DogDTO dog = dogService.updateDog(request, id);
        return ResponseEntity.ok(dog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDog(@PathVariable UUID id) {
        dogService.deleteDog(id);
        return ResponseEntity.ok(Map.of("message", "Dog deactivated successfully"));
    }
}
