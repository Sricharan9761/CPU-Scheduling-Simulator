package scheduler;

import model.Process;
import model.ExecutionSlice;
import java.util.*;

public class RoundRobin implements Scheduler {

    private int timeQuantum;

    public RoundRobin(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public List<ExecutionSlice> schedule(List<Process> processes) {


        List<Process> list = new ArrayList<>();
        for (Process p : processes) {
            list.add(p.clone());
        }


        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<ExecutionSlice> slices = new ArrayList<>();
        Queue<Process> queue = new LinkedList<>();

        int currentTime = 0;
        int index = 0;

        while (!queue.isEmpty() || index < list.size()) {


            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                queue.add(list.get(index));
                index++;
            }
            if (queue.isEmpty()) {
                currentTime = list.get(index).getArrivalTime();
                continue;
            }

            Process p = queue.poll();

            int execTime = Math.min(timeQuantum, p.getRemainingTime());
            int start = currentTime;
            int end = start + execTime;

            slices.add(new ExecutionSlice(p.getPid(), start, end));
            currentTime = end;

            p.setRemainingTime(p.getRemainingTime() - execTime);

            while (index < list.size() && list.get(index).getArrivalTime() <= currentTime) {
                queue.add(list.get(index));
                index++;
            }
            if (p.getRemainingTime() > 0) {
                queue.add(p);
            }
        }

        return mergeSlices(slices);
    }
    private List<ExecutionSlice> mergeSlices(List<ExecutionSlice> slices) {
        if (slices.isEmpty()) return slices;

        List<ExecutionSlice> merged = new ArrayList<>();
        merged.add(slices.get(0));

        for (int i = 1; i < slices.size(); i++) {
            ExecutionSlice last = merged.get(merged.size() - 1);
            ExecutionSlice curr = slices.get(i);

            if (last.getPid().equals(curr.getPid()) && last.getEnd() == curr.getStart()) {

                merged.set(merged.size() - 1,
                        new ExecutionSlice(last.getPid(), last.getStart(), curr.getEnd()));
            } else {
                merged.add(curr);
            }
        }
        return merged;
    }
}
