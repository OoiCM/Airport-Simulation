package Asia_Pacific_Airport;

public class Worker implements Runnable{
    int id;
    Plane plane;
    Action action;

    // Constructor to initialize a worker with an ID, a plane, and an action
    public Worker(int id, Plane plane, Action action) {
        this.id = id;
        this.plane = plane;
        this.action = action;
    }

    // Method to simulate cleaning the plane
    public void Cleaning(Plane plane){
        System.out.println("Worker " + id + ": Currently cleaning " + plane.getId() + ".");

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Worker " + id + ": Cleaning process on " + plane.getId() + " had been successfully executed.");
    }

    // Method to simulate refilling supplies on the plane
    public void Refill(Plane plane){
        System.out.println("Worker " + id + ": Currently refilling supplies on " + plane.getId() + ".");

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Worker " + id + ": The supplies on " + plane.getId() + " had been successfully refilled.");
    }

    // Execute the worker's action based on the action when the thread is created
    @Override
    public void run() {
        if (action.equals(Action.Cleaning))
        {
            Cleaning(plane);
        }
        else if (action.equals(Action.Refill))
        {
            Refill(plane);
        }
    }
}
