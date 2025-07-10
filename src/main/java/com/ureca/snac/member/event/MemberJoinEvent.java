package com.ureca.snac.member.event;

import com.ureca.snac.member.Member;

/**
 * 회원 가입 성공 이벤트를 나타내는 Record
 *
 * @param member 새로 가입한 회원
 */
public record MemberJoinEvent(Member member) {

}
