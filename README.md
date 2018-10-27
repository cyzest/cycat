# cycat
Java 8 based Simple Web Application Server

### 스펙 구현 상황
1. HTTP/1.1 의 Host 헤더를 해석하세요. (구현 O)
2. 다음 사항을 설정 파일로 관리하세요. (구현 O)
3. 403, 404, 500 오류를 처리합니다. (구현 O)
4. 다음과 같은 보안 규칙을 둡니다. (구현 O)
5. logback 프레임워크 http://logback.qos.ch/를 이용하여 다음의 로깅 작업을 합니다. (구현 O)
6. 간단한 WAS 를 구현합니다. (구현 O)
7. 현재 시각을 출력하는 SimpleServlet 구현체를 작성하세요. (구현 O)
8. 앞에서 구현한 여러 스펙을 검증하는 테스트 케이스를 JUnit4 를 이용해서 작성하세요.

### 빌드 및 실행
```console
$ mvn clean package
$ java -jar ./target/cycat-1.0.0.jar
$ java -jar ./target/cycat-1.0.0.jar --config ./conf/config.json
```