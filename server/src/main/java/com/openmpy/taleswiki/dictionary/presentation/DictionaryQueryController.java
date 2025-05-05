package com.openmpy.taleswiki.dictionary.presentation;

import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop10Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/dictionaries")
@RestController
public class DictionaryQueryController {

    private final DictionaryQueryService dictionaryQueryService;

    @GetMapping("/latest-modified")
    public ResponseEntity<DictionaryGetTop10Response> getTop20Dictionaries() {
        final DictionaryGetTop10Response response = dictionaryQueryService.getTop20Dictionaries();
        return ResponseEntity.ok(response);
    }
}
