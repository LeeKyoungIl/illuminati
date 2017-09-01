# Project illuminati

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/illuminati-logo.png)

# Application 에서 일어나는 모든 EVENT 데이터를 수집하고 Kibana또는 다른툴을(어떤툴이든) 이용해서 보여주는 플랫폼 입니다.

## 필수사항
* Java6 이상
* RabbitMQ 또는 Kafka
* AspectJ를 사용할 수 있는 Java Web Application 

## 권장사항
* ElasticSearch
* Kibana

# illuminati에서 수집을 하는 Event 데이터 정보
1. 적용 서버의 정보와(IP, HOST_NAME..등등), JVM MEMORY 사용정보
2. 클라이언트 요청에 관한 모든 정보
    * 모든 HEADER, COOKIE
    * OS, BROWSER, DEVICE 정보
    * Application상의 실행 메서드 및 파라메터
    * Application상의 메서드 실행 시간
    * Application상의 메서드 요청의 파라메터값 (GET, POST)
    * Application상의 메서드 요청의 결과값
    
# illuminati는 쉽게 사용할 수 있습니다.
1. MAVEN, GRADLE Dependency 추가
2. 수집을 원하는 곳에 @Illuminati Annotation을 추가
