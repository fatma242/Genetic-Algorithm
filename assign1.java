import java.util.Scanner;

public class assign1{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        int NumberOfTestCases = input.nextInt();
        for (int i = 0; i < NumberOfTestCases; i++) {
            int MaxTime = input.nextInt();
            int NumberOfTasks = input.nextInt();
            int[] tasks = new int[NumberOfTasks];
            for(int j = 0; j < NumberOfTasks; j++){
                tasks[j] = input.nextInt();
            }
            Task task = new Task(MaxTime, tasks);
            task.allocate();
        }
        input.close();
    }
}