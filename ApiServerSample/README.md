# Project illuminati : this project is sample of applied of illuminati.

## kind of spring boot project.
 * check configuration file in resource folder.
 
## It is a performance comparison chart according to illuminati applied application.
*  VM 4 core 4G 
1. without illuminati
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/without_illuminati.png)
2. with logback 
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/with_log.png)
3. with illuminati
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/with_illuminati.png)

## Performance degradation when used on physical machine is less than VMs.

===============================================================================

# Project illuminati : illuminati 를 적용한 Sample프로젝트 입니다.

## Spring Boot를 사용한 프로젝트 입니다.
 * resource안의 yml 설정파일을 확인하세요. 
 
## illuminati적용에 따른 성능 비교표 입니다. - VM 4코어 4기가 환경
1. illuminati 미적용시
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/without_illuminati.png)
2. logback log 적용시 - 그래프를 보면 GC가 신경쓰입니다.
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/with_log.png)
3. illuminati 적용시 - 1번에 비해 TPS는 하락했지만 2번에 비해 그래프가 안정적 입니다.
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/with_illuminati.png)

## 물리 서버에서 사용시 성능 하락은 VM에 비해 적습니다.