package scheduler;
import  model.Process;
import model.ExecutionSlice;
import java.util.List;
public interface Scheduler {
   List<ExecutionSlice> schedule(List<Process> processes);
}
