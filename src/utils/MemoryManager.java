package utils;

import java.util.ArrayList;

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
	private static ArrayList<Pages> FrameTable = new ArrayList<Pages>();
	
	public static void run(Process p) throws SharedResourceException
	{
		boolean inMemory = false;
		for(Pages frame : FrameTable)
		{
			if(frame.pid==p.id)
			{
				inMemory=true;
				if(frame.pid==p.id) //if part of process is on disk
				{
					swap(frame);	
				}
			}
		}
		for(Pages page : PageTable) if(page.pid==p.id) inMemory=true;
		if(!inMemory)
		{
			FrameTable.add(new Pages(p.id, (p.memory/pageSize)+1)); //give process memory for first time
			run(p);
		}else{
			p.step(); //increment process age
		}
	}
	
	public static boolean hasSpace( int memoryToStore ){
		int temp=0;
		for(Pages i : PageTable){
			temp += i.pages;
		}
		return ((temp+((memoryToStore-1)/pageSize)+1)<=mainMemorySize);
	}
	public static void deallocate( Process process ){
		for(int i=0;i<PageTable.size();i++)
		{
			if(PageTable.get(i).pid==process.id)
			{
				PageTable.remove(i);
			}
		}
	}
	private static void swap(Pages block)
	{
		if(!hasSpace(block.pages*pageSize))
		{
			int count=block.pages;
			do
			{
				Process minAge=null;
				for(Process p : scheduler.processes)
				{
					if(minAge==null || (p.age<minAge.age && minAge.id!=block.pid))
					{
						for(Pages pt : PageTable) if(pt.pid==p.id) minAge=p;
					}
				}
				Pages oldPages = null;
				for(Pages p : PageTable) if(p.pid==minAge.id) oldPages=p;
				if(oldPages.pages>count)
				{
					FrameTable.add(oldPages);
					oldPages.pages-=count;
					count=0;
				}else{
					FrameTable.add(new Pages(oldPages.pid, oldPages.pages));
					PageTable.remove(oldPages);
					count-=oldPages.pages;
				}//swap out processes of biggest age until there's enough space
			}while(count>0);
			
		}
		//swap process in
		FrameTable.remove(block);
		PageTable.add(block);
	}
}