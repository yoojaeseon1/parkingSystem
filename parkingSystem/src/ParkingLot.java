import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingLot {

	private Map<Integer, Integer> areaNumToIsOccupied; // key : 공통 주차 번호, value : 주차 여부( value >= 0 : 주차 중, value < 0 : 빈 공간)
	private final int[][] areaNumsEachEntrances; // 입구 별 주차 번호
	private AtomicInteger numOccupied; // 현재 주차 중인 차량 수
	private final int numMaxOccupied; // 주차장의 최대 주차량
	private Queue<int[]> carWaitingQueue; // 주차장이 꽉차서 주차 대기중인 차량들
	
	
	// test code -------
	private Set<Integer> areaSet;
	
	private AtomicInteger numGoInto;
	private AtomicInteger numGoOut;
	// -------

	public ParkingLot(int[][] areaNumsEachEntrances) {

		this.areaNumsEachEntrances = areaNumsEachEntrances;
		numOccupied = new AtomicInteger(0);
		this.numMaxOccupied = areaNumsEachEntrances[0].length;
		this.areaNumToIsOccupied = new ConcurrentHashMap<>();
		this.carWaitingQueue = new LinkedBlockingQueue<>();
		
		for (int numArea = 0; numArea < this.numMaxOccupied; numArea++)
			areaNumToIsOccupied.put(numArea, -1);
		
		
		// test code -------
		
		this.areaSet = new HashSet<>();
		this.numGoInto = new AtomicInteger(0);
		this.numGoOut = new AtomicInteger(0);
		// -------
	}

	public synchronized void goIntoParkingLot(int entranceNumber, int carNumber) {

		if (this.numOccupied.get() == this.numMaxOccupied) {
			int[] carInfo = { entranceNumber, carNumber };
			this.carWaitingQueue.add(carInfo);
			return;
		}

		int[] areaNums = this.areaNumsEachEntrances[entranceNumber];
		//ex) { 4, 3, 5, 1, 0, 2 }
		// areaNums[0] = 현재 입구의 주차번호 0번에 해당하는 공통 주차번호

		for (int areaNumsI = 0; areaNumsI < areaNums.length; areaNumsI++) {

			int areaNum = areaNums[areaNumsI];

			if (this.areaNumToIsOccupied.get(areaNum) == -1) {
				this.areaNumToIsOccupied.put(areaNum, carNumber);
				this.numOccupied.getAndIncrement();
				
				// test code -------
				this.areaSet.add(areaNum);
				this.numGoInto.getAndIncrement();
				// -------
				break;
			}
		}
	}
	

	public synchronized void goOutParkingLot(int areaNum) {

		if (this.carWaitingQueue.size() > 0) {
			int[] carInfo = this.carWaitingQueue.poll();
			this.areaNumToIsOccupied.put(areaNum, carInfo[1]);

			return;
		}
		
		this.areaNumToIsOccupied.put(areaNum, -1);
		this.numOccupied.getAndDecrement();

	}
	
	
	// test (출차-무작위 주차번호)
	
	public synchronized void goOutParkingLot() {
		
		Iterator<Integer> areaIter = areaSet.iterator();
		int areaNum = areaIter.next();
		
		if (this.carWaitingQueue.size() > 0) {
			int[] carInfo = this.carWaitingQueue.poll();
			this.areaNumToIsOccupied.put(areaNum, carInfo[1]);
			
			// test code -------
			this.areaSet.add(areaNum);
			this.numGoInto.getAndIncrement();
			// -------
			
			return;
		}
		
		this.areaNumToIsOccupied.put(areaNum, -1);
		this.numOccupied.getAndDecrement();
		
		// test code -------
		this.numGoOut.getAndIncrement();
		// -------
	}
	
	
	// getter / setter

	public Map<Integer, Integer> getAreaNumsToIsOccupied() {
		return areaNumToIsOccupied;
	}

	public void setAreaNumsToIsOccupied(Map<Integer, Integer> areaNumToIsOccupied) {
		this.areaNumToIsOccupied = areaNumToIsOccupied;
	}

	public AtomicInteger getNumOccupied() {
		return numOccupied;
	}

	public void setNumOccupied(AtomicInteger numOccupied) {
		this.numOccupied = numOccupied;
	}

	public Queue<int[]> getCarWaitingQueue() {
		return carWaitingQueue;
	}

	public void setCarWaitingQueue(Queue<int[]> carWaitingQueue) {
		this.carWaitingQueue = carWaitingQueue;
	}

	public int[][] getAreaNumsEachEntrances() {
		return areaNumsEachEntrances;
	}

	public int getNumMaxOccupied() {
		return numMaxOccupied;
	}
	
	
	

	public static void main(String[] args) throws Exception {

		// int[][] areaNumsEachEntrances = { { 4, 3, 5, 1, 0, 2 }, { 3, 0, 4, 1,
		// 5, 2 } };

		// test code
		
		int[][] areaNumsEachEntances = { { 13, 14, 12, 15, 9, 10, 8, 11, 5, 6, 4, 7, 1, 2, 0, 3 },
				{ 4, 8, 0, 12, 5, 9, 1, 13, 6, 10, 2, 14, 7, 11, 3, 15 },
				{ 7, 11, 3, 15, 6, 10, 2, 14, 5, 9, 1, 13, 4, 8, 0, 12 } };
		
		ParkingLot parkingLot = new ParkingLot(areaNumsEachEntances);
		
		int carNum = 1000;
		
		
		for(int areaNum = 0; areaNum < 10; areaNum++) {
			parkingLot.goIntoParkingLot(1, carNum++);	
		}
		
		
		Runnable[] tasks = new Runnable[20];
		int tasksI = 0;
		
		for(int numMovedCar = 0; numMovedCar < 20; numMovedCar++) {
			
			int intoOrOut = (int)Math.round(Math.random());
			int carNumber = carNum++;
			
			if(parkingLot.getNumOccupied().get() == 0 || intoOrOut == 0) { // 주차 - 주차된 차량이 없거나 선택된 명령어가 주차일 경우
				tasks[tasksI++] = () ->{
					
					int entranceNum = (int)(Math.random() * 3); // 무작위 입구번호
					
					parkingLot.goIntoParkingLot(entranceNum, carNumber);					
				};
				
				
			} else { // 출차
				
				tasks[tasksI++] = () ->{
					parkingLot.goOutParkingLot();
				};
			}
		}
		
		
		ExecutorService executor = Executors.newCachedThreadPool();
		
		for(tasksI = 0; tasksI < tasks.length; tasksI++) {
			executor.execute(tasks[tasksI]);
		}
		
		
		Thread.sleep(3000);
		
		System.out.println("numOccupied : " + parkingLot.getNumOccupied()); // 현재 주차된 차량 수
		System.out.println("numGoInto : " + parkingLot.numGoInto); // 주차량
		System.out.println("numGoOut : " + parkingLot.numGoOut); // 출차량
		
	}
}