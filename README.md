# 유레카 비대면 융합프로젝트 1조

## Snac (Share Network Allocation & Commerce)

![헤더 (2)](https://github.com/user-attachments/assets/78461da4-dee7-4be4-882e-df732ee39129)

## 기획

### 남는 통신 데이터를 쉽고 안전하게 사고파는 실시간 데이터 거래 플랫폼, Snac!

Snac은 이런 서비스예요.

실시간 거래: 판매자와 구매자가 즉시 연결되어, 남는 데이터를 빠르고 쉽게 거래할 수 있습니다.
거래 안전성과 익명성: 개인정보 노출 없이, 안심하고 거래할 수 있도록 익명 매칭과 안전 결제 시스템을 도입했습니다.
시장 데이터 약 300건을 조사하여 불편함을 느낀 20명 중 18명이 빠른 응답을, 2명이 익명성 보장을 가장 중요한 가치로 꼽았습니다.

# 디렉토리 구조

```
com.ureca.snac
├── asset          # 자산 내역 (스낵 머니 / 포인트) 
├── auth           # 인증/인가 (로그인, 회원가입, 소셜 연동 등)
├── board          # 게시글 관련 (카드 등록, 상태 등)
├── common         # 공통 유틸, 예외 처리, 응답 구조 등
├── config         # 전역 설정 (Swagger, S3, Jpa, RabbitMQ)
├── dev            # 비밀통로 (작업 단축 API)
├── favorite       # 단골 (CRUD)
├── finance        # 계좌, 은행 도메인
├── infra          # Toss payments Api
├── member         # 사용자 도메인
├── money          # 머니 충전 요청 (주문서)
├── mypage         # 마이페이지 관련 기능
├── notification   # 알림 기능 (WebSocket,  RabbitMQ 등)
├── payment        # 결제 처리 (결제 승인 및 취소, 누락)
├── settlement     # 정산 (판매 대금 정산)
├── swagger        # Swagger 관련 설정 및 문서화
├── trade          # 거래 처리 (구매/판매 흐름, 상태 전환 등)
└── wallet         # 지갑 및 잔액 기록
```

# ERD

<img width="2101" height="1157" alt="Image" src="https://github.com/user-attachments/assets/bead419f-1e1b-428e-8a8e-f354a9942de0" />

# 기술 스택

## 백엔드

![BE](https://github.com/user-attachments/assets/21e6a9fb-7fe4-4bbb-9bcd-3ff51b621c40)

## 인프라

![Infra](https://github.com/user-attachments/assets/c2f28915-222a-4fef-91ad-4f4fa29275f5)

# 인프라 아키텍쳐

![Image](https://github.com/user-attachments/assets/972b9bb2-0cf0-4ff8-960a-91d705505468)

# 팀원

***
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/iju42829"><img src="https://avatars.githubusercontent.com/u/116072376?v=4" width="120px;" alt=""/><br /><b>이재윤</b></a><br /></td>
      <td align="center"><a href="https://github.com/Iamcalmdown"><img src="https://avatars.githubusercontent.com/u/144317474?v=4" width="120px;" alt=""/><br /><b>정동현</b></a><br /></td>
      <td align="center"><a href="https://github.com/mike7643"><img src="https://avatars.githubusercontent.com/u/121170730?v=4" width="120px;" alt=""/><br /><b>정유민</b></a><br /></td>
      <td align="center"><a href="https://github.com/seokjuun"><img src="https://avatars.githubusercontent.com/u/45346977?v=4" width="120px;" alt=""/><br /><b>홍석준</b></a><br /></td>
    </tr>
  </tbody>
</table>

## ⌛ Developed Period

### 2025.6.30 ~ 2025.8.8 (39 days)
