package com.openmpy.taleswiki.dictionary.presentation;

import com.openmpy.taleswiki.dictionary.application.DictionaryQueryService;
import com.openmpy.taleswiki.dictionary.application.DictionarySearchService;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetGroupResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetHistoriesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetPopularResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetRandomResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetTop20Response;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryGetVersionsResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionaryHistoryResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/dictionaries")
@RestController
public class DictionaryQueryController {

    private final DictionaryQueryService dictionaryQueryService;
    private final DictionarySearchService dictionarySearchService;

    @GetMapping("/latest-modified")
    public ResponseEntity<DictionaryGetTop20Response> getTop20Dictionaries() {
        final DictionaryGetTop20Response response = dictionaryQueryService.getTop20Dictionaries();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{categoryName}")
    public ResponseEntity<DictionaryGetGroupResponse> getGroupDictionaries(@PathVariable final String categoryName) {
        final DictionaryGetGroupResponse response = dictionaryQueryService.getGroupDictionaries(categoryName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/histories/{dictionaryHistoryId}")
    public ResponseEntity<DictionaryHistoryResponse> get(
            final HttpServletRequest servletRequest,
            @PathVariable final Long dictionaryHistoryId
    ) {
        final DictionaryHistoryResponse response = dictionaryQueryService.get(servletRequest, dictionaryHistoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dictionaryId}/history")
    public ResponseEntity<DictionaryGetHistoriesResponse> getHistories(@PathVariable final Long dictionaryId) {
        final DictionaryGetHistoriesResponse response = dictionaryQueryService.getHistories(dictionaryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dictionaryId}/versions")
    public ResponseEntity<DictionaryGetVersionsResponse> getVersions(@PathVariable final Long dictionaryId) {
        final DictionaryGetVersionsResponse response = dictionaryQueryService.getVersions(dictionaryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{dictionaryId}/versions/{version}")
    public ResponseEntity<DictionaryGetVersionResponse> getVersion(
            @PathVariable final Long dictionaryId,
            @PathVariable final Long version
    ) {
        final DictionaryGetVersionResponse response = dictionaryQueryService.getVersion(dictionaryId, version);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<DictionarySearchDictionariesResponse> searchDictionaries(
            @RequestParam(value = "title") final String title
    ) {
        final DictionarySearchDictionariesResponse response = dictionarySearchService.searchByTitle(title);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/random")
    public ResponseEntity<DictionaryGetRandomResponse> getRandomDictionary() {
        final DictionaryGetRandomResponse response = dictionaryQueryService.getRandomDictionary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<DictionaryGetPopularResponse> getPopular() {
        final DictionaryGetPopularResponse response = dictionaryQueryService.getPopular();
        return ResponseEntity.ok(response);
    }
}
