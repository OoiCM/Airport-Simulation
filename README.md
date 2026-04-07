# ✈️ Airport Simulation System (Java Concurrency)

This project is a **Java-based concurrent simulation** of an airport system, demonstrating how multiple planes are managed using **threads, semaphores, and synchronization**.

The system simulates real-world airport operations such as landing, docking, servicing, and takeoff under resource constraints.

---

## 📂 Project Description

This simulation models operations at a **resource-limited airport** with:

* 🛬 1 runway
* 🛫 Maximum 3 planes in the airport at a time
* 🏢 Multiple gates
* ⛽ 1 refuelling truck

The system handles:

* Plane arrivals and landing queues
* Passenger boarding and disembarking
* Refuelling and cleaning services
* Takeoff scheduling

It also includes an **emergency landing scenario** where priority is given to critical planes. 

---

## ⚙️ Key Features

* 🧵 Multi-threaded simulation using Java Threads
* 🚦 Semaphore control for shared resources:

  * Runway (1)
  * Gates (3)
  * Refuelling truck (1)
* 🚨 Emergency landing priority (queue jump)
* 📊 Statistics tracking:

  * Waiting time
  * Planes served
  * Passengers processed
* 🔄 Full lifecycle simulation of a plane

---

## 🧠 System Workflow

Each plane goes through the following lifecycle:

1. Request Landing
2. Land (runway access controlled)
3. Coast & Dock to gate
4. Disembark passengers
5. Refuel
6. Cleaning & refill supplies
7. Embark passengers
8. Undock
9. Take off

All steps are handled concurrently with proper synchronization.

---

## 🏗️ Classes Overview

### Core Classes

* **AirportSimulator**
  Initializes the system and starts the simulation

* **ATC (Air Traffic Control)**
  Manages:

  * Landing queue
  * Resource allocation
  * Coordination between planes

* **Plane (Thread)**
  Represents each aircraft and controls its lifecycle

---

### Supporting Classes

* **Gate** → Manages gate availability
* **Passenger** → Simulates boarding & disembarking
* **RefuellingTruck** → Controls refuelling process (mutual exclusion)
* **Worker** → Handles cleaning & supply refilling
* **Statistics** → Tracks performance metrics
* **Action (enum)** → Defines task types (cleaning, refilling, etc.)

---

## 🔐 Concurrency Concepts Used

* **Threads** → Each plane, passenger, and worker runs independently
* **Semaphore** → Controls limited resources
* **Synchronized blocks** → Prevent race conditions
* **wait() / notify()** → Manage landing queue coordination
* **AtomicInteger** → Thread-safe counters

---

## 📊 Simulation Scenario

* Total planes: **6**
* One plane is marked as **emergency (fuel shortage)**
* Emergency plane:

  * Skips queue
  * Lands first when possible

This demonstrates **priority handling in concurrent systems**.

---

## 📈 Statistics Output

At the end of the simulation, the system prints:

* Total planes served
* Total passengers boarded
* Minimum waiting time
* Maximum waiting time
* Average waiting time

All values are calculated using thread-safe operations.

---

## 🚀 How to Run

1. Make sure you have Java installed

2. Compile the program:

```bash
javac AirportSimulator.java
```

3. Run the simulation:

```bash
java AirportSimulator
```

---

## 📝 Notes

* Simulation uses **random passenger counts** per plane
* Each passenger action is simulated with delays
* Refuelling is strictly **one plane at a time**
* Cleaning and refilling run **concurrently**
* System ensures no deadlocks and proper synchronization

---

## 📚 Learning Outcomes

This project demonstrates:

* Java concurrency (threads, synchronization)
* Resource management using semaphores
* Producer-consumer style coordination
* Real-world system simulation
* Performance tracking in concurrent systems

---

## 💡 Highlights

* Realistic airport workflow simulation
* Emergency priority handling
* Clean separation of responsibilities across classes
* Strong use of concurrency concepts in Java
