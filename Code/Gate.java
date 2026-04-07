package Asia_Pacific_Airport;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Gate {
    private final String id;
    private boolean available = true;
    private final Lock gateLock = new ReentrantLock();

    // Constructor for gate class with give ID
    public Gate(String id) {
        this.id = id;
    }

    // Getter for the gate's ID
    public String getId() {
        return id;
    }

    // Getter for the availability of the gates
    public boolean isAvailable() {
        return available;
    }

    // Setter for the availability of the gates
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Overridden toString method to return the gate's ID as its string representation
    @Override
    public String toString() {
        return id;
    }
}
