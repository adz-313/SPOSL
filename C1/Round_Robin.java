import java.util.*;

class Process
{
	String name;
	int arrival_time, burst_time;
	Process(String n, int at, int bt)
	{
		name = n;
		arrival_time = at;
		burst_time = bt;
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

class MyClass
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
		System.out.print("Enter number of processes: ");
		int n = sc.nextInt();
		Vector<Process> process = new Vector<Process>();
		Vector<Process> process_clone = new Vector<Process>();
		Vector<Integer> completion_time = new Vector<Integer>();
		Vector<Integer> turn_around_time = new Vector<Integer>();
		Vector<Integer> waiting_time = new Vector<Integer>();
		Vector<String> gant_chart = new Vector<String>();
        int bt, at, time_quantum;
        System.out.print("Enter time quantum: ");
        time_quantum = sc.nextInt();
		String name;
		for(int i=0; i<n; i++)
		{	
			sc.nextLine();
			System.out.print("Process name: ");
			name = sc.nextLine();
			System.out.print("Arrival time for " + name + ": ");
			at = sc.nextInt();
			System.out.print("Burst time for " + name + ": ");
			bt = sc.nextInt();
			process.add(new Process(name, at, bt));
			process_clone.add(new Process(name, at, bt));
		}	
		Collections.sort(process_clone, new SortByArrivalTime());
        //check if any process has arrival time = 0
        //if not, wait till it becomes zero
        if(process_clone.get(0).arrival_time != 0)
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
        }
        int temp = 0;
        while(true)
        {
            if(process_clone.size() == 0)
            {
                break;
            }
            for(int i=0; i<process_clone.size(); i++)
            {
                if(process_clone.get(i).arrival_time == 0)
                {
                    if(process_clone.get(i).burst_time > time_quantum)
                    {
                        temp = time_quantum;
                        process_clone.get(i).burst_time -= time_quantum;
                        for(int j=0; j<time_quantum; j++)
                        {
                            gant_chart.add(process_clone.get(i).name);
                        }
                    }
                    else
                    {
                        temp = process_clone.get(i).burst_time;
                        for(int j=0; j<process_clone.get(i).burst_time; j++)
                        {
                            gant_chart.add(process_clone.get(i).name);
                        }
                        process_clone.get(i).burst_time = 0;
                    }
                }
                for(int j=0; j<process_clone.size(); j++)
                {
                    if(process_clone.get(j).arrival_time < temp)
                    {
                        process_clone.get(j).arrival_time = 0;
                    }
                    else
                    {
                        process_clone.get(j).arrival_time -= temp;
                    }
                }
            }
            for(int i=0; i<process_clone.size(); i++)
            {
                if(process_clone.get(i).burst_time == 0)
                {
                    process_clone.removeElementAt(i);
                }
            }
        }
        for(int i=0; i<n; i++)
		{
			completion_time.add(gant_chart.lastIndexOf(process.get(i).name) + 1);
		}
		for(int i=0; i<n; i++)
		{
			turn_around_time.add(completion_time.get(i) - process.get(i).arrival_time);
		}
		for(int i=0; i<n; i++)
		{
			waiting_time.add(turn_around_time.get(i) - process.get(i).burst_time);
		}
		System.out.println("");
		System.out.println("****Round Robin****");
		System.out.println("");
		System.out.println("N  AT BT CT  TAT WT");
		for(int i=0; i<n; i++)
		{
			System.out.print(process.get(i).name + " ");
			System.out.print(process.get(i).arrival_time + "  ");
			System.out.print(process.get(i).burst_time + "  ");
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