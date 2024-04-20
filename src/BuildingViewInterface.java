import building.BuildingReport;
import elevator.ElevatorReport;
import java.awt.event.ActionListener;
import java.util.List;
import scanerzus.Request;

/**
 * This interface is used to represent a building view. A building view can update the
 * building info, elevator grid, elevator reports, and request size display.
 * It can also update the request status
 * label and get the start floor, end floor, number of floors, number of elevators, and elevator
 * capacity input.
 */
public interface BuildingViewInterface {
  /**
   * Update the building info display with the given number of floors,
   * number of elevators, elevator capacity, and elevator system status.
   *
   * @param numFloors        the number of floors in the building.
   * @param numElevators     the number of elevators in the building.
   * @param elevatorCapacity the capacity of the elevators in the building.
   * @param string           the elevator system status.
   */
  void updateBuildingInfo(int numFloors, int numElevators, int elevatorCapacity, String string);

  /**
   * Update the elevator grid display with the given building report, number of floors,
   * and number of elevators.
   *
   * @param elevatorSystemStatus the building report that contains the elevator system status.
   * @param numFloors            the number of floors in the building.
   * @param numElevators         the number of elevators in the building.
   */
  void updateElevatorGrid(BuildingReport elevatorSystemStatus, int numFloors, int numElevators);

  /**
   * Update the elevator reports display with the given elevator reports.
   *
   * @param elevatorReports the elevator reports.
   */
  void updateElevatorReports(ElevatorReport[] elevatorReports);

  /**
   * Update the request size display with the given up requests and down requests.
   *
   * @param upRequests   the up requests.
   * @param downRequests the down requests.
   */
  void updateRequestSizeDisplay(List<Request> upRequests, List<Request> downRequests);

  /**
   * Update the prompt message label with the message.
   *
   * @param prompt the prompt message.
   */
  void updatePromptLabel(String prompt);

  /**
   * Get the start floor input.
   *
   * @return the start floor input.
   */
  String getStartFloorInput();

  /**
   * Get the end floor input.
   *
   * @return the end floor input.
   */
  String getEndFloorInput();

  /**
   * Clear the request fields.
   */
  void clearRequestFields();

  /**
   * Get the number of floors input.
   *
   * @return the number of floors input.
   */
  int getNumFloorsInput();

  /**
   * Get the number of elevators input.
   *
   * @return the number of elevators input.
   */
  int getNumElevatorsInput();

  /**
   * Get the elevator capacity input.
   *
   * @return the elevator capacity input.
   */
  int getCapacityInput();

  /**
   * Start a new simulation.
   */
  void startNewSimulation();

  /**
   * Add an action listener to the view.
   *
   * @param listener the action listener to add.
   */
  void addActionListener(ActionListener listener);
}
