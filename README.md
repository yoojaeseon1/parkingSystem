# parkingSystem

### 소스파일

parkingSystem/src/parkingSystem.java

---

#### 메소드 정보

- public synchronized void goIntoParkingLot(int entranceNumber, int carNumber) : 주차(차량 진입)

- public synchronized void goOutParkingLot(int areaNum) : 출차(차량 진출)

- public synchronized void goOutParkingLot() : 출차(차량진출), 테스트용(출차할 공간의 번호(areaNum)가 주차 중인 차량 중 무작위로 선택됨)

---

#### 테스트 방식

주차장 크기 : 16(0~15번), 입구 3개

초기 10대 주차(주차된 차량 set에 저장)

그 이후부터는 주차 / 출차 무작위로 결정

주차 : 주차 가능한 공간이 있는 경우만
        (주차된 차량 set에 저장)

출차 : 주차되어 있는 차량이 있는 경우만
 (무작위로 출차됨. set의 iterator에서 첫 번째만 탐색)

원하는 결과<br><br>
현재 주차량 = 주차된 차량 – 출차한 차량
