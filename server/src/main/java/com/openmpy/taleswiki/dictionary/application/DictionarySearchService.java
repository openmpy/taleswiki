package com.openmpy.taleswiki.dictionary.application;

import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse;
import com.openmpy.taleswiki.dictionary.dto.response.DictionarySearchDictionariesResponse.DictionarySearchDictionariesItemResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class DictionarySearchService {

    private static final String INDEX = "dictionaries";

    private final RestClient meiliSearchRestClient;

    @PostConstruct
    public void init() {
        final List<String> searchableAttributes = List.of("title");

        meiliSearchRestClient.put()
                .uri("/indexes/{index}/settings/searchable-attributes", INDEX)
                .body(searchableAttributes)
                .retrieve()
                .toBodilessEntity();
    }

    public DictionarySearchDictionariesResponse searchByTitle(final String keyword) {
        final Map<String, Object> requestBody = Map.of(
                "q", keyword,
                "limit", 1000
        );

        return meiliSearchRestClient.post()
                .uri("/indexes/{index}/search", INDEX)
                .body(requestBody)
                .retrieve()
                .body(DictionarySearchDictionariesResponse.class);
    }

    public void indexDictionaries(final List<DictionarySearchDictionariesItemResponse> items) {
        meiliSearchRestClient.post()
                .uri("/indexes/{index}/documents", INDEX)
                .contentType(MediaType.APPLICATION_JSON)
                .body(items)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteDictionaries() {
        meiliSearchRestClient.delete()
                .uri("/indexes/{index}/documents", INDEX)
                .retrieve()
                .toBodilessEntity();
    }
}
