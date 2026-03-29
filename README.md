# Loan Wisely Backend

Engine(Back-end)

추천 판단이 실제로 이루어지는 핵심 영역입니다.
Spring Boot 기반으로 구성되어 있습니다.

- 핵심 역할
- 입력 검증
- 리스크 점수 산출
- 상품 필터링
- 점수 계산 및 정렬
- 추천 결과 생성

추천 실행 흐름
validate → filter → score → sort

Backend 구조 (Spring)
domain
 ├─ user
 ├─ product
 ├─ consent
 └─ recommend

이 프로젝트는 단순 CRUD 서비스를 넘어
금융 의사결정 지원 시스템을 목표로 합니다.

핵심 원칙
추천은 ENGINE에서만 수행
데이터는 판단하지 않음
정책은 MANAGEMENT에서만 정의
