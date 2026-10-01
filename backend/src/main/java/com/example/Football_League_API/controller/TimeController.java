package com.example.Football_League_API.controller;

import com.example.Football_League_API.dto.request.TimeRequestDto;
import com.example.Football_League_API.dto.response.TimeResponseDto;
import com.example.Football_League_API.service.TimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/times")
@RequiredArgsConstructor
public class TimeController {

    private final TimeService service;

    @GetMapping
    public ResponseEntity<List<TimeResponseDto>> findAll() {
        List<TimeResponseDto> times = service.findAll();
        return ResponseEntity.ok(times);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeResponseDto> findById(@PathVariable Long id) {
        TimeResponseDto time = service.findById(id);
        return ResponseEntity.ok(time);
    }

    @PostMapping
    public ResponseEntity<TimeResponseDto> create(@Valid @RequestBody TimeRequestDto dto) {
        TimeResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TimeRequestDto dto) {
        TimeResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
