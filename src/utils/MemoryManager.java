package utils;

import java.util.ArrayList;


public class MemoryManager {
	private static final byte MAIN=0;
	private static final byte DISK=1;
	public static int pageSize;
	public static int mainMemorySize;
	private class Pages {
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
	
	public void run(Process p) throws Exception
	{
		boolean inMemory = false;
		for(int i=0;i<PageTable.size();i++)
		{
			if(FrameTable.get(i).pid==p.id || PageTable.get(i).pid==p.id)
			{
				inMemory=true;
				if(FrameTable.get(i).pid==p.id) //if part of process is on disk
				{
					swapIn(FrameTable.get(i));	
				}
			}
		}
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
	
	public int allocate( Process process ){
		if(hasSpace(process.memory)) return 0;
		PageTable.add(new Pages(process.id, ((process.memory-1)/pageSize)+1));
		return ((process.memory-1)/pageSize)+1;
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
	private static void swapIn(Pages block)
	{
		if(!hasSpace(block.pages*pageSize))
		{
			int temp=0;
			for(int i=0;i<PageTable.size();i++)
			{
				if(FrameTable.get(i)==block)
				{
					
				}
				//need to swap out processes of biggest age until there's enough space
			}
		}
		//handle the swap here
	}
	private static void swapOut(Pages block)
	{
		
	}
}