# 코틀린 practice를 위한 조직도 API

### 사용기술
+ Kotlin
+ Junit5, MockK, 
+ Spring Boot, JPA
+ Library
  + mapstruct
+ Docker
+ Mysql
+ Spring doc
  + {application-ip}:{port}/swagger-ui.html
+ Spring flyway

### 실행방법
1. `./gradlew clean bootJar`
2. `docker compose up -d --build` (도커이미지의 캐싱때문에 --build option을 붙이는것을 추천)

### 주의사항
1. 실제 DB와 연결을 해서 사용할때, 최상위 그룹을 조직도로 만들고 사용하세요.

### 추가할 예정인 것들
1. spring-doc에 Controller에 노출된 API 리스트들 제공
2. spring-boot-actuator
   1. 디폴트 엔드포인트를 제외하고는, 권한을 가지고 접근할 수 있도록합니다.
3. prometheus
4. 권한 관련 모듈을 추가한후, 조직도 API 접근을 제한합니다.
   1. Spring Security - 세션 혹은 JWT을 사용합니다. 
5. Spring-Cloud-GateWay