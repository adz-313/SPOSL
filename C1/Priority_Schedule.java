import java.util.*;
import java.lang.*;
import java.io.*;
	
class Process
{
	String name;
	int arrival_time, burst_time, priority;
	Process(String n, int at, int bt, int p)
	{
		name = n;
		arrival_time = at;
		burst_time = bt;
		priority = p;
	}	
}

class SortByArrivalTime implements Comparator<Process>
{
	public int compare(Process a, Process b)
	{
		return a.arrival_time - b.arrival_time;
   	}
}

class SortByBurstTime implements Comparator<Process>
{
	public int compare(Process a, Process b)
	{
		return a.burst_time - b.burst_time;
   	}
}

class SortByPriority implements Comparator<Process>
{
	public int compare(Process a, Process b)
	{
		return a.priority - b.priority;
   	}
}

class MyClass
{
	public static void main(String[] args) throws ClassCastException
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter number of processes: ");
		int n = sc.nextInt();
		Vector<Process> process = new Vector<Process>();
		Vector<Process> process_clone = new Vector<Process>();
		Vector<Integer> completion_time = new Vector<Integer>();
		Vector<Integer> turn_around_time = new Vector<Integer>();
		Vector<Integer> waiting_time = new Vector<Integer>();
		Vector<String> gant_chart = new Vector<String>();
		int bt, at, priority;
		String name;
		for(int i=0; i<n; i++)
		{	
			sc.nextLine();
			System.out.print("Process name: ");
			name = sc.nextLine();
			System.out.print("Priority: ");
			priority = sc.nextInt();
			System.out.print("Arrival time for " + name + ": ");
			at = sc.nextInt();
			System.out.print("Burst time for " + name + ": ");
			bt = sc.nextInt();
			process.add(new Process(name, at, bt, priority));
			process_clone.add(new Process(name, at, bt, priority));
		}
		Collections.sort(process_clone, new SortByArrivalTime());
		/*if(process.get(0).arrival_time != 0)
        {
            int t = process_clone.get(0).arrival_time;
            for(int i=0; i<t; i++)
            {
                gant_chart.add("n");
            }
            for(int i=0; i<n; i++)
            {
                process_clone.get(i).arrival_time -= t;
            }
		}*/
		while(true)
		{
			int min_pt = 99999;
			int index = -1;
			for(int i=0; i<process.size(); i++)
			{
				if(process.get(i).priority < min_pt && process.get(i).arrival_time == 0)
				{
					index = i;
					min_pt = process.get(i).priority;
				}
			}
			if(index != -1)
			{
				process.get(index).burst_time--;
				gant_chart.add(process.get(index).name);
			}
			else
			{
				gant_chart.add("n");
			}
			for(int i=0; i<process.size(); i++)
			{
				if(process.get(i).arrival_time != 0)
				{
					process.get(i).arrival_time--;
				}
			}
			for(int i=0; i<process.size(); i++)
			{
				if(process.get(i).burst_time == 0)
				{
					process.removeElementAt(i);
				}
			}	
			if(process.size() == 0)
			{
				break;
			}	
		}
		for(int i=0; i<n; i++)
		{
			completion_time.add(gant_chart.lastIndexOf(process_clone.get(i).name) + 1);
		}
		for(int i=0; i<n; i++)
		{
			turn_around_time.add(completion_time.get(i) - process_clone.get(i).arrival_time);
		}
		for(int i=0; i<n; i++)
		{
			waiting_time.add(turn_around_time.get(i) - process_clone.get(i).burst_time);
		}
		System.out.println("");
		System.out.println("****Priority Scheduling****");
		System.out.println("");
		System.out.println("N  P  AT BT CT  TAT WT");
		for(int i=0; i<n; i++)
		{
			System.out.print(process_clone.get(i).name + " ");
			System.out.print(process_clone.get(i).priority + " ");
			System.out.print(process_clone.get(i).arrival_time + "  ");
			System.out.print(process_clone.get(i).burst_time + "  ");
			System.out.print(completion_time.get(i) + "   ");
			System.out.print(turn_around_time.get(i) + "   ");
			System.out.print(waiting_time.get(i));
			System.out.println("");
		}
		System.out.println("");
		System.out.println("Gant Chart: ");
		for(int i=1; i<gant_chart.size()+1; i++)
		{
			System.out.print(i + " ");
		}
		System.out.println("");
		for(int i=0; i<gant_chart.size(); i++)
		{
			System.out.print(gant_chart.get(i) + " ");
		}
		System.out.println("");
		System.out.println("");
		float sum = 0;
		for(float t : turn_around_time)
		{
			sum += t;
		}
		float average_tat = sum/n;
		sum = 0;
		for(float t : waiting_time)
		{
			sum += t;
		}
		float average_wt = sum/n;
		System.out.println("Average turn around time: " + average_tat);
		System.out.println("Average waiting time: " + average_wt);
	}
}
