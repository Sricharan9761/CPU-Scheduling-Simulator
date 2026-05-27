package scheduler;
import model.Process;
import model.ExecutionSlice;
import java.util.*;
public class FCFS  implements Scheduler{
    public List<ExecutionSlice> schedule(List<Process> processes){
        List<Process> list = new ArrayList<>();

        for(Process p : processes){
            list.add(p.clone());
        }

        list.sort(Comparator.comparingInt(Process::getArrivalTime));
        List<ExecutionSlice> slices = new ArrayList<>();
        int currentTime = 0;

        for(Process p : list){
            if(currentTime < p.getArrivalTime()){
                currentTime = p.getArrivalTime();
            }

            int start = currentTime;
            int end = start+p.getBurstTime();

            slices.add(new ExecutionSlice(p.getPid() , start , end));
            currentTime = end;
        }
        return slices;
    }
}
