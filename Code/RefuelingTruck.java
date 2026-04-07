package Asia_Pacific_Airport;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RefuelingTruck implements Runnable{
    private final Plane plane;
    private final Lock refuelingLock = new ReentrantLock(); // Lock to ensure exclusive access to the refueling process

    // Constructor to initialize the RefuelingTruck with a specific plane
    public RefuelingTruck(Plane plane) {
        this.plane = plane;
    }

    @Override
    public void run() {

        // Acquire the lock before refueling
        refuelingLock.lock();
        try {
            // Simulate refueling
            System.out.println("Refueling worker: Currently refuelling " + plane.getId() + ".");
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Refueling worker: "+ plane.getId() + " had successfully refueled.");
        } finally {
            // Release the lock after refueling is complete
            refuelingLock.unlock();
        }
    }
}
