package building;

import scanerzus.Request;

/**
 * This interface is used to represent a building. A building has a number of floors, elevators, and
 * elevator capacity. It also has a list of up requests and down requests. The building can start
 * and stop the elevator system, add requests, and step the elevators.
 */
public interface BuildingInterface {

  /**
   * Returns the number of floors in the building.
   *
   * @return the number of floors in the building.
   */
  int getNumFloors();

  /**
   * Returns the number of elevators in the building.
   *
   * @return the number of elevators in the building.
   */
  int getNumElevators();

  /**
   * Returns the capacity of the elevators in the building.
   *
   * @return the capacity of the elevators in the building.
   */
  int getElevatorCapacity();

  /**
   * Returns a building report that contains the elevator system status.
   *
   * @return the building report that contains the elevator system status.
   */
  BuildingReport getElevatorSystemStatus();

  /**
   * Starts the elevator system. If the elevator system is still running or stopping, this method
   * throws an IllegalStateException. if the elevator system is out of service, this method starts
   * each elevator in the building. And sets the elevator system status to running.
   *
   * @return true if the elevator system was started, false otherwise.
   * @throws IllegalStateException if the elevator system is still running or stopping.
   */
  boolean startElevatorSystem() throws IllegalStateException;

  /**
   * Adds a request to the building.
   * <p>
   * If the elevator system is running, this method adds the request to the appropriate list. And
   * returns true to indicate that the request was added. If the elevator system is not running,
   * this method does nothing and returns false.
   *
   * @param request the request to add.
   * @return true if the request was added, false otherwise.
   */
  boolean addRequest(Request request);

  /**
   * Steps the elevators in the building. If the elevator system is out of service, this method
   * throws an IllegalStateException. If the elevator system is running, this method distributes the
   * requests to the elevators. And steps each elevator in the building. If the elevator system is
   * stopping, this method also stops each elevator in the building. And sets the elevator system
   * status to out of service if all elevators are stopped.
   *
   * @throws IllegalStateException if the elevator system is out of service.
   */
  void stepElevatorSystem() throws IllegalStateException;

  /**
   * Stops the elevator system.
   * <p>
   * If the elevator system is already stopping or out of service, this method throws an
   * IllegalStateException. If the elevator system is running, this method stops each elevator in
   * the building. It also sets the elevator system status to stopping, and clear all current
   * requests.
   */
  void stopElevatorSystem();
}
