package com.openmpy.taleswiki.dictionary.dto.response;

import com.openmpy.taleswiki.common.util.CharacterUtil;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record DictionaryGetGroupResponse(List<DictionaryGetGroupItemsResponse> groups) {

    public record DictionaryGetGroupItemResponse(Long currentHistoryId, String title, String status) {
    }

    public record DictionaryGetGroupItemsResponse(char initial, List<DictionaryGetGroupItemResponse> dictionaries) {

        public static List<DictionaryGetGroupItemsResponse> of(final List<Dictionary> dictionaries) {
            final Map<Character, List<DictionaryGetGroupItemResponse>> groupedDictionaries = dictionaries.stream()
                    .map(it -> new DictionaryGetGroupItemResponse(
                            it.getCurrentHistory().getId(),
                            it.getTitle(),
                            it.getStatus().name()
                    ))
                    .collect(Collectors.groupingBy(it ->
                            CharacterUtil.getInitialGroup(it.title())
                    ));

            return groupedDictionaries.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> new DictionaryGetGroupItemsResponse(entry.getKey(), entry.getValue()))
                    .toList();
        }
    }
}
