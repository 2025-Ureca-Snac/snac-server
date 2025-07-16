# 유레카 비대면 융합프로젝트 1조

![헤더 (2)](https://github.com/user-attachments/assets/78461da4-dee7-4be4-882e-df732ee39129)

## Snac (Share Network Allocation & Commerce)

![vc12](https://github.com/user-attachments/assets/4523d261-9049-4efc-ab5d-ecc28d3e8fd3)


[🔗팀노션]()
[🎨피그마]()
[🌐배포사이트]()
[👨‍🏫시연영상]()


# 🗂️ 디렉토리 구조

```
com.ureca.snac
├── auth           # 인증/인가 (로그인, 회원가입, 소셜 연동 등)
├── board          # 게시글 관련 (카드 등록, 상태 등)
├── common         # 공통 유틸, 예외 처리, 응답 구조 등
├── config         # 전역 설정 (Swagger, S3, Jpa)
├── finance        # 계좌 도메인
├── infra          # 외부 인프라 연동
├── member         # 사용자 도메인
├── money          # 머니 관리 (충전, 환불, 이체 등)
├── notification   # 알림 기능 (WebSocket,  RabbitMQ 등)
├── payment        # 결제 처리 (PG사 연동, 결제 승인 등)
├── swagger        # Swagger 관련 설정 및 문서화
├── trade          # 거래 처리 (구매/판매 흐름, 상태 전환 등)
└── wallet         # 지갑 및 잔액 기록
```

# 🔰 실행 방법
<img width="1407" height="948" alt="Image" src="https://github.com/user-attachments/assets/5aa7c1cf-c36b-4a47-95e4-6b18ee513a68" />

## 📚 Tech Stack

### 💻 BE Development

[![My Skills](https://skillicons.dev/icons?i=idea,java,spring,gradle,hibernate,mysql,redis,rabbitmq,docker,aws&theme=dark)](https://skillicons.dev)

### ⌛ Developed Period

#### 2025.6.30 ~ 2025.8.8 (39 days)

# 👩‍💻 팀원

<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/iju42829"><img src="https://avatars.githubusercontent.com/u/116072376?v=4" width="120px;" alt=""/><br /><b>이재윤</b></a><br /><p>개발</p></td>
      <td align="center"><a href="https://github.com/Iamcalmdown"><img src="https://avatars.githubusercontent.com/u/144317474?v=4" width="120px;" alt=""/><br /><b>정동현</b></a><br /><p>개발</p></td>
      <td align="center"><a href="https://github.com/mike7643"><img src="https://avatars.githubusercontent.com/u/121170730?v=4" width="120px;" alt=""/><br /><b>정유민</b></a><br /><p>개발</p></td>
      <td align="center"><a href="https://github.com/seokjuun"><img src="https://avatars.githubusercontent.com/u/45346977?v=4" width="120px;" alt=""/><br /><b>홍석준</b></a><br /><p>개발</p></td>
    </tr>
  </tbody>
</table>

# 🎯 커밋 컨벤션

- `feat`: Add a new feature
- `fix`: Bug fix
- `docs`: Documentation updates
- `style`: Code formatting, missing semicolons, cases where no code change is involved
- `refactor`: Code refactoring
- `test`: Test code, adding refactoring tests
- `build`: Build task updates, package manager updates
