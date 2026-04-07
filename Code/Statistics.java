package Asia_Pacific_Airport;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private final List<Long> waitingTimes = new ArrayList<>();
    final AtomicInteger planesServed = new AtomicInteger(0);
    private final AtomicInteger passengersBoarded = new AtomicInteger(0);
    private long startSimulationTime;
    private long endSimulationTime;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    // Method to record the start time of the simulation
    public void startSimulation() {
        this.startSimulationTime = System.currentTimeMillis();
    }

    // Method to record the end time of the simulation
    public void endSimulation() {
        this.endSimulationTime = System.currentTimeMillis();
    }

    // Method to add the waiting time of the planes to the list
    public void addWaitingTime(long waitingTime) {
        synchronized (waitingTimes) {
            waitingTimes.add(waitingTime);
        }
    }

    // Method to increment the number of planes served
    public void incrementPlanesServed() {
        planesServed.incrementAndGet();
    }

    // Method to add to the total number of passengers boarded
    public void addPassengersBoarded(int count) {
        passengersBoarded.addAndGet(count);
    }

    // Method to print the statistics at the end of the simulation
    public void printStatistics() {

        // Calculate the total simulation time
        long totalSimulationTime = endSimulationTime - startSimulationTime;
        System.out.println("\n=======================================Statistics======================================= ");
        System.out.println("Total simulation time: " + totalSimulationTime/1000 + " seconds.");
        System.out.println("Number of planes served: " + planesServed.get());
        System.out.println("Number of passengers boarded: " + passengersBoarded.get());

        // Synchronize on the waitingTimes to ensure thread safety, only one thread can access at a time
        synchronized (waitingTimes) {
            if (!waitingTimes.isEmpty()) {

                long maxWaitTime = waitingTimes.stream().max(Long::compare).orElse(0L);
                long minWaitTime = waitingTimes.stream().min(Long::compare).orElse(0L);
                double avgWaitTime = waitingTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

                System.out.println("Maximum waiting time: " + maxWaitTime/1000 + " seconds.");
                System.out.println("Average waiting time: " + decfor.format(avgWaitTime/1000) + " seconds");
                System.out.println("Minimum waiting time: " + minWaitTime/1000 + " seconds.");
                System.out.println("===================================End of Simulation====================================");

                System.exit(0);
            }
        }
    }

    // Method to check if all gates are empty
    public boolean allGatesEmpty(List<Gate> gates) {
        for (Gate gate : gates) {
            if (!gate.isAvailable()) {
                return false;
            }
        }
        return true;
    }
}
