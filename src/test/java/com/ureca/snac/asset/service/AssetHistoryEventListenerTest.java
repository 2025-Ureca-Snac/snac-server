package com.ureca.snac.asset.service;

import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.repository.AssetHistoryRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.support.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetHistoryEventListenerTest {

    @InjectMocks
    private AssetHistoryEventListener assetHistoryEventListener;

    @Mock
    private AssetHistoryRepository assetHistoryRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private AssetChangedEvent event;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
        event = TestFixture.createDummyEvent();
    }

    @Test
    void 자산_변경_이벤트_수신시_내역_정상_저장() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        ArgumentCaptor<AssetHistory> history =
                ArgumentCaptor.forClass(AssetHistory.class);

        // when
        assetHistoryEventListener.handleAssetChangedEvent(event);

        // then
        verify(assetHistoryRepository, times(1)).save(history.capture());

        AssetHistory savedHistory = history.getValue();
        assertThat(savedHistory.getMember()).isEqualTo(member);
        assertThat(savedHistory.getTitle()).isEqualTo(event.title());
    }

    @Test
    void 이벤트_존재하지않는_회원_있으면_예외_발생() {
        // given
        AssetChangedEvent invalidEvent =
                TestFixture.createDummyEventWithMemberId(999L);
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // then
        assertThrows(MemberNotFoundException.class, () ->
                assetHistoryEventListener.handleAssetChangedEvent(invalidEvent));

        // then
        verify(assetHistoryRepository, never()).save(any(AssetHistory.class));
    }

    @Test
    void 내역_저장중_DB_오류_발생_예외_던지기() {
        // given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(assetHistoryRepository.save(any(AssetHistory.class)))
                .thenThrow(new RuntimeException("디비 제약 조건"));

        // when then
        assertThrows(RuntimeException.class, () ->
                assetHistoryEventListener.handleAssetChangedEvent(event));
    }
}