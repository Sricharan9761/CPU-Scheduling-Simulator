package scheduler;

import model.Process;
import model.ExecutionSlice;
import java.util.*;

public class SJF implements Scheduler {

    @Override
    public List<ExecutionSlice> schedule(List<Process> processes) {

        // Clone the process list
        List<Process> list = new ArrayList<>();
        for (Process p : processes) {
            list.add(p.clone());
        }

        // Sort by arrival time
        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<ExecutionSlice> slices = new ArrayList<>();

        // PriorityQueue sorted by burstTime
        PriorityQueue<Process> pq = new PriorityQueue<>(
                Comparator.comparingInt(Process::getBurstTime)
                        .thenComparingInt(Process::getArrivalTime)
                        .thenComparing(Process::getPid)
        );

        int currentTime = 0;
        int index = 0;

        while (!pq.isEmpty() || index < list.size()) {

            // Add all arrived processes into PQ
            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                pq.add(list.get(index));
                index++;
            }

            // If no processes have arrived yet
            if (pq.isEmpty()) {
                currentTime = list.get(index).getArrivalTime();
                continue;
            }

            // Take the process with the smallest burst time
            Process p = pq.poll();

            int start = currentTime;
            int end = start + p.getBurstTime();

            slices.add(new ExecutionSlice(p.getPid(), start, end));
            currentTime = end;
        }

        return slices;
    }
}
