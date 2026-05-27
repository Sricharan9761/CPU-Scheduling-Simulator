package scheduler;

import model.Process;
import model.ExecutionSlice;
import java.util.*;

public class SRTF implements Scheduler {

    @Override
    public List<ExecutionSlice> schedule(List<Process> processes) {

        // Clone the list so original data is untouched
        List<Process> list = new ArrayList<>();
        for (Process p : processes) list.add(p.clone());

        // Sort by arrival time
        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<ExecutionSlice> slices = new ArrayList<>();

        // Priority queue sorted by remaining time
        PriorityQueue<Process> pq = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
                        .thenComparingInt(Process::getArrivalTime)
                        .thenComparing(Process::getPid)
        );

        int currentTime = 0;
        int index = 0;
        String runningPid = null;
        int runStart = -1;

        while (!pq.isEmpty() || index < list.size()) {

            // Add all newly arrived processes
            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                pq.add(list.get(index));
                index++;
            }

            // If nothing to run, jump to next arrival time
            if (pq.isEmpty()) {
                currentTime = list.get(index).getArrivalTime();
                continue;
            }

            // Pick the process with smallest remaining time
            Process p = pq.poll();

            // Preemption check: if new process starts running
            if (!p.getPid().equals(runningPid)) {

                // Close previous slice
                if (runningPid != null) {
                    slices.add(new ExecutionSlice(runningPid, runStart, currentTime));
                }

                // Start new slice
                runningPid = p.getPid();
                runStart = currentTime;
            }

            // Execute for 1 unit
            p.setRemainingTime(p.getRemainingTime() - 1);
            currentTime++;

            // Add arrivals during execution
            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                pq.add(list.get(index));
                index++;
            }

            // If still remaining, put back
            if (p.getRemainingTime() > 0) {
                pq.add(p);
            } else {
                // Finished → close slice
                slices.add(new ExecutionSlice(p.getPid(), runStart, currentTime));
                runningPid = null;
                runStart = -1;
            }
        }

        return slices;
    }
}
