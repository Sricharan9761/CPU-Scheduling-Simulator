package scheduler;

import model.Process;
import model.ExecutionSlice;
import java.util.*;

public class PriorityScheduling implements Scheduler {

    @Override
    public List<ExecutionSlice> schedule(List<Process> processes) {

        // Clone list so original is not changed
        List<Process> list = new ArrayList<>();
        for (Process p : processes) {
            list.add(p.clone());
        }

        // Sort by arrival time first
        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<ExecutionSlice> slices = new ArrayList<>();
        PriorityQueue<Process> pq = new PriorityQueue<>(
                Comparator.comparingInt(Process::getPriority)
                        .thenComparingInt(Process::getArrivalTime)
                        .thenComparing(Process::getPid)
        );

        int currentTime = 0;
        int index = 0;

        while (!pq.isEmpty() || index < list.size()) {
            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                pq.add(list.get(index));
                index++;
            }
            if (pq.isEmpty()) {
                currentTime = list.get(index).getArrivalTime();
                continue;
            }
            Process p = pq.poll();

            int start = currentTime;
            int end = start + p.getBurstTime();

            slices.add(new ExecutionSlice(p.getPid(), start, end));
            currentTime = end;
        }

        return slices;
    }
}
