# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답

알게 된 것들
1. 자바스크립트와 다르게 String은 불변 객체이기 때문에 == 비교 연산자로 같은 지 검증 할 수 없음. equals 메소드를 사용해야 함.
2. Thread란 스레드를 만들고 관리하기 위한 클래스. start 메소드를 호출하면 run() 메소드가 실행된다. run 메소드는 새로운 스레드를 만들고 실행된다. 
3. InputStream이란? 바이너리 데이터를 읽기 위한 추상 클래스. 바이트 단위로 데이터를 읽는다. 주로 문자, 파일, 네트워크 데이터 등을 처리할 때 byte 데이터를 읽을 때 사용.
4. OutputStream이란? 데이터를 출력하기 위한 추상 클래스. 바이트 단위로 출력하는 기본 방법 제공.
5. try with resource 구문을 사용하면 자동으로 close 메소드를 호출한다
6. BufferedRedader는 문자스트림을 더 효율적으로 사용할 수 잇도록 하는 클래스. 기존 메모리에 올려서 여러번 읽는 것과 다르게 데이터를 버퍼 메모리에 한꺼번에 올리고 조금씩 읽어나가는 구조
7. 막히는 점: favicon 요청과 GET resource path를 어떻게 구분짓는데 좋을까
8. 여러가지 랜덤한 path 로 요청이 올 경우 응답이 정해지지 않는 path에 일관적인 응답을 줄 수 있는 방법은?
9. HTTP 응답 header 내에 Location을 설정하면 해당 Path로 redirect 한다.
10. Header는 항상 \r\n으로 줄바꿈을 해야하고 마지막에는 끝났다는 의미로 \r\n 하나 더 붙여야 한다
11. TODO: http 요청 테스트는 어떻게 구현할 수 있을까?

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 