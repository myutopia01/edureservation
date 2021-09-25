![edu](https://user-images.githubusercontent.com/66100487/134440393-d317efa3-2c58-4591-b66d-b37fb70fba75.png)


# 교육 신청 사이트

# Table of contents

- [교육 신청](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출 과 Fallback 처리](#동기식-호출-과-Fallback-처리)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
  - [신규 개발 조직의 추가](#신규-개발-조직의-추가)

# 서비스 시나리오

기능적 요구사항
1. 고객이 교육을 신청한다
2. 고객이 결제한다
3. 교육 신청이 되면 신청 내역이 교육 관리자에게 전달된다
4. 교육 관리자가 확인하여 교육을 확정한다.
5. 고객이 교육을 취소할 수 있다
6. 교육이 취소되면 교육 수강이 취소된다
7. 고객이 교육 시작 여부를 중간중간 조회한다
8. 교육 신청상태가 바뀔 때 마다 카톡으로 알림을 보낸다

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 주문건은 아예 거래가 성립되지 않아야 한다  Sync 호출 
1. 장애격리
    1. 교육 관리 기능이 수행되지 않더라도 신청은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 고객이 자주 교육 관리에서 확인할 수 있는 배달상태를 신청 시스템(프론트엔드)에서 확인할 수 있어야 한다  CQRS
    1. 신청 상태가 바뀔때마다 카톡 등으로 알림을 줄 수 있어야 한다  Event driven


# 체크포인트

- 분석 설계


  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 개인별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?

- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/66100487/134432238-5e600e56-820f-49b5-9798-1540b31e6b66.png)


## TO-BE 조직 (Vertically-Aligned)
  ![그림1](https://user-images.githubusercontent.com/66100487/134458727-93a70857-c064-4389-965b-8274fa3131ee.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  http://www.msaez.io/#/storming/M6lvbk0YRhRY9U77UJGeBaQwXez2/140f28e7200384b3b96fef84b3ddacd7
 ![image](https://user-images.githubusercontent.com/66100487/134434695-88eab785-c851-4481-a9e4-a73ac9c868e9.png)



### 이벤트 도출
![image](https://user-images.githubusercontent.com/66100487/134459708-dd6a848c-2224-418e-9a80-f9e3822d9db8.png)

### 부적격 이벤트 탈락
![image](https://user-images.githubusercontent.com/66100487/134459834-23bf1116-598e-4df1-811a-02aee4312b99.png)

    - 과정중 도출된 잘못된 도메인 이벤트들을 걸러내는 작업을 수행함
        - 신청시>교육이 신청됨, 결제 버튼이 클릭됨, 예약시>예약내역이 전달됨 :  UI 의 이벤트이지, 업무적인 의미의 이벤트가 아니라서 제외

### 액터, 커맨드 부착하여 읽기 좋게
![image](https://user-images.githubusercontent.com/66100487/134758245-9a0ecd9f-fef3-4e6c-a2ab-2847bbe40407.png)


### 어그리게잇으로 묶기
![image](https://user-images.githubusercontent.com/66100487/134758247-60e74c01-5b86-4811-9438-4b57f6d1c221.png)

    - 예약 처리, 주문처리, 결제, 예약관리 등 트랜잭션이 유지되어야 하는 단위로 묶어줌

### 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/66100487/134758289-8d40803e-589a-4776-9550-b9d8d565a257.png)

    - 도메인 서열 분리 
        - 예약 -> 결제 -> 예약관리 순으로 정의

### 폴리시 부착 (괄호는 수행주체, 폴리시 부착을 둘째단계에서 해놔도 상관 없음. 전체 연계가 초기에 드러남)
![image](https://user-images.githubusercontent.com/66100487/134758474-ec6ed571-b21f-4350-ad34-76ca0215b3c3.png)


### 폴리시의 이동과 컨텍스트 매핑 (점선은 Pub/Sub, 실선은 Req/Resp)
![image](https://user-images.githubusercontent.com/66100487/134758330-d26e00f8-c667-4ad2-ad02-3878c2de196a.png)


### 완성된 1차 모형
![image](https://user-images.githubusercontent.com/66100487/134758335-34b619ed-8f07-413d-a6bb-9e54515ef746.png)

    - View Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증
![image](https://user-images.githubusercontent.com/66100487/134758346-7666f1d8-6637-4e4b-a504-f727db6b14fe.png)

    - 고객이 교육을 선택하여 신청한다 (ok)
    - 고객이 결제한다 (ok)
    - 예약이 요청되면 예약 내역이 관리자에게 전달된다 (ok)
    - 관리자가 확인하여 교육을 확정한다 (ok)

![image](https://user-images.githubusercontent.com/66100487/134758371-e600e288-4468-4a75-bcbe-9471a30c8751.png)
    - 고객이 신청을 취소할 수 있다 (ok)
    - 교육 신청이 취소되면 교육이 취소된다 (ok)
    - 고객이 예약상태를 중간중간 조회한다 (ok) 
    - 예약상태가 바뀔 때 마다 카톡으로 알림을 보낸다 (?)


### 모델 수정
![image](https://user-images.githubusercontent.com/66100487/134758395-107170fe-e08c-49d0-a0dd-10d66b504989.png)
    
    - 수정된 모델은 모든 요구사항을 커버함.

### 비기능 요구사항에 대한 검증
![image](https://user-images.githubusercontent.com/66100487/134758414-86ad4063-163f-4c62-9986-7af6b4313afc.png)

    - 결제가 완료되지 않은 예약 건은 유효하지 않다. Request-Response 방식 처리
    - 예약 관리 기능이 정상적으로 수행되지 않더라도 교육 신청은 가능해야 한다. Eventual Consistency 방식으로 트랜잭션 처리
    - 예약 상태를 카톡으로 확인할 수 있어야 한다(ok): 즉시성이 필요하지 않으므로 Eventual Consistency로 처리


## 헥사고날 아키텍처 다이어그램 도출
![image](https://user-images.githubusercontent.com/66100487/134758516-b882b2a7-36d5-45a3-93de-f018ab9186db.png)


# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 파이선으로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd Order
mvn spring-boot:run

cd Pay
mvn spring-boot:run

cd Reservation
mvn spring-boot:run

cd Notification
mvn spring-boot:run
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다
- order 마이크로 서비스

```
package roomreservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long customerId;
    private Long roomNo;
    private Long cardNo;
    private Integer guest;
    private String status;

    @PostPersist
    public void onPostPersist() {
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("Ordered");
        ordered.publishAfterCommit();
        roomreservation.external.Pay pay = new roomreservation.external.Pay();
        pay.setCardNo(this.cardNo);
        pay.setCustomerId(this.customerId);
        pay.setOrderId(this.orderId);


```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package roomreservation;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="orders", path="orders")
public interface OrderRepository extends PagingAndSortingRepository<Order, Long>{
    Order findByOrderId(Long orderId);

}
```

적용 후 API TEST
![image](https://user-images.githubusercontent.com/66100487/134758714-7e61874d-9662-42ec-ad6e-65b0fef9f2ab.png)

Order 서비스의 주문처리
![image](https://user-images.githubusercontent.com/66100487/134758665-eb98193c-d847-4a34-9950-3019a470ccc5.png)

주문 상태 확인
![image](https://user-images.githubusercontent.com/66100487/134758685-c4f2f50c-68a6-46b5-90bf-09ee730c12e0.png)

View 확인
![image](https://user-images.githubusercontent.com/66100487/134758721-510a3f5a-1b60-48fd-94b0-1257420f6111.png)


## 폴리글랏 퍼시스턴스

- Order 서비스의 로컬 DB를 기존 h2에서 hsqldb로 변경후에도 잘 동작함을 확인

```
<!—기존 DB
<dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
	<scope>runtime</scope>
</dependency>
-->
<dependency>
	<groupId>org.hsqldb</groupId>
	<artifactId>hsqldb</artifactId>
	<version>2.4.0</version>
	<scope>runtime</scope>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 주문(order)->결제(pay) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 
```
package roomreservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Date;

@FeignClient(name="Pay", url="${api.Pay.url}", fallback = PayServiceImpl.class)
public interface PayService {
    @RequestMapping(method= RequestMethod.POST, path="/pays")
    public void payment(@RequestBody Pay pay);
}
```

- 주문을 받은 직후(@PostPersist) 결제를 요청하도록 처리
```
@PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("Ordered");
        ordered.publishAfterCommit();
    
        roomreservation.external.Pay pay = new roomreservation.external.Pay();
        pay.setCardNo(this.cardNo);
        pay.setCustomerId(this.customerId);
        pay.setOrderId(this.orderId);
        pay.setStatus("Pay Request");
        pay.setRoomNo(this.roomNo);

        // mappings goes here
        OrderApplication.applicationContext.getBean(roomreservation.external.PayService.class)
            .payment(pay);
    }
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인:


```
# 결제(pay) 서비스를 잠시 내려 놓음 (kubectl delete svc, deploy pay)

# Fallback 처리 전 주문 처리: 결제 시스템 장애 시 주문이 되지 않음을 확인
http http://20.200.206.197:8080/orders customerId=1 roomNo=101 cardNo=1234 guest=1 status=ordered orderId=1111	#Fail
http http://20.200.206.197:8080/orders customerId=1 roomNo=101 cardNo=1234 guest=1 status=ordered orderId=1112	#Fail

# Fallback 처리 후 주문 처리: fallback으로 주문은 정상적으로 접수되고, 결제 지연에 대한 오류처리 확인
http http://20.200.206.197:8080/orders customerId=1 roomNo=101 cardNo=1234 guest=1 status=ordered orderId=1113#OrderSuccess

# Fallback처리 Message확인
![image](https://user-images.githubusercontent.com/66100487/134758894-938f4ddf-2021-4399-8e8d-ab0e0272d064.png)

# View 확인에서 주문처리는 되었으나 결제되지 않음을 확인
![image](https://user-images.githubusercontent.com/66100487/134758897-16bd73d2-84a3-4351-966d-ba1e8e1a1428.png)
```

- 또한 과도한 요청시에 서비스 장애가 도미노 처럼 벌어질 수 있다.




## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트


결제가 이루어진 후에 예약관리 시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 예약관리 시스템의 처리를 위하여 결제주문이 블로킹 되지 않아도록 처리한다.
 
- 이를 위하여 결제이력에 기록을 남긴 후에 곧바로 결제승인이 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package roomreservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Pay_table")
public class Pay {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long customerId;
    private Long cardNo;
    private String status;
    private Long roomNo;
    @PrePersist
    public void onPrePersist(){
        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);
paid.setStatus("Paid");
        paid.publish();
    }

```
- 예약관리 서비스에서는 결제승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
@Service
public class PolicyHandler{
    @Autowired ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Reservationinfo(@Payload Paid paid){

        if(!paid.validate()) return;

        System.out.println("\n\n##### listener Reservationinfo : " + paid.toJson() + "\n\n");
        Reservation reservation = new Reservation();
        reservation.setStatus("Reservation Confirmed");
        reservation.setOrderId(paid.getOrderId());
        reservation.setRoomNo(paid.getRoomNo());
        reservationRepository.save(reservation);

    }
```

예약관리 시스템은 주문/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 예약관리 시스템이 유지보수로 인해 잠시 내려간 상태라도 신청을 받는데 문제가 없다:
```
# 예약관리시스템(Reservation)을 잠시 내려둠 (kubectl delete deploy,svc reservation)

# 주문처리 : 주문됨을 확인
http http://20.200.206.197:8080/orders customerId=1 roomNo=101 cardNo=1234 guest=1 status=ordered orderId=1114

# 주문 상태 확인 
http http://20.200.206.197:8080/infomations

# 주문과 결제는 되었으나 예약되지 않음을 확인
```
![image](https://user-images.githubusercontent.com/66100487/134759028-58dc0d2a-199f-4593-9b85-d495f33e83ea.png)

```
# 예약 관리서비스 기동 후 주문 상태 확인 (kafka에서 주문/결제 이벤트 수신 후 예약 처리 됨)
```
![image](https://user-images.githubusercontent.com/66100487/134759038-8487aba3-44f6-4902-bd6a-b3afd7ea7ef9.png)

```
# 주문 상태 확인 
http http://20.200.206.197:8080/infomations		#예약됨을 확인
```
![image](https://user-images.githubusercontent.com/66100487/134759044-265801c8-d674-4457-8c77-ecf18be4eb76.png)



# 운영

## CI/CD 설정


각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 Azure를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 kubernetes 폴더의 deployment.yml 에 포함되었다.

azure Devops의 pipeline에 각각의 서비스에 대한 CI/CD 생성 후, Github에서 코드가 업데이트 될때마다 자동으로 빌드/배포된다.
![image](https://user-images.githubusercontent.com/66100487/134759162-468214ff-8d56-4842-801b-b34d590c256b.png)


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 단말앱(app)-->결제(pay) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
```
![image](https://user-images.githubusercontent.com/66100487/134759933-fa31cd25-023d-4f6f-af9b-44ec6c8b020a.png)


- 피호출 서비스(결제:pay) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# (pay) 결제이력.java (Entity)
```
![image](https://user-images.githubusercontent.com/66100487/134759959-ee354fd8-07c1-4691-b6f5-bce56b34754b.png)


* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 70명
- 60초 동안 실시

```
$ siege -c70 -t60S -v --content-type "application/json" 'http://10.0.247.8:8080/orders POST {"orderId": 2, "roomNo": 102}'
** SIEGE 4.0.4
** Preparing 70 concurrent users for battle.
The server is now under siege...
HTTP/1.1 201     1.84 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201     2.32 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201     2.90 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201     3.36 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201     3.87 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201     4.33 secs:     268 bytes ==> POST http://10.0.247.8:8080/orders
--------------------------------------------------------------------------------
* 요청이 과도하여 CB를 동작함 요청을 차단
HTTP/1.1 500    30.60 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500    30.61 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500    30.63 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500    30.63 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500    30.63 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500    30.61 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
* 요청을 어느정도 돌려보내고 나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작
HTTP/1.1 201    31.13 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    31.62 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    32.20 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
--------------------------------------------------------------------------------
* 다시 요청이 쌓여 건당 처리 시간이 610 밀리를 살짝 넘김 → 회로 열기 → 요청 실패 처리 
HTTP/1.1 500    30.04 secs:     271 bytes ==> POST http://10.0.247.8:8080/orders
* 상태 호전됨 - (건당 (쓰레드당) 처리 시간이 610 밀리 미만으로 회복) → 요청 수락
HTTP/1.1 201    33.89 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    33.80 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
--------------------------------------------------------------------------------
HTTP/1.1 201    31.69 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    31.60 secs:     270 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.04 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    31.01 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.91 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.02 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.85 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.74 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.02 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.76 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.78 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.03 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.76 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.71 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.03 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.83 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.88 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 500     1.02 secs:     193 bytes ==> POST http://10.0.247.8:8080/orders
HTTP/1.1 201    30.85 secs:     272 bytes ==> POST http://10.0.247.8:8080/orders
* 이러한 패턴이 계속 반복되면서 시스템은 도미노 현상이나 자원 소모의 폭주 없이 잘 운영됨
Lifting the server siege...
Transactions:		         110 hits
Availability:		       86.61 %
Elapsed time:		       59.78 secs
Data transferred:	        0.03 MB
Response time:		       27.41 secs
Transaction rate:	        1.84 trans/sec
Throughput:		        0.00 MB/sec
Concurrency:		       50.43
Successful transactions:         110
Failed transactions:	          17
Longest transaction:	       35.62
Shortest transaction:	        1.02
```
- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 63.55% 가 성공하였고, 46.45%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 

- 예약서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy pay --min=1 --max=10 --cpu-percent=15
```
- CB 에서 했던 방식대로 워크로드를 1분 동안 걸어준다.
```
siege -c70 -t60S -v --content-type "application/json" 'http://10.0.247.8:8080/orders POST {"orderId": 2, "roomNo": 102}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
watch kubectl get pod
```
![image](https://user-images.githubusercontent.com/66100487/134759307-6384988b-c09d-4cac-9c9e-b59361f8cae4.png)
```
kubectl get deploy pay -w
```
![image](https://user-images.githubusercontent.com/66100487/134759331-9b4e8334-82a3-469e-b0dd-a85918b47f1b.png)

- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
![image](https://user-images.githubusercontent.com/66100487/134759380-4574d609-665d-4200-a2ef-35bf4f164584.png)



## 무정지 재배포(Readyness)

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c1 -t60S -v --content-type "application/json" 'http://10.0.247.8:8080/orders POST {"orderId": 2, "roomNo": 102}'
```


- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
배포중 서비스 중단이 발생하여 Availability가 99% 임을 확인.
![image](https://user-images.githubusercontent.com/66100487/134759591-a84814c0-961b-4369-8e5a-391cbcd413c5.png)
![image](https://user-images.githubusercontent.com/66100487/134759594-bef2688f-3c2d-4535-8ff4-55f9c031a2e5.png)

- deployment.yml에 readinessProbe, livenessProbe 설정
![image](https://user-images.githubusercontent.com/66100487/134759599-e12e2f99-92f7-48d4-97ba-479a9f29159f.png)
![image](https://user-images.githubusercontent.com/66100487/134759572-1c3c30b7-2518-48bd-ba8b-71d782cf16f7.png)


배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.


## Liveness
Pod의 상태가 비정상인 경우 재시작하는지 확인하기 위해 liveness 포트를 사용하지 않는 포트(8088)로 설정 후 배포
![image](https://user-images.githubusercontent.com/66100487/134759647-27dffbc0-f9a9-4070-9b52-e51ce2f55f50.png)

해당포트로는 서비스 확인이 불가능하므로 RESTART 횟수가 늘어나고 있음을 확인
![image](https://user-images.githubusercontent.com/66100487/134759658-92d9d522-72e5-4017-8140-93b42098d6f1.png)


## ConfigMap 적용

·deployment.yml에 파일 설정
![image](https://user-images.githubusercontent.com/66100487/134759861-ae4037dd-3749-4179-bccd-132b944ebd8c.png)

ConfigMap 생성, 정보 확인
kubectl create configmap applocation --from-literal=applocationvalue=ACR
Kubectl get configmap applocation -o yaml
![image](https://user-images.githubusercontent.com/66100487/134759868-984ef90e-53a6-4c79-ae43-cf6949257aac.png)


Order.java에서 Configmap에서 설정한 value를 읽어오도록 구현
![image](https://user-images.githubusercontent.com/66100487/134759876-3a36a70c-53cf-4d63-b975-5f79e90cfb91.png)

![image](https://user-images.githubusercontent.com/66100487/134759879-a8681f0a-fb0b-4885-8837-15d002db683c.png)

## PersistentVolume & PersistentVolumeClaim
deployment.yml 내 Pod/ PersistentVolumeClaim 추가
![image](https://user-images.githubusercontent.com/66100487/134759889-934e746c-69fd-41ea-bfd9-a40833bae9b8.png)

PersistentVolume 추가하지 않을 경우 상단과 같이 Pending 상태 유지
![image](https://user-images.githubusercontent.com/66100487/134759897-96a75433-a168-4621-ad58-f3e801fb2567.png)

PersistentVolume 추가
![image](https://user-images.githubusercontent.com/66100487/134759905-0d8e2624-b62e-4c70-a770-665d29f68098.png)

Pod/ PersistentVolumeClaim 활성화
![image](https://user-images.githubusercontent.com/66100487/134759912-077e1eab-7e6f-405b-ad1e-a94b2f2702ff.png)

![image](https://user-images.githubusercontent.com/66100487/134759915-ad98ec0b-1373-41db-b707-b57b24485be5.png)

