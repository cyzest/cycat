# cycat

[![Build Status](https://travis-ci.org/cyzest/cycat.svg?branch=master)](https://travis-ci.org/cyzest/cycat)
[![Code Coverage](https://codecov.io/gh/cyzest/cycat/branch/master/graph/badge.svg)](https://codecov.io/gh/cyzest/cycat)

### Java 8 based Simple Web Application Server
* HTTP 기반 통신을 주고받기 위한 웹서버의 구조 이해
* Java 기반 서블릿 컨테이너의 구조 이해

### 빌드 및 실행
* 빌드툴은 Maven을 활용
* 빌드 시 Java8과 Maven이 미리 설치되어 있어야 한다.
```console
$ cd cycat
$ mvn clean package
$ java -jar ./target/cycat-1.0.0.jar
$ java -jar ./target/cycat-1.0.0.jar --config ./conf/config.json
```
* http://localhost:8080 으로 접속하여 확인
* configuration 정보를 설정하지 않을 경우 디폴트 설정 적용
* 현재 로그파일은 서버가 실행된 폴더에 logs 폴더가 자동으로 생성됨 (추후 설정파일로 변경가능 하도록 고도화 필요)

### configuration json file
```json
{
  "port":8080,
  "thread":10,
  "hosts":[
    {
      "host":"cyzest.com",
      "root":"html",
      "index":"index.html",
      "errorPage":{
        "403":"403.html",
        "404":"404.html",
        "500":"500.html"
      },
      "servletMapping":{
        "/time":"com.cyzest.cycat.service.CurrentTimeServlet"
      }
    }
  ]
}
```
* port : Listen Port
* thread : 소켓 커넥션 스레드풀 개수
* hosts : 도메인 별 설정 분기
* host : 도메인 (hosts 없을 경우 디폴트 localhost)
* root : Document Root 설정 (디폴트 html)
* index : 초기 페이지 설정 (디폴트 index.html)
* errorPage : 에러 페이지 설정 (Key: HTTP Status Code) (Value: 파일명)
* servletMapping : 서블릿 맵핑 설정 (Key: 맵핑URL) (Value: 서블릿클래스명)

### 앞으로의 계획
* 서블릿클래스를 외부에서 주입받을 수 있도록 고도화
* 일반적인 서블릿 컨테이너가 지원하는 Filter 기능 추가
* Non-Blocking 커넥터 지원