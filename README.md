# 편의점 결제 시스템 프로그램

편의점에서의 결제 과정을 콘솔 환경에서 체험할 수 있는 Java 애플리케이션입니다.

## 목차
- [프로젝트 소개](#프로젝트-소개)
- [시스템 요구사항](#시스템-요구사항)
- [시작하기](#시작하기)
- [기술 스택](#기술-스택)
- [기능 목록](#기능-목록)

## 프로젝트 소개
이 프로젝트는 구매자의 할인 혜택과 재고 상황을 고려하여 최종 결제 금액을 계산하고 안내하는 편의점 결제 시스템입니다.

## 시스템 요구사항
- JDK 21 이상
- Gradle 8.x 이상

## 시작하기

#### 프로젝트 클론
```bash
git clone https://github.com/hyungjunn/java-convenience-store-7-hyungjunn.git
cd java-convenience-store-7-hyungjunn
```

#### 빌드
```bash
./gradlew build
```

#### 실행
```bash
./gradlew run
```
또는 IDE에서 `Application` 클래스의 `main()` 메서드를 직접 실행할 수 있습니다.

## 기술 스택
- Java 21
- Gradle
- [mission-utils](https://github.com/woowacourse-projects/mission-utils) (1.2.0)

#### 코드 컨벤션
- Google Java Style Guide를 따릅니다.
- 메서드와 클래스에 적절한 주석을 추가해주세요.
- 테스트 코드 작성을 권장합니다.

## 기능 목록(how X, what O)
- [x] 상품 수량이 결제된 수량만큼 차감한다.
- [x] 구매 수량이 재고 수량을 초과한 경우: [ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.
- [x] 프로모션 할인을 적용한다.
- [x] 프로모션 기간 중 프로모션 재고를 우선 차감하고, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.
- [x] 프로모션 적용이 가능한 상품인데 해당 수량보다 적게 구매했는지 판단한다. 
  - [x] 프로모션 적용이 가능한 상품인지 판별한다. - 구매 수량을 통해서(부가정보 -> 인자) 
- [x] 해당 수량보다 적게 구매했는지 판별한다.
- [ ] (혜택 알림에 Y를 했을 때) 수량 +1 
  - [ ] 증정 받을 수 있는 상품을 추가한다.
- [x] 프로모션 혜택없이 결제해야 하는 수량을 계산한다.
- [x] 주어진 수량을 정가로 결제한다. 
  - 여기서는 프로모션 혜택없이 결제하는 수를 말함.
- [x] 정가로 결제해야하는 수량을 제외한 후 결제한다. 
- [x] 프로모션 미적용 금액의 30% 멤버십 할인한다.
- [x] 프로모션 적용 후 남은 금액에 멤버십 할인 받는다.
- [x] 멤버십 할인은 8000원까지다.
- [ ] 총 구매액을 구한다.
- [ ] 행사할인 금액(프로모션에 의해 할인된 금액)을 구한다.
- [ ] 멤버십할인 금액을 구한다.
- [ ] 최종 결제 금액을 구한다.
- [ ] 다음 고객이 구매할 때 정확한 재고 정보를 제공한다.

## 입출력 기능 목록
- [x] 보유하고 있는 상품을 출력한다.
  - 재고가 0인 경우 `재고 없음` 출력한다.
- [ ] 구매할 상품명과 수량을 입력받는다. (예: [사이다-2],[감자칩-1])
- [ ] 구매할 상품과 수량 형식이 올바르지 않은 경우: [ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요. 
- [ ] 존재하지 않는 상품을 입력한 경우: [ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.
- [ ] 구매 수량이 재고 수량을 초과한 경우: [ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.
- [ ] 멤버십 할인 적용 여부를 입력 받는다.
- [ ] 잘못된 입력의 경우: [ERROR] 잘못된 입력입니다. 다시 입력해 주세요.
- [ ] 구매 상품 내역, 증정 상품 내역, 금액 정보를 출력한다.
- [ ] 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부에 대한 안내 메시지를 출력한다.
- [ ] 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 혜택에 대한 안내 메시지를 출력한다.
- [ ] 추가 구매 여부를 확인하기 위해 안내 문구를 출력한다.

```
안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-3],[에너지바-5]

멤버십 할인을 받으시겠습니까? (Y/N)
Y 

===========W 편의점=============
상품명		    수량	    금액
콜라		        3 	    3,000
에너지바 		    5 	    10,000
===========증	정=============
콜라		        1
==============================
총구매액		    8	    13,000
행사할인			        -1,000
멤버십할인			        -3,000
내실돈			         9,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 7개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[콜라-10]

현재 콜라 4개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
N

===========W 편의점=============
상품명		    수량	    금액
콜라		        10 	    10,000
===========증	정=============
콜라		        2
==============================
총구매액		    10	    10,000
행사할인			        -2,000
멤버십할인			        -0
내실돈			         8,000

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
Y

안녕하세요. W편의점입니다.
현재 보유하고 있는 상품입니다.

- 콜라 1,000원 재고 없음 탄산2+1
- 콜라 1,000원 7개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 재고 없음
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
[오렌지주스-1]

현재 오렌지주스은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
Y

멤버십 할인을 받으시겠습니까? (Y/N)
Y

===========W 편의점=============
상품명		    수량	     금액
오렌지주스		    2 	     3,600
===========증	정=============
오렌지주스		    1
==============================
총구매액		    2	     3,600
행사할인			        -1,800
멤버십할인			        -0
내실돈			         1,800

감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
N
```

