package Asia_Pacific_Airport;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class AirportSimulator {
    public static void main(String[] args) {
        // Initialize and start the simulation
        ATC atc = new ATC();

        // Creating semaphores for runway, gate and refuel truck to control access
        Semaphore runwaySemaphore = new Semaphore(1); // Only one plane can take off or land at a time
        Semaphore gateSemaphore = new Semaphore(3); // Three planes can be docked at gates simultaneously
        Semaphore refuelingSemaphore = new Semaphore(1); // Only one plane can refuel at a time as there are only one refuel truck

        // Creating a thread for ATC to start the simulation
        Thread atcThread = new Thread(atc);
        atcThread.start();

        // For loop to create 6 plane threads and initiate their concurrent processes
        for (int i = 0; i < 6; i++) {
            Plane plane = new Plane("Plane-" + (i+1), atc, runwaySemaphore, gateSemaphore, refuelingSemaphore);
            new Thread(plane).start();
            try {
                Thread.sleep(new Random().nextInt(100,2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
