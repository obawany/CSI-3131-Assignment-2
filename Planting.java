// Name: Muhammad Owais Bawany
// Student id: 7993647
//
// The Planting Synchronization Problem
//
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Planting 
{
	public static void main(String args[]) 
	{
		int i;
	 	// Create Student, TA, Professor threads
		TA ta = new TA();
		Professor prof = new Professor(ta);
		Student stdnt = new Student(ta);

		// Start the threads
		prof.start();
		ta.start();
		stdnt.start();

		// Wait for prof to call it quits
		try {prof.join();} catch(InterruptedException e) { }; 
		// Terminate the TA and Student Threads
		ta.interrupt();
		stdnt.interrupt();
	}   
}

class Student extends Thread
{
	TA ta;

	public Student(TA taThread)
        {
	    ta = taThread;
	}


	public void run()
	{

		while(true)
		{

		try{
			ta.max_hole.acquire(); 
			// if (ta.max_hole.availablePermits() == 0){
		     	// ta.shovel_share.release(1);
		 		// }	
		     // Can dig a hole - lets get the shovel
		    //try acqure uniterruptible if just try acquire doesnt work 
		    try{
		     ta.shovel_share.acquireUninterruptibly(1);

		     System.out.println("Student: Got the shovel");
	             try {sleep((int) (100*Math.random()));} catch (Exception e) { break;} // Time to fill hole
                     ta.incrHoleDug();  // hole filled - increment the number	
		     System.out.println("Student: Hole "+ta.getHoleDug()+" Dug");
		     System.out.println("Student: Letting go of the shovel");
		     ta.empty_hole.release();
		     ta.shovel_share.release();
		 
		 }
		 finally {
		 	ta.shovel_share.release();
		 }
		}
		
		catch(InterruptedException e1)
			{
			System.out.println("Student: Must wait for TA "+ta.getMAX()+" holes ahead\n");
			//e1.printStackTrace();
			break;
			}
		
		finally {
			 ta.unfilled_hole.release();
		     if(isInterrupted()) break;
		}
		
	
		}
		System.out.println("Student is done");
		}
		}



class TA extends Thread
{
	// Some variables to count number of holes dug and filled - the TA keeps track of things
	private int holeFilledNum=0;  // number of the hole filled
	private int holePlantedNum=0;  // number of the hole planted
	private int holeDugNum=0;     // number of hole dug
	private final int MAX=5;   // can only get 5 holes ahead
	// add semaphores - the professor lets the TA manage things.

	// private final Semaphore available = new Semaphore(3, true);
			// Instead of Semaphores for people
		// I'll try semaphores for tasks
	// Semaphore student_semaphore;
	// Semaphore professor_semaphore;
	// Semaphore ta_semaphore;

	Semaphore empty_hole;
	Semaphore unfilled_hole;
	Semaphore max_hole;
	Semaphore shovel_share;
	

	public int getMAX() { return(MAX); }
	public void incrHoleDug() { holeDugNum++; }
	public int getHoleDug() { return(holeDugNum); }
	public void incrHolePlanted() { holePlantedNum++; }
	public int getHolePlanted() { return(holePlantedNum); }

	public TA()
	{
		// Initialise things here
		//Initializing the three semaphores
		// Instead of Semaphores for people
		// I'll try semaphores for tasks
		// ta_semaphore      		= new Semaphore(0, true);
		// professor_semaphore 	= new Semaphore(0, true);
		// student_semaphore 		= new Semaphore(MAX, true);

		empty_hole 		= new Semaphore(0,true);
		unfilled_hole	= new Semaphore(0, true);
		max_hole		= new Semaphore(5, true);
		shovel_share	= new Semaphore(1, true);

	}
	
	public void run()
	{
		// available.acquire();
		try{
		unfilled_hole.acquire();}
		catch(InterruptedException e2)
		{e2.printStackTrace();}
		
		while(true)
		{	
			shovel_share.acquireUninterruptibly(1);
			 // if (ta_semaphore.tryacquire() == 1){
			 	System.out.println("TA: Got the shovel");
	            // ta_semaphore.acquire();
	            // shovel_share.tryacquire();


	             try {sleep((int) (100*Math.random()));} catch (Exception e) { break;} // Time to fill hole
                     holeFilledNum++;  // hole filled - increment the number	
		     System.out.println("TA: The hole "+holeFilledNum+" has been filled");
		     System.out.println("TA: Letting go of the shovel");
		     //unfilled_hole.release();
		     shovel_share.release();
		     max_hole.release();
		     
		     if(isInterrupted()) break;
		}
		System.out.println("TA is done");

	}

}


class Professor extends Thread
{
	TA ta;

	public Professor(TA taThread)
        {
	    ta = taThread;
	}


	
	
	public void run()
	{
		try {
		ta.empty_hole.acquire();}
		catch(InterruptedException e3)
		{e3.printStackTrace();}

		while(ta.getHolePlanted() <= 20)
		{
				// TA.empty_hole.acquire();
		  //    	TA.incrHolePlanted();

	            try {sleep((int) (50*Math.random()));
	            	ta.empty_hole.acquire();} catch (Exception e) { break;} // Time to plant
                     ta.incrHolePlanted();  // the seed is planted - increment the number	
		     System.out.println("Professor: All be advised that I have completed planting hole "+
				        ta.getHolePlanted());
		    ta.unfilled_hole.release();
		}

		System.out.println("Professeur: We have worked enough for today");
	}
}

