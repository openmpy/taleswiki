package com.openmpy.taleswiki.admin.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.admin.dto.request.AdminBlacklistSaveRequest;
import com.openmpy.taleswiki.admin.dto.request.AdminSigninRequest;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.dictionary.application.DictionarySearchService;
import com.openmpy.taleswiki.dictionary.domain.constants.DictionaryStatus;
import com.openmpy.taleswiki.dictionary.domain.entity.Dictionary;
import com.openmpy.taleswiki.dictionary.domain.entity.DictionaryHistory;
import com.openmpy.taleswiki.dictionary.domain.repository.DictionaryRepository;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.TestcontainerSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AdminCommandServiceTest extends TestcontainerSupport {

    @Autowired
    private AdminCommandService adminCommandService;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private BlacklistRepository blacklistRepository;

    @MockitoBean
    private DictionarySearchService dictionarySearchService;

    @DisplayName("[통과] 어드민 계정에 로그인한다.")
    @Test
    void admin_command_service_test_01() {
        // given
        final AdminSigninRequest request = new AdminSigninRequest("admin", "test");

        // when
        final String token = adminCommandService.signin(request);

        // then
        assertThat(token).isEqualTo("success");
    }

    @DisplayName("[통과] 사전 상태를 변경한다.")
    @Test
    void admin_command_service_test_02() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        // when
        adminCommandService.changeDictionaryStatus("success", savedDictionary.getId(), "read_only");

        // then
        assertThat(savedDictionary.getStatus()).isEqualTo(DictionaryStatus.READ_ONLY);
    }

    @DisplayName("[통과] 사전 기록 상태를 숨김 처리 한다.")
    @Test
    void admin_command_service_test_03() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        // stub
        doNothing().when(dictionarySearchService).delete(anyLong());

        // when
        adminCommandService.changeDictionaryStatus("success", dictionaryHistory.getId(), "hidden");

        // then
        assertThat(savedDictionary.getStatus()).isEqualTo(DictionaryStatus.HIDDEN);
    }

    @DisplayName("[통과] 사전을 삭제한다.")
    @Test
    void admin_command_service_test_04() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);

        // stub
        doNothing().when(dictionarySearchService).delete(anyLong());

        // when
        adminCommandService.delete("success", savedDictionary.getId());

        // then
        final long count = dictionaryRepository.count();

        assertThat(count).isZero();
    }

    @DisplayName("[통과] 사전 기록 상태를 변경한다.")
    @Test
    void admin_command_service_test_05() {
        // given
        final Dictionary dictionary = Fixture.createDictionary();
        final Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        final DictionaryHistory dictionaryHistory = savedDictionary.getCurrentHistory();

        // when
        adminCommandService.changeDictionaryHistoryStatus("success", dictionaryHistory.getId(), "read_only");

        // then
        assertThat(dictionaryHistory.getStatus()).isEqualTo(DictionaryStatus.READ_ONLY);
    }

    @DisplayName("[통과] 클라이언트 IP를 블랙리스트에 저장한다.")
    @Test
    void admin_command_service_test_06() {
        // given
        final AdminBlacklistSaveRequest request = new AdminBlacklistSaveRequest("127.0.0.1", "사유");

        // when
        adminCommandService.saveBlacklist("success", request);

        // then
        final Blacklist blacklist = blacklistRepository.findAll().getFirst();

        assertThat(blacklist.getIp()).isEqualTo("127.0.0.1");
        assertThat(blacklist.getReason()).isEqualTo("사유");
    }

    @DisplayName("[통과] 블랙리스트에 저장된 클라이언트 IP를 제거한다.")
    @Test
    void admin_command_service_test_07() {
        // given
        final Blacklist blacklist = blacklistRepository.save(Blacklist.create("127.0.0.1", "사유"));

        // when
        adminCommandService.deleteBlacklist("success", blacklist.getId());

        // then
        final long count = blacklistRepository.count();

        assertThat(count).isZero();
    }

    @DisplayName("[예외] 어드민 아이디가 일치하지 않아 로그인에 실패한다.")
    @Test
    void 예외_admin_command_service_test_01() {
        // given
        final AdminSigninRequest request = new AdminSigninRequest("wrong", "test");

        // when & then
        assertThatThrownBy(() -> adminCommandService.signin(request))
                .isInstanceOf(CustomException.class)
                .hasMessage("닉네임 또는 패스워드를 다시 한번 확인해주시길 바랍니다.");
    }

    @DisplayName("[예외] 어드민 비밀번호가 일치하지 않아 로그인에 실패한다.")
    @Test
    void 예외_admin_command_service_test_02() {
        // given
        final AdminSigninRequest request = new AdminSigninRequest("admin", "wrong");

        // when & then
        assertThatThrownBy(() -> adminCommandService.signin(request))
                .isInstanceOf(CustomException.class)
                .hasMessage("닉네임 또는 패스워드를 다시 한번 확인해주시길 바랍니다.");
    }

    @DisplayName("[예외] 블랙리스트에 이미 등록된 클라이언트 IP이다.")
    @Test
    void 예외_admin_command_service_test_03() {
        // given
        blacklistRepository.save(Blacklist.create("127.0.0.1", "사유"));

        final AdminBlacklistSaveRequest request = new AdminBlacklistSaveRequest("127.0.0.1", "사유");

        // when & then
        assertThatThrownBy(() -> adminCommandService.saveBlacklist("success", request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 등록된 IP입니다.");
    }

    @DisplayName("[예외] 블랙리스트에서 찾을 수 없는 클라이언트 IP라서 삭제할 수 없다.")
    @Test
    void 예외_admin_command_service_test_04() {
        // when & then
        assertThatThrownBy(() -> adminCommandService.deleteBlacklist("success", 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 블랙리스트 번호입니다.");
    }
}