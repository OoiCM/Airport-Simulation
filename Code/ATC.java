package Asia_Pacific_Airport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ATC implements Runnable {
    private static final Semaphore runwaySemaphore = new Semaphore(1);
    private static final Semaphore gateSemaphore = new Semaphore(3);
    private static final Semaphore refuelingSemaphore = new Semaphore(1);
    private static LinkedList<Plane> landingQueue;
    private static int planeOnGround = 0;
    private static final List<Gate> gates = Arrays.asList(new Gate("Gate-1"), new Gate("Gate-2"), new Gate("Gate-3"));
    private static final Statistics statistic = new Statistics();
    private static final int TOTAL_PLANES = 6;

    // Constructor for ATC class
    public ATC(){

        // Create a landingQueue linked list to store the plane that is waiting in queue for landing
        landingQueue = new LinkedList<Plane>();
    }

    // Confirm whether the plane can land or not after receiving plane's request to land
    public static void confirmLanding(Plane plane) {

        // Synchronize on the landingQueue to ensure thread safety, only one thread can access at a time
        synchronized (landingQueue) {

            // If the plane does not have an emergency, add it to the end of the landing queue
            if (!plane.getEmergencyStatus()){
                landingQueue.addLast(plane);
            }
            else{
                // If the plane has an emergency, add it to the front of the landing queue and notify the plane
                landingQueue.addFirst(plane);
                System.out.println("ATC: "+ plane.getId() + ": Request accepted. Successfully moved " + plane.getId() + " to the first in the queue.");
            }

            // Record the time the plane requested to land
            plane.setRequestTime(System.currentTimeMillis());

            // Notify any waiting plane threads that the state of the landingQueue has changed
            landingQueue.notifyAll();

            // Prompt message to plane when airport is full (3 or more planes on airport)
            if (planeOnGround >= 3){
                System.out.println("ATC: "+ plane.getId() + ": Airport is full! Please wait in queue for further notice.");
            }

            // Put the plane to wait when the runway is not available and airport is full
            while (planeOnGround >= 3 || runwaySemaphore.availablePermits() == 0) {
                try {
                    landingQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // After the waiting plane is notified, the first plane in the queue will land
            LandPlane(landingQueue.poll());
        }
    }

    // Plane Landing
    public static void LandPlane(Plane plane){
        // Synchronize on the landingQueue to ensure thread safety, only one thread can access at a time
        synchronized (landingQueue){

            // Acquire the runway semaphore for the plane to land
            try{
                runwaySemaphore.acquire();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("ATC: "+ plane.getId() + ": Runway is available." + plane.getId() + " may land now.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(plane.getId() + ": Successfully landed!");

            // Record the time the plane successfully landed
            plane.setLandTime(System.currentTimeMillis());

            // Increment 1 to plane on ground
            planeOnGround++;

            // Set landstatus back to true
            plane.setLandStatus(true);

            // Calculate the waitingTime and add it into the statistic variable
            long waitingTime = plane.getLandTime() - plane.getRequestTime();
            statistic.addWaitingTime(waitingTime);

            // After landing successfully, release the semaphore for other plane to use
            runwaySemaphore.release();

            // Notify the planes in the landing queue
            landingQueue.notifyAll();
        }
    }

    // Determine available gates from the array list and return the index number of the gate
    public static int assignGate(){
        for (int i = 0; i < gates.size(); i++) {
            if (gates.get(i).isAvailable()) {

                // Disable the gate so that other plane won't get the same gate at a same time
                gates.get(i).setAvailable(false);
                return i;
            }
        }
        return -1; // Should never reach here
    }

    // Confirm the plane's request to coast and dock to available gate after landed
    public static void ConfirmCoastAndDock(Plane plane) {
        if (gateSemaphore.availablePermits() >=1){
            // Acquire the gate's semaphore for the plane to dock
            try{
                gateSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Get the available gate using the assignGate() method and store the string into gate variable
            int availableGateIndex = assignGate();
            Gate gate = gates.get(availableGateIndex);


            if (availableGateIndex != -1) {
                // Assign the available gate to the plane to coast to and dock
                plane.setAssignedGate(gate.getId(), availableGateIndex);
                System.out.println("ATC: " + plane.getId() + ": Please proceed to coast and dock in " + gate + ".");
            }else{
                System.out.println("ATC: "+ plane.getId() + ": No gate available. Please wait for further notice."); //Should never reach here
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(plane.getId() + ": Successfully coasted to " + gate + " and docked.");
        }
    }

    // Confirm the plane's request to disembark passengers to the airport
    public static void DisembarkPassenger(Plane plane){
        System.out.println("ATC: "+ plane.getId() + ": Passenger from " + plane.getId() + " may disembark from the plane now. Please remember to " + "take all you belongings with you!");

        // Create passenger thread to run disembark function
        Passenger pass = new Passenger(plane,Action.Disembark);
        Thread disembarkpass = new Thread(pass);
        disembarkpass.start();

        try {
            // Use join() for the process to be completed before other process continues
            disembarkpass.join();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(plane.getId() + ": All " + plane.getDisPassNum() + " passengers from " + plane.getId() + " had successfully disembarked!");
    }

    // Confirm the plane's request to refuel plane
    public static void refuelPlane(Plane plane) throws InterruptedException {

        // Create thread for refuelling truck
        RefuelingTruck refueling = new RefuelingTruck(plane);
        Thread refuel = new Thread(refueling);

        // Acquire the refuel semaphore to refuel the plane
        refuelingSemaphore.acquire();
        try {
            // Try to start the thread and use join() to wait for the refuelling process to finish
            System.out.println("ATC: "+ plane.getId() + ": Request for refuelling approved.");
            refuel.start();
            refuel.join();
        } finally {
            // Release the refuel semaphore after the refuelling process is done
            refuelingSemaphore.release();
        }
    }

    // Confirm the plane's request to clean the plane and refill supplies to the plane
    public static void CleaningAndRefilling(Plane plane){
        System.out.println("ATC: "+ plane.getId() + ": Request accepted. Cleaning and Refilling process on " + plane.getId() + " will now occur simultaneously.");

        // Create two workers object to clean and refill simultaneously
        Worker worker1 = new Worker(1,plane, Action.Cleaning);
        Worker worker2 = new Worker(2,plane, Action.Refill);

        // Create threads  for both clean and refill
        Thread clean = new Thread(worker1);
        Thread refill = new Thread(worker2);

        // Start the thread
        clean.start();
        refill.start();

        // Use join() for both process to be completed before other process continues
        try{
            clean.join();
            refill.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Embark passengers onto the plane
    public static void EmbarkPassenger(Plane plane){
        System.out.println("ATC: "+ plane.getId() + ": Passengers will now embark onto " + plane.getId() + ". (Passenger total: " + plane.getEmbPassNum() + ")");

        // Create passengers thread to run the boardPlane function
        Passenger pass = new Passenger(plane,Action.Embark);
        Thread embarkpass = new Thread(pass);
        embarkpass.start();

        try {
            // Use join() for the process to be completed before other process continues
            embarkpass.join();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("ATC: All " + plane.getEmbPassNum() + " passengers has successfully embark onto " + plane.getId() + ". Please be prepared to take off.");

        // Add the passengers boarded to the total passengers boarded statistics
        statistic.addPassengersBoarded(plane.getEmbPassNum());
    }

    // Confirm the plane's request to undock from the gate after every ground operation is done
    public static void ConfirmUndock(Plane plane) {
        System.out.println("ATC: " + plane.getId() + ": Request accepted." + plane.getId() + " may now undock from " + plane.getGate() + ".");

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(plane.getId() + ": Successfully undocked from " + plane.getGate() + ".");

        // Release the gate after the plane successfully undocked from the gate
        releaseGate(plane.getAssignedGateIndex());
    }

    // Method to release gate after the planes undock successfully
    public static void releaseGate(int gateIndex) {

        // Set the availability of gate back to true and release gate semaphore
        gates.get(gateIndex).setAvailable(true);
        gateSemaphore.release();
        System.out.println("ATC: " + gates.get(gateIndex).getId() + " is now available.");
    }

    // Confirm the plane's request to takeoff from the airport
    public static void Takeoff(Plane plane) throws InterruptedException {

        // Synchronize on the landingQueue to ensure thread safety, only one thread can access at a time
        synchronized (landingQueue){

            // Acquire the runway semaphore for the plane to take off
            try {
                runwaySemaphore.acquire();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            System.out.println("ATC: " + plane.getId() + ": Runway is available. " + plane.getId() + " may now coast to runway and take off.");

            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(plane.getId() + ": Successfully took off! Thanks for the service.");

            // Decrement 1 for the plane on ground
            planeOnGround--;

            // Set landstatus back to false
            plane.setLandStatus(false);

            // Release the runway semaphore after the plane successfully took off
            runwaySemaphore.release();

            // Notify the planes in the landing queue
            landingQueue.notifyAll();

            // Increment the number fo planes served
            statistic.incrementPlanesServed();

            // If all planes are served and the gates are all empty, end recording the simulation time and print the statistics
            if (statistic.planesServed.get() == TOTAL_PLANES && statistic.allGatesEmpty(gates)) {
                statistic.endSimulation();
                statistic.printStatistics();
            }
        }
    }

    // Start the simulation when the atc thread is created and started
    public void run() {

        // Start recording the simulation time
        statistic.startSimulation();

        // Main loop to manage plane landings
        while (true) {
            synchronized (landingQueue) {

                // Wait while the landing queue is empty until a plane request for landing
                while (landingQueue.isEmpty()) {
                    try {
                        landingQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}