package utils;
import java.util.ArrayList;

import bursts.Burst;
import bursts.CPUBurst;
import bursts.IOBurst;


public class Driver {
	public static int globalTime = 0;
	
	public static void main(String[] args){
		//run1();
		//run2();
		run3();
		//run4();
	}
	
	public static void run1(){
		ArrayList<Burst> p1Bursts = new ArrayList<Burst>();
		p1Bursts.add(new CPUBurst(3));
		p1Bursts.add(new IOBurst(14));
		p1Bursts.add(new CPUBurst(3, 2, 2, 1));
		
		ArrayList<Burst> p2Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(5));
		p2Bursts.add(new IOBurst(7));
		p2Bursts.add(new CPUBurst(1, 2, 1, 1));
		
		ArrayList<Burst> p3Bursts = new ArrayList<Burst>();
		p3Bursts.add(new CPUBurst(6));
		
		Process[] processes = {
				new Process(1, 64, p1Bursts),
				new Process(2, 32, p2Bursts),
				new Process(3, 32, p3Bursts),
		};
		MemoryManager.pageSize = 16;
		MemoryManager.mainMemorySize=256;
		Scheduler schedule = new Scheduler( processes );
		MemoryManager.scheduler = schedule;
		schedule.start();
	}
	
	public static void run2(){
		ArrayList<Burst> p1Bursts = new ArrayList<Burst>();
		p1Bursts.add(new CPUBurst(5));
		p1Bursts.add(new IOBurst(5));
		p1Bursts.add(new CPUBurst(4));
		p1Bursts.add(new IOBurst(5));
		p1Bursts.add(new CPUBurst(6));
		
		ArrayList<Burst> p2Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(3));
		p2Bursts.add(new IOBurst(6));
		p2Bursts.add(new CPUBurst(3));
		p2Bursts.add(new IOBurst(6));
		p2Bursts.add(new CPUBurst(3));
		
		ArrayList<Burst> p3Bursts = new ArrayList<Burst>();
		p3Bursts.add(new CPUBurst(4));
		p3Bursts.add(new IOBurst(3));
		p3Bursts.add(new CPUBurst(5));
		
		ArrayList<Burst> p4Bursts = new ArrayList<Burst>();
		p4Bursts.add(new CPUBurst(2));
		p4Bursts.add(new IOBurst(6));
		p4Bursts.add(new CPUBurst(2));
		p4Bursts.add(new IOBurst(8));
		p4Bursts.add(new CPUBurst(4));
		
		Process[] processes = {
				new Process(1, 32, p1Bursts),
				new Process(2, 110, p2Bursts),
				new Process(3, 24, p3Bursts),
				new Process(4, 60, p4Bursts),
		};
		
		MemoryManager.pageSize = 16;
		MemoryManager.mainMemorySize = 512;
		Scheduler schedule = new Scheduler( processes );
		MemoryManager.scheduler = schedule;
		schedule.start();
	}
	
	public static void run3(){
		ArrayList<Burst> p1Bursts = new ArrayList<Burst>();
		p1Bursts.add(new CPUBurst(3));
		p1Bursts.add(new IOBurst(3));
		p1Bursts.add(new CPUBurst(1, 3, 1, 1));
		p1Bursts.add(new IOBurst(3));
		p1Bursts.add(new CPUBurst(3));
		
		ArrayList<Burst> p2Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(3));
		p2Bursts.add(new IOBurst(2));
		p2Bursts.add(new CPUBurst(1, 3, 1, 2));
		p2Bursts.add(new IOBurst(2));
		p2Bursts.add(new CPUBurst(3));
		
		ArrayList<Burst> p3Bursts = new ArrayList<Burst>();
		p3Bursts.add(new CPUBurst(1, 2, 1, 1));
		p3Bursts.add(new IOBurst(3));
		p3Bursts.add(new CPUBurst(1, 2, 1, 2));
		p3Bursts.add(new IOBurst(2));
		p3Bursts.add(new CPUBurst(5));
		
		ArrayList<Burst> p4Bursts = new ArrayList<Burst>();
		p4Bursts.add(new CPUBurst(1, 3, 1, 2));
		
		Process[] processes = {
				new Process(1, 64, p1Bursts),
				new Process(2, 100, p2Bursts),
				new Process(3, 90, p3Bursts),
				new Process(4, 30, p4Bursts),
		};
		
		MemoryManager.pageSize = 16;
		MemoryManager.mainMemorySize = 512;
		Scheduler schedule = new Scheduler( processes );
		MemoryManager.scheduler = schedule;
		schedule.start();
	}
	
	public static void run4(){
		ArrayList<Burst> p1Bursts = new ArrayList<Burst>();
		p1Bursts.add(new CPUBurst(6));
		p1Bursts.add(new IOBurst(15));
		p1Bursts.add(new CPUBurst(7));
		
		ArrayList<Burst> p2Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(3));
		p2Bursts.add(new IOBurst(5));
		p2Bursts.add(new CPUBurst(3));
		p2Bursts.add(new IOBurst(5));
		p2Bursts.add(new CPUBurst(3));
		
		ArrayList<Burst> p3Bursts = new ArrayList<Burst>();
		p2Bursts.add(new CPUBurst(8));
		p2Bursts.add(new IOBurst(6));
		p2Bursts.add(new CPUBurst(10));
		
		ArrayList<Burst> p4Bursts = new ArrayList<Burst>();
		p4Bursts.add(new CPUBurst(1));
		p4Bursts.add(new IOBurst(3));
		p4Bursts.add(new CPUBurst(2));
		p4Bursts.add(new IOBurst(3));
		p4Bursts.add(new CPUBurst(2));
		p4Bursts.add(new IOBurst(3));
		p4Bursts.add(new CPUBurst(2));
		
		ArrayList<Burst> p5Bursts = new ArrayList<Burst>();
		p5Bursts.add(new CPUBurst(15));
		p5Bursts.add(new IOBurst(6));
		p5Bursts.add(new CPUBurst(20));
		
		Process[] processes = {
				new Process(1, 60, p1Bursts),
				new Process(2, 300, p2Bursts),
				new Process(3, 224, p3Bursts),
				new Process(4, 80, p4Bursts),
				new Process(5, 120, p5Bursts),
		};
		
		MemoryManager.pageSize = 16;
		MemoryManager.mainMemorySize = 512;
		Scheduler schedule = new Scheduler( processes );
		MemoryManager.scheduler = schedule;
		schedule.start();
	}
}

