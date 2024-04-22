import building.Building;
import building.BuildingInterface;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import scanerzus.Request;

/**
 * The SwingBuildingController class is the controller for the building elevator system.
 */
public class SwingBuildingController implements BuildingControllerInterface, ActionListener {

  private BuildingInterface model;
  private final BuildingViewInterface view;

  /**
   * Construct a SwingBuildingController with the given model and view.
   *
   * @param model the building model
   * @param view  the building view
   */
  public SwingBuildingController(BuildingInterface model, BuildingViewInterface view) {
    this.model = model;
    this.view = view;
    this.view.addActionListener(this);
  }

  /**
   * Attempt to add a request to the building model.
   */
  private void attemptRequest() {
    // initialize a request
    int startFloor = Integer.parseInt(view.getStartFloorInput());
    int endFloor = Integer.parseInt(view.getEndFloorInput());
    Request request = new Request(startFloor, endFloor);

    // add request to model & update view
    if (model.addRequest(request)) {
      view.updatePromptLabel("Request Added Successfully");
    } else {
      view.updatePromptLabel("Cannot Add Request: The Elevator System stopping or stopped");
    }
    view.clearRequestFields();
  }

  /**
   * Update the model with the current view values.
   */
  private void updateModel() {
    this.model = new Building(view.getNumFloorsInput(), view.getNumElevatorsInput(),
        view.getCapacityInput());
  }

  /**
   * Update the elevator displays with the current model values.
   */
  private void updateElevatorDisplays() {
    // Update building info, grid, reports, and requests
    view.updateBuildingInfo(model.getNumFloors(), model.getNumElevators(),
        model.getElevatorCapacity(), model.getElevatorSystemStatus().getSystemStatus().toString());
    view.updateElevatorGrid(model.getElevatorSystemStatus(), model.getNumFloors(),
        model.getNumElevators());
    view.updateElevatorReports(model.getElevatorSystemStatus().getElevatorReports());
    view.updateRequestSizeDisplay(model.getElevatorSystemStatus().getUpRequests(),
        model.getElevatorSystemStatus().getDownRequests());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    switch (cmd) {
      case "Start":
        try {
          model.startElevatorSystem();
          view.updatePromptLabel("The Elevator System is now started.");
        } catch (IllegalStateException ise) {
          view.updatePromptLabel("Cannot Start: The Elevator System is "
              + "already running or still stopping.");
          return;
        }
        break;
      case "Stop":
        try {
          model.stopElevatorSystem();
          view.updatePromptLabel("Stopping");
        } catch (IllegalStateException ise) {
          view.updatePromptLabel("Cannot Stop: The Elevator System is "
              + "already stopped or out of service.");
          return;
        }
        break;
      case "Step":
        try {
          model.stepElevatorSystem();
          view.updatePromptLabel("Stepping");
        } catch (IllegalStateException ise) {
          view.updatePromptLabel("Cannot Step: The Elevator System is "
              + "out of Service.");
          return;
        }
        break;
      case "Request":
        attemptRequest();
        break;
      case "Reset":
        updateModel();
        view.startNewSimulation();
        break;
      default:
        break;
    }
    updateElevatorDisplays();
  }

  @Override
  public void go() {
    updateElevatorDisplays();
  }
}
