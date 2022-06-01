# 코틀린 practice를 위한 조직도 API

### 사용기술
+ Kotlin
+ Junit5, MockK, 
+ Spring Boot, JPA
+ Library
  + mapstruct
+ Docker
+ Maria db
+ Spring doc
  + {application-ip}:{port}/swagger-ui.html

### 실행방법
1. `./gradlew clean bootJar`
2. `docker build -t group .`
3. `docker-compose up -d`

### 주의사항
1. 실제 DB와 연결을 해서 사용할때, 최상위 그룹을 조직도로 만들고 사용하세요.