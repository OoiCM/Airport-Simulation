package Asia_Pacific_Airport;

import java.util.concurrent.CountDownLatch;

public class Passenger implements Runnable{
    Plane plane;
    Action action;

    // Constructor to initialize a passenger with the plane and an action (from the Action enum)
    public Passenger(Plane plane, Action action) {
        this.plane = plane;
        this.action = action;
    }

    // Method to simulate boarding of passengers onto the plane
    public void boardPlane(Plane plane) {
        System.out.println(plane.getId() + ": Passengers are currently boarding onto " + plane.getId() + ".");

        // Simulate the time taken for each passenger to board (100 ms for each passenger)
        for (int i=0 ; i < plane.getEmbPassNum(); i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to simulate disembarking of passengers from the plane
    public void disembarkPlane(Plane plane) {
        System.out.println(plane.getId() + ": Passengers are currently disembarking from " + plane.getId() + ".");

        // Simulate the time taken for each passenger to disembark (100 ms for each passenger)
        for (int i=0 ; i < plane.getDisPassNum(); i++){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Execute the passenger action based on the action when the thread is created
    @Override
    public void run() {
        if (action.equals(Action.Disembark))
        {
            disembarkPlane(plane);
        }
        else if (action.equals(Action.Embark))
        {
            boardPlane(plane);
        }
    }
}
