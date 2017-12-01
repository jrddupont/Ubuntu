package utils;

import java.util.ArrayList;
import java.util.Iterator;

import utils.Process.SharedResourceException;

public class MemoryManager {
	public static Scheduler scheduler = null;
	public static int pageSize;
	public static int mainMemorySize;
	private static int pCount=0;
	private static class Pages {
		public int pid;
		public int pages;
		Pages(int id, int p)
		{
			pid=id;
			pages=p;
		}
	}
	private static ArrayList<Pages> PageTable = new ArrayList<Pages>();
	private static ArrayList<Pages> virtualPageTable = new ArrayList<Pages>();
	
	public static void run(Process p) throws SharedResourceException
	{
		boolean inMemory = false;
		ArrayList<Pages> tempVirtualPageTable = new ArrayList<Pages>();
		for(Iterator<Pages> virtualPage = virtualPageTable.iterator(); virtualPage.hasNext();) //swap pages back into memory if they are on disk
		{
			Pages block = virtualPage.next();
			if(block.pid==p.id)
			{
				inMemory=true;
				if(!(freePages()>=block.pages))
				{
					int count=block.pages-freePages(); //pages needed to swap
					do
					{
						Process minAge=null;
						for(Process process : Scheduler.processes) //find process of minimum age
						{
							if(minAge==null || (process.age<minAge.age && minAge.id!=block.pid))
							{
								for(Pages pt : PageTable) if(pt.pid==process.id && pt.pid!=p.id) minAge=process;
							}
						}
						Pages oldPages = null;
						for(Pages page : PageTable) if(page.pid==minAge.id) oldPages=page; //find first page owned by minAge
						if(oldPages.pages>count) //Don't need to swap everything
						{
							System.out.print("Swapped "+count+" pages from process "+oldPages.pid+" out to disk, ");
							tempVirtualPageTable.add(new Pages(oldPages.pid, count));
							oldPages.pages-=count;
							count=0;
						}else{ //need to swap whole set of pages
							System.out.print("Swapped "+oldPages.pages+" pages from process "+oldPages.pid+" out to disk, ");
							tempVirtualPageTable.add(oldPages);
							PageTable.remove(oldPages);
							count-=oldPages.pages;
						}//swap out processes of biggest age until there's enough space
					}while(count>0);
				}
				//swap process in
				virtualPage.remove();
				PageTable.add(block);
			}
		}
		for(Pages page : tempVirtualPageTable) virtualPageTable.add(page);
		for(Pages page : PageTable) if(page.pid==p.id) inMemory=true; //check to see if the process exists
		if(!inMemory) //process doesn't exist in memory yet
		{
			pCount++;
			virtualPageTable.add(new Pages(p.id, (p.memory/pageSize)+1)); //give process memory for first time
			System.out.print("P"+p.id+" loaded into memory, ");
			run(p);
		}else{
			p.step(); //increment process age
		}
	}
	
	public static int freePages(){
		int temp=0;
		for(Pages i : PageTable){
			temp += i.pages;
		}
		return ((mainMemorySize-(pageSize*temp))/pageSize);
	}
	public static void deallocate( Process process ){
		for(int i=0;i<PageTable.size();i++)
		{
			if(PageTable.get(i).pid==process.id)
			{
				PageTable.remove(i);
			}
		}
		pCount--;
	}

	public static void printDebug() {
		System.out.println("  Memory:");
		System.out.println("    Loaded: " + pCount+" Processes");
		System.out.println("    Used space: " + ((mainMemorySize/pageSize)-freePages())+" pages");
		System.out.println("    Available space: " +freePages()+ " pages");
	}
}