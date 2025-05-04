package com.openmpy.taleswiki.dictionary.presentation;

import com.openmpy.taleswiki.dictionary.application.DictionaryCommandService;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/dictionaries")
@RestController
public class DictionaryCommandController {

    private final DictionaryCommandService dictionaryCommandService;

    @PostMapping
    public ResponseEntity<Void> save(
            final HttpServletRequest servletRequest, @RequestBody final DictionarySaveRequest request
    ) {
        dictionaryCommandService.save(servletRequest, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{dictionaryId}")
    public ResponseEntity<Void> update(
            @PathVariable final Long dictionaryId,
            final HttpServletRequest servletRequest,
            @RequestBody final DictionaryUpdateRequest request
    ) {
        dictionaryCommandService.update(dictionaryId, servletRequest, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{dictionaryId}")
    public ResponseEntity<Void> changeStatus(
            @PathVariable final Long dictionaryId,
            @RequestParam(value = "status") final String status
    ) {
        dictionaryCommandService.changeStatus(dictionaryId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{dictionaryId}")
    public ResponseEntity<Void> delete(@PathVariable final Long dictionaryId) {
        dictionaryCommandService.delete(dictionaryId);
        return ResponseEntity.noContent().build();
    }
}
