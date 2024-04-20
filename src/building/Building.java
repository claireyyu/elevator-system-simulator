package building;

import building.enums.ElevatorSystemStatus;
import elevator.Elevator;
import elevator.ElevatorReport;
import java.util.ArrayList;
import java.util.List;
import scanerzus.Request;

/**
 * This class represents a building. A building has a number of floors, elevators, and elevator
 * capacity. It also has a list of up requests and down requests. The building can start and stop
 * the elevator system, add requests, and step the elevators.
 */
public class Building implements BuildingInterface {

  private final int numFloors;
  private final int numElevators;
  private final int elevatorCapacity;

  private final Elevator[] elevators;

  private List<Request> upRequests;

  private List<Request> downRequests;

  private ElevatorSystemStatus elevatorSystemStatus;

  /**
   * The constructor for the building.
   *
   * @param numFloors        the number of floors in the building.
   * @param numElevators     the number of elevators in the building.
   * @param elevatorCapacity the capacity of the elevators in the building.
   */
  public Building(int numFloors, int numElevators, int elevatorCapacity)
      throws IllegalArgumentException {

    if (numFloors < 1 || numElevators < 1 || elevatorCapacity < 1) {
      throw new IllegalArgumentException("Invalid input. Please enter a positive integer "
          + "for the number of floors, elevators, and elevator capacity.");
    }

    this.numFloors = numFloors;
    this.numElevators = numElevators;
    this.elevatorCapacity = elevatorCapacity;
    this.elevatorSystemStatus = ElevatorSystemStatus.outOfService;
    this.upRequests = new ArrayList<>();
    this.downRequests = new ArrayList<>();

    // Create the elevators according to the number of elevators in the building
    // and initialize them with the number of floors and elevator capacity.
    this.elevators = new Elevator[numElevators];
    for (int i = 0; i < numElevators; ++i) {
      this.elevators[i] = new Elevator(numFloors, elevatorCapacity);
    }
  }

  @Override
  public int getNumFloors() {
    return numFloors;
  }

  @Override
  public int getNumElevators() {
    return numElevators;
  }

  @Override
  public int getElevatorCapacity() {
    return elevatorCapacity;
  }

  @Override
  public BuildingReport getElevatorSystemStatus() {
    // Create an array of elevator reports to get the building report
    ElevatorReport[] elevatorReports = new ElevatorReport[numElevators];
    for (int i = 0; i < numElevators; ++i) {
      elevatorReports[i] = elevators[i].getElevatorStatus();
    }

    return new BuildingReport(numFloors, numElevators, elevatorCapacity,
        elevatorReports, upRequests, downRequests, elevatorSystemStatus);
  }

  @Override
  public boolean startElevatorSystem() throws IllegalStateException {
    // If the elevator system is still running or stopping, throw an exception
    if (elevatorSystemStatus != ElevatorSystemStatus.outOfService) {
      throw new IllegalStateException("Elevator cannot be started until it is stopped");
    }

    // If the elevator system is out of service, start each elevator in the building
    for (int i = 0; i < numElevators; ++i) {
      elevators[i].start();
    }
    // Set the elevator system status to running
    elevatorSystemStatus = ElevatorSystemStatus.running;
    return true;
  }

  /**
   * Distribute the request to the upRequests or downRequests list. If the request is from a lower
   * floor to a higher floor, add it to the upRequests list. If the request is from a higher floor
   * to a lower floor, add it to the downRequests list.
   *
   * @param request the request to distribute.
   */
  private void distributeRequest(Request request) {
    if (request.getStartFloor() < request.getEndFloor()) {
      upRequests.add(request);
    } else {
      downRequests.add(request);
    }
  }

  @Override
  public boolean addRequest(Request request) {
    if (elevatorSystemStatus == ElevatorSystemStatus.running) {
      distributeRequest(request);
      return true;
    } else {
      return false;  // If the elevator system is not running, do nothing and return false.
    }
  }

  private void distributeRequests() {
    // Check if there are any requests to process.
    if (!upRequests.isEmpty() || !downRequests.isEmpty()) {
      // Iterate through each elevator to distribute requests.
      for (Elevator elevator : elevators) {
        if (elevator.isTakingRequests()) { // Check if the elevator can take requests.

          // Distribute UP requests if the elevator is at the first floor and there are UP requests.
          if (elevator.getCurrentFloor() == 0 && !upRequests.isEmpty()) {
            // Take the minimum of the elevator capacity and the number of UP requests.
            int requestsToTake = Math.min(elevatorCapacity, upRequests.size());
            List<Request> requests = new ArrayList<>(upRequests.subList(0, requestsToTake));
            // Remove the requests that have been taken.
            // ArrayList can automatically resize itself and shift to fill the gaps.
            upRequests.subList(0, requestsToTake).clear();
            // Process the taken requests.
            elevator.processRequests(requests);
          } else if (elevator.getCurrentFloor() == numFloors - 1 && !downRequests.isEmpty()) {
            // Distribute DOWN requests if the elevator is at the top and there are DOWN requests.
            // Take the minimum of the elevator capacity and the number of DOWN requests.
            int requestsToTake = Math.min(elevatorCapacity, downRequests.size());
            List<Request> requests = new ArrayList<>(downRequests.subList(0, requestsToTake));
            // Remove the requests that have been taken.
            downRequests.subList(0, requestsToTake).clear();
            // Process the taken requests.
            elevator.processRequests(requests);
          }

        }
      }
    }
  }

  /**
   * Call step() on each elevator in the building.
   */
  private void stepEachElevator() {
    for (int i = 0; i < numElevators; ++i) {
      elevators[i].step();
    }
  }

  /**
   * Check if all elevators have stopped.
   *
   * @return true if all elevators have stopped (at first floor), false otherwise.
   */
  private boolean allElevatorsStopped() {
    for (int i = 0; i < numElevators; ++i) {
      if (elevators[i].getCurrentFloor() != 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void stepElevatorSystem() throws IllegalStateException {
    // if the elevator system is out of service, throw an exception
    if (elevatorSystemStatus == ElevatorSystemStatus.outOfService) {
      throw new IllegalStateException("Building is out of service. Cannot step the"
          + " elevator system.");
    }

    // If the elevator system is running, distribute requests
    // to the elevators and step each elevator
    if (elevatorSystemStatus == ElevatorSystemStatus.running) {
      distributeRequests();
      stepEachElevator();
    } else {
      // If the elevator system is stopping, also step each elevator to bring them to ground floor
      // and check if all elevators have stopped.
      stepEachElevator();
      // If so, set the elevator system status to out of service and open the doors.
      if (allElevatorsStopped()) {
        elevatorSystemStatus = ElevatorSystemStatus.outOfService;
        // make sure the elevators' door are open
        stepEachElevator();
      }
    }
  }

  @Override
  public void stopElevatorSystem() throws IllegalStateException {
    if (elevatorSystemStatus != ElevatorSystemStatus.running) {
      throw new IllegalStateException("Elevator system is already stopping or out of service.");
    }

    // If the elevator system is running, stop each elevator in the building
    // set the elevator system status to stopping, and clear the requests.
    for (int i = 0; i < numElevators; ++i) {
      elevators[i].takeOutOfService();
    }
    elevatorSystemStatus = ElevatorSystemStatus.stopping;
    upRequests.clear();
    downRequests.clear();
  }
}


