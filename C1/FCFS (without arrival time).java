import java.util.*;
	
class MyClass
{
	static void waitTime(int burst_time[], int wait_time[], int n)
	{
		wait_time[0] = 0;
		for(int i=1; i<n; i++)
		{
			wait_time[i] = burst_time[i-1] + wait_time[i-1];
		}
	}

	static void turnAroundTime(int turn_around_time[], int burst_time[], int wait_time[], int n)
	{
		for(int i=0; i<n; i++)
		{
			turn_around_time[i] = burst_time[i] + wait_time[i];
		}
	}

	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter number of processes: ");
		int n = sc.nextInt();
		int process[] = new int[n];
		int burst_time[] = new int[n];
		int wait_time[] = new int[n];
		int turn_around_time[] = new int[n];
		for(int i=0; i<n; i++)
		{
			System.out.println("Process number: ");
			process[i] = sc.nextInt();
			System.out.println("Burst time for process number " + process[i] + ": ");
			burst_time[i] = sc.nextInt();
		}
		waitTime(burst_time, wait_time, n);
		turnAroundTime(turn_around_time, burst_time, wait_time, n);
		float total_wait_time = 0, total_turn_around_time = 0;
		for(int i=0; i<n; i++)
		{
			total_wait_time += wait_time[i];
			total_turn_around_time += turn_around_time[i];
		}
		float average_wait_time = total_wait_time/n;
		float average_turn_around_time = total_turn_around_time/n;
		for(int i=0; i<n; i++)
		{
			System.out.println("Wait time for " + process[i] + ": " + wait_time[i]);
			System.out.println("Turn around time for " + process[i] + ": " + turn_around_time[i] + "\n");
		}
		System.out.println("Average wait time: " + average_wait_time);
		System.out.println("Average turn around time: " + average_turn_around_time);
	}

}