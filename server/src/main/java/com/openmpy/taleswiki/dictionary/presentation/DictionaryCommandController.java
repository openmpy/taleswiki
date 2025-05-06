package com.openmpy.taleswiki.dictionary.presentation;

import com.openmpy.taleswiki.dictionary.application.DictionaryCommandService;
import com.openmpy.taleswiki.dictionary.dto.request.DictionarySaveRequest;
import com.openmpy.taleswiki.dictionary.dto.request.DictionaryUpdateRequest;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryUpdateResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<DictionaryUpdateResponse> update(
            @PathVariable final Long dictionaryId,
            final HttpServletRequest servletRequest,
            @RequestBody final DictionaryUpdateRequest request
    ) {
        final DictionaryUpdateResponse response = dictionaryCommandService.update(
                dictionaryId, servletRequest, request
        );
        return ResponseEntity.ok(response);
    }
}
