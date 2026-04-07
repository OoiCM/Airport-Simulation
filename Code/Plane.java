package Asia_Pacific_Airport;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Plane implements Runnable{
    public static boolean landStatus;
    private final String id;
    private String assignedGate;
    private int assignedGateIndex;
    private final ATC atc;
    private boolean emergencyLanding;
    private final Semaphore runwaySemaphore;
    private final Semaphore gateSemaphore;
    private final Semaphore refuelingSemaphore;
    private int DisPassNum, EmbPassNum;
    private long requestTime;
    private long landTime;

    // Setter for assignedGate
    public void setAssignedGate(String assignedGate, int gateIndex) {
        this.assignedGate = assignedGate;
        this.assignedGateIndex = gateIndex;
    }

    // Setter for plane's landStatus
    public void setLandStatus(boolean status) {
        this.landStatus = status;
    }

    // Setter for plane's requestTime
    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    // Setter for plane's LandTime
    public void setLandTime(long landTime) {
        this.landTime = landTime;
    }

    // Getter for the plane's ID
    public String getId(){
        return id;
    }

    // Getter for the plane's emergency status
    public boolean getEmergencyStatus(){
        return emergencyLanding;
    }

    // Getter for assignedGate
    public String getGate() {
        return assignedGate;
    }

    // Getter for assignedGate index
    public int getAssignedGateIndex() {
        return assignedGateIndex;
    }

    // Getter for the plane's number of passengers that is going to disembark
    public int getDisPassNum(){
        return DisPassNum;
    }

    // Getter for number of passengers that is going to board onto the plane
    public int getEmbPassNum(){
        return EmbPassNum;
    }

    // Getter for plane's requestTime
    public long getRequestTime() {
        return requestTime;
    }

    // Getter for plane's LandTime
    public long getLandTime() {
        return landTime;
    }

    // Constructor for Plane class
    public Plane(String id, ATC atc, Semaphore runwaySemaphore, Semaphore gateSemaphore, Semaphore refuelingSemaphore) {
        this.id = id;

        // Set Plane-6 to emergency case (Fuel Shortage)
        if (id.equals("Plane-6") ){
            this.emergencyLanding = true;
        }else{
            this.emergencyLanding = false;
        }

        this.landStatus = false;
        this.atc = atc;
        this.runwaySemaphore = runwaySemaphore;
        this.gateSemaphore = gateSemaphore;
        this.refuelingSemaphore = refuelingSemaphore;

        // Randomize number of passengers to disembark from and embark onto each planes
        this.DisPassNum = new Random().nextInt(50+1-10)+10;
        this.EmbPassNum = new Random().nextInt(50+1-10)+10;

    }

    // Plane send request to ATC for permission to Land after arriving the airport
    public void requestLand(){

        // Prompt different message according to the plane's emergency status
        if (emergencyLanding == true){
            System.out.println(id + ": Encountered fuel shortage and request for an emergency landing!");
        }else{
            System.out.println(id + ": Request for landing!");
        }

        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Bring the flow to ATC's function
        ATC.confirmLanding(this);
    }

    // After landing successfully, plane send request to ATC for coasting and docking to available gate
    public void requestCoastAndDock() throws InterruptedException {
        System.out.println(id+ ": Request to coast to available gate.");

        // Bring the flow to ATC's function
        ATC.ConfirmCoastAndDock(this);
    }

    // After docking successful to a gate, plane request to ATC for disembarking passengers
    public void requestDisembarkPassenger() throws InterruptedException {
        System.out.println(id+ ": Request to disembark passenger. (Passenger Total: " + DisPassNum + ")");

        // Bring the flow to ATC's function
        ATC.DisembarkPassenger(this);
    }

    // After plane successfully disembark all passengers to the airport, plane will request to ATC for refueling
    public void RequestRefuel() throws InterruptedException {
        System.out.println(id + ": Request for refueling!");

        // Bring the flow to ATC's function
        ATC.refuelPlane(this);
    }

    // After plane's refueling process is done, plane will request for cleaning and refilling supplies
    public void RequestCleanAndRefill(){
        System.out.println(id+ ": Request for Cleaning and Supplies Refilling services.");

        // Bring the flow to ATC's function
        ATC.CleaningAndRefilling(this);
    }

    // After all passengers successfully board onto the plane, plane will request to ATC for undocking from the gate
    public void requestUndock(){
        System.out.println(id + ": Request to undock from " + assignedGate + ".");

        // Bring the flow to ATC's function
        ATC.ConfirmUndock(this);
    }

    // After plane successfully undock from the gate, plane will request to take off
    public void requestTakeOff() throws InterruptedException {
        System.out.println(id + ": All ground operations had been successfully done. Request to take off!" );

        // Bring the flow to ATC's function
        ATC.Takeoff(this);
    }

    // Start the flow of the plane after the plane''s thread is created and started
    public void run() {
        System.out.println(id + ": Arrived at airport");

        requestLand();

        // Make the plane wait until it is landed
        synchronized (this) {
            while (!landStatus) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // After the plane landed successfully, the rest of the ground operation continues
        try {
            requestCoastAndDock();
            Thread.sleep(2000);
            requestDisembarkPassenger();
            RequestRefuel();
            RequestCleanAndRefill();
            Thread.sleep(2000);
            ATC.EmbarkPassenger(this);
            Thread.sleep(1000);
            requestUndock();
            Thread.sleep(1000);
            requestTakeOff();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
