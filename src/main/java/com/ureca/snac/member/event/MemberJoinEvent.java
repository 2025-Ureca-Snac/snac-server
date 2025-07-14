package com.ureca.snac.member.event;

/**
 * 회원 가입 성공 이벤트를 나타내는 Record
 *
 * @param memberId 새로 가입한 회원 Id
 */
public record MemberJoinEvent(Long memberId) {

}
