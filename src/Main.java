import building.Building;
import building.BuildingInterface;

/**
 * The Main class is the entry point for the building elevator system.
 */
public class Main {
  /**
   * The main method is the entry point for the building elevator system.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    int numFloors = 8;
    int numElevators = 4;
    int elevatorCapacity = 3;

    BuildingInterface model = new Building(numFloors, numElevators, elevatorCapacity);
    BuildingViewInterface view = new SwingBuildingView(
        "Building Elevator System", numFloors, numElevators, elevatorCapacity);
    BuildingControllerInterface controller = new SwingBuildingController(model, view);
    controller.go();
  }
}
