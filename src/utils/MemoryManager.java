package utils;

import java.util.ArrayList;
import java.util.Iterator;

import utils.Process.SharedResourceException;

public class MemoryManager {
	public static Scheduler scheduler = null;
	public static int pageSize;
	public static int mainMemorySize;
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
		for(Iterator<Pages> virtualPage = virtualPageTable.iterator(); virtualPage.hasNext();) //swap pages back into memory if they are on disk
		{
			Pages block = virtualPage.next();
			if(block.pid==p.id)
			{
				inMemory=true;
				if(!(freePages()>=block.pages))
				{
					int count=freePages()-block.pages; //pages needed to swap
					do
					{
						Process minAge=null;
						for(Process process : Scheduler.processes) //find process of minimum age
						{
							if(minAge==null || (process.age<minAge.age && minAge.id!=block.pid))
							{
								for(Pages pt : PageTable) if(pt.pid==process.id) minAge=p;
							}
						}
						Pages oldPages = null;
						for(Pages page : PageTable) if(page.pid==minAge.id) oldPages=page; //find first page owned by minAge
						if(oldPages.pages>count)
						{
							virtualPageTable.add(oldPages);
							oldPages.pages-=count;
							count=0;
						}else{
							virtualPageTable.add(new Pages(oldPages.pid, oldPages.pages));
							PageTable.remove(oldPages);
							count-=oldPages.pages;
						}//swap out processes of biggest age until there's enough space
						System.out.print("Swapped "+oldPages.pages+" pages from process "+oldPages.pid+" out to disk, ");
					}while(count>0);
				}
				//swap process in
				virtualPage.remove();
				PageTable.add(block);
			}
		}
		for(Pages page : PageTable) if(page.pid==p.id) inMemory=true; //check to see if the process exists
		if(!inMemory) //process doesn't exist in memory yet
		{
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
			System.out.print("P"+process.id+" page removed from memory, ");
		}
	}

	public static void printDebug() {
		System.out.println("  Memory:");
		System.out.println("    Loaded: " + "");
		System.out.println("    Used space: " + "");
		System.out.println("    Available space: " + "");
	}
}