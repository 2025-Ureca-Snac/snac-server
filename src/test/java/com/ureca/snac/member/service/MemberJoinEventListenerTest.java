package com.ureca.snac.member.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.event.MemberJoinEvent;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.support.TestFixture;
import com.ureca.snac.wallet.exception.WalletAlreadyExistsException;
import com.ureca.snac.wallet.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberJoinEventListenerTest {

    @InjectMocks
    private MemberJoinEventListener memberJoinEventListener;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AssetChangedEventFactory assetChangedEventFactory;

    @Mock
    private AssetHistoryEventPublisher assetHistoryEventPublisher;

    private Member member;
    private MemberJoinEvent event;
    private AssetChangedEvent dummyAssetEvent;

    @BeforeEach
    void setUp() {
        member = TestFixture.createTestMember();
        event = new MemberJoinEvent(member.getId());
        dummyAssetEvent = TestFixture.createDummyEvent();

        when(memberRepository.findById(member.getId())).
                thenReturn(Optional.of(member));
    }

    @Test
    void 회원가입_이벤트_수신시_지갑생성_및_포인트지급_정상_호출() {
        // given
        when(assetChangedEventFactory.createForSignupBonus(anyLong(), anyLong(), anyLong()))
                .thenReturn(dummyAssetEvent);

        // when
        memberJoinEventListener.handleMemberJoinEvent(event);

        // then
        verify(walletService, times(1)).createWallet(member);
        verify(walletService, times(1)).depositPoint(member.getId(), 1000L);
        verify(assetHistoryEventPublisher, times(1)).publish(dummyAssetEvent);
    }

    @Test
    void 멱등성_케이스_지갑이_이미_존재하면_지갑_생성은_건너뛰고_포인트만_지급() {
        // given
        doThrow(new WalletAlreadyExistsException()).when(walletService).createWallet(member);

        when(assetChangedEventFactory.createForSignupBonus(anyLong(), anyLong(), anyLong()))
                .thenReturn(dummyAssetEvent);

        // when
        memberJoinEventListener.handleMemberJoinEvent(event);

        // then
        verify(walletService, times(1)).createWallet(member);
        verify(walletService, times(1)).depositPoint(member.getId(), 1000L);
        verify(assetHistoryEventPublisher, times(1)).publish(dummyAssetEvent);
    }

    @Test
    void 포인트_지급중_예상치_못한_DB_오류_예외_발생() {
        // given
        doThrow(new RuntimeException("DB 연결 오류")).when(walletService).depositPoint(anyLong(), anyLong());

        // when
        Assertions.assertThrows(RuntimeException.class,
                () -> memberJoinEventListener.handleMemberJoinEvent(event));

        // then
        verify(walletService, times(1)).createWallet(member);
        verify(walletService, times(1)).depositPoint(member.getId(), 1000L);
        // 포인트 지급 실패했으므로 자산 기록 안함
        verify(assetHistoryEventPublisher, never()).publish(dummyAssetEvent);
    }
}