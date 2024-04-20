import building.BuildingReport;
import elevator.ElevatorReport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import scanerzus.Request;

/**
 * The SwingBuildingView class is the view for the building elevator system.
 */
public class SwingBuildingView extends JFrame implements BuildingViewInterface {

  private int numFloors;
  private int numElevators;
  private int elevatorCapacity;
  private JTextField numFloorsInput;
  private JTextField numElevatorsInput;
  private JTextField capacityInput;
  private JLabel statusLabel;
  private JButton resetButton;
  private JPanel leftStatusPanel;
  private JPanel rightStatusPanel;
  private JButton[][] elevatorButtons;
  private List<JLabel> elevatorReports;
  private JLabel upRequestsLabel;
  private JLabel downRequestsLabel;
  private JButton startButton;
  private JButton stopButton;
  private JButton stepButton;
  private JButton addRequestButton;
  private JTextField startFloorInput;
  private JTextField endFloorInput;
  private JLabel promptLabel;

  /**
   * Construct a SwingBuildingView with the given building elevator system name, number of floors,
   * number of elevators, and elevator capacity.
   *
   * @param buildingElevatorSystem the name of the building elevator system
   * @param numFloors              the number of floors in the building
   * @param numElevators           the number of elevators in the building
   * @param elevatorCapacity       the capacity of each elevator
   */
  public SwingBuildingView(String buildingElevatorSystem, int numFloors, int numElevators,
      int elevatorCapacity) {
    super(buildingElevatorSystem);
    this.numFloors = numFloors;
    this.numElevators = numElevators;
    this.elevatorCapacity = elevatorCapacity;
    setupFrame();
    setupPanels();
    addInputFieldListeners();
  }

  /**
   * Initialize the frame for the building elevator system.
   */
  private void setupFrame() {
    // set the frame to be fullscreen
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
  }

  /**
   * Initialize the panels for the building elevator system.
   */
  private void setupPanels() {
    initializeBuildingInfoPanel();  // Panel 1: Building Info
    initializeElevatorStatusPanel();  // Panel 2: Elevator Status
    initializeInspectPanel();  // Panel 3: Requests Display
    setVisible(true);
  }

  /**
   * Initialize the building info panel.
   */
  private void initializeBuildingInfoPanel() {
    // Panel 1: Building Info
    JPanel buildingInfoPanel = new JPanel();
    buildingInfoPanel.setLayout(new BoxLayout(buildingInfoPanel, BoxLayout.Y_AXIS));
    buildingInfoPanel.setBorder(new javax.swing.border.LineBorder(Color.BLACK));

    // Initialize relevant labels
    numFloorsInput = new JTextField(5);
    numElevatorsInput = new JTextField(5);
    capacityInput = new JTextField(5);
    statusLabel = new JLabel();
    resetButton = new JButton("Reset");

    // Create first row panel
    JPanel buildingInfoInputPanel = new JPanel();
    buildingInfoInputPanel.setLayout(new BoxLayout(buildingInfoInputPanel, BoxLayout.X_AXIS));
    buildingInfoInputPanel.add(Box.createHorizontalGlue()); // Add glue before components
    buildingInfoInputPanel.add(new JLabel("Number of Floors: "));
    buildingInfoInputPanel.add(numFloorsInput);
    buildingInfoInputPanel.add(Box.createHorizontalStrut(30)); // Add space
    buildingInfoInputPanel.add(new JLabel("Number of Elevators: "));
    buildingInfoInputPanel.add(numElevatorsInput);
    buildingInfoInputPanel.add(Box.createHorizontalStrut(30)); // Add space
    buildingInfoInputPanel.add(new JLabel("Elevator Capacity: "));
    buildingInfoInputPanel.add(capacityInput);
    buildingInfoInputPanel.add(Box.createHorizontalStrut(30)); // Add space
    buildingInfoInputPanel.add(resetButton);
    buildingInfoInputPanel.add(Box.createHorizontalGlue()); // Add glue after components

    // Create second row panel
    JPanel buildingStatusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buildingStatusPanel.add(statusLabel);

    // Add rows to buildingInfoPanel
    buildingInfoPanel.add(buildingInfoInputPanel, BorderLayout.NORTH);
    buildingInfoPanel.add(buildingStatusPanel, BorderLayout.SOUTH);

    // Add buildingInfoPanel to the JFrame
    add(buildingInfoPanel, BorderLayout.NORTH);
  }

  /**
   * Initialize the elevator status panel.
   */
  private void initializeElevatorStatusPanel() {
    // Panel 2: Elevator Status
    // Left panel for elevator grid
    leftStatusPanel = new JPanel();
    initializeElevatorsGrid();

    // Right panel for elevator details
    rightStatusPanel = new JPanel(new GridLayout(0, 1));
    initializeElevatorReports(numElevators);

    // Combine left and right panels into a JSplitPane
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftStatusPanel,
        rightStatusPanel);
    splitPane.setResizeWeight(0.7); // This sets the split at 70%
    splitPane.setDividerSize(1); // You can set the size of the divider if needed
    splitPane.setBorder(null); // Remove the border if undesired
    JPanel elevatorStatusPanel = new JPanel(new BorderLayout());
    elevatorStatusPanel.add(splitPane, BorderLayout.CENTER);

    // Add the JSplitPane to the main JFrame
    add(elevatorStatusPanel, BorderLayout.CENTER);
  }

  /**
   * Initialize the elevator grid.
   */
  private void initializeElevatorsGrid() {
    // Initialize the elevator grid
    leftStatusPanel.setLayout(new GridLayout(numFloors, numElevators));
    // Create a 2D array of JButtons to represent the elevator grid
    elevatorButtons = new JButton[numFloors][numElevators];

    // Create the buttons and add them to the panel
    for (int floor = 0; floor < numFloors; floor++) {
      for (int elevator = 0; elevator < numElevators; elevator++) {
        JButton elevatorButton = new JButton();
        elevatorButton.setOpaque(true);
        elevatorButton.setContentAreaFilled(true);

        // Set the background color of the button based on the floor
        if (floor == numFloors - 1) {
          elevatorButton.setBackground(Color.darkGray);
        } else {
          elevatorButton.setBackground(Color.WHITE);
        }
        elevatorButton.setFocusable(false);

        // Add the button to the panel and the 2D array
        leftStatusPanel.add(elevatorButton);
        // Add the button to the 2D array
        elevatorButtons[floor][elevator] = elevatorButton;
      }
    }
  }

  /**
   * Initialize the elevator reports.
   *
   * @param numElevators the number of elevators in the building
   */
  private void initializeElevatorReports(int numElevators) {
    rightStatusPanel.setLayout(new GridLayout(numElevators, 1));
    elevatorReports = new ArrayList<>();
    for (int i = 0; i < numElevators; i++) {
      JLabel elevatorReportLabel = new JLabel("Elevator " + (i + 1) + ": ");
      elevatorReportLabel.setBorder(new javax.swing.border.LineBorder(Color.WHITE));
      elevatorReports.add(elevatorReportLabel);
      rightStatusPanel.add(elevatorReportLabel);
    }
  }

  /**
   * Initialize the inspect panel.
   */
  private void initializeInspectPanel() {
    // Panel 3: Requests Display
    JPanel requestsPanel = new JPanel(new GridLayout(2, 1));
    requestsPanel.setBorder(new javax.swing.border.LineBorder(Color.BLACK));

    // relevant labels for requests display
    upRequestsLabel = new JLabel("Up Requests: 0");
    downRequestsLabel = new JLabel("Down Requests: 0");
    requestsPanel.add(upRequestsLabel);
    requestsPanel.add(downRequestsLabel);

    // Panel 4: Controls
    // relevant buttons for controls
    startButton = new JButton("Start");
    stopButton = new JButton("Stop");
    stepButton = new JButton("Step");
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(startButton);
    buttonPanel.add(stopButton);
    buttonPanel.add(stepButton);

    // Panel 5: request input
    // relevant text fields and labels for request input
    startFloorInput = new JTextField(5);
    endFloorInput = new JTextField(5);
    addRequestButton = new JButton("Add Request");
    addRequestButton.setEnabled(false);
    promptLabel = new JLabel("");
    JPanel requestInputPanel = new JPanel();
    requestInputPanel.add(new JLabel("Start Floor:"));
    requestInputPanel.add(startFloorInput);
    requestInputPanel.add(new JLabel("End Floor:"));
    requestInputPanel.add(endFloorInput);
    requestInputPanel.add(addRequestButton);
    requestInputPanel.add(promptLabel);

    // Add the button panel and request input panel to the control panel
    JPanel controlPanel = new JPanel(new GridLayout(2, 1));
    controlPanel.add(buttonPanel);
    controlPanel.add(requestInputPanel);

    // Create a new panel to hold both the requests and control panels
    JPanel inspectPanel = new JPanel(new BorderLayout());
    inspectPanel.add(requestsPanel, BorderLayout.NORTH);
    inspectPanel.add(controlPanel, BorderLayout.SOUTH);
    add(inspectPanel, BorderLayout.SOUTH);
  }

  /**
   * Add listeners to the input fields.
   */
  private void addInputFieldListeners() {
    DocumentListener documentListener = new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        validateRequestInputs();
        validateResetInputs();
      }

      public void removeUpdate(DocumentEvent e) {
        validateRequestInputs();
        validateResetInputs();
      }

      public void insertUpdate(DocumentEvent e) {
        validateRequestInputs();
        validateResetInputs();
      }
    };

    numFloorsInput.getDocument().addDocumentListener(documentListener);
    numElevatorsInput.getDocument().addDocumentListener(documentListener);
    capacityInput.getDocument().addDocumentListener(documentListener);
    startFloorInput.getDocument().addDocumentListener(documentListener);
    endFloorInput.getDocument().addDocumentListener(documentListener);
  }

  /**
   * Validate the request input fields.
   */
  private void validateRequestInputs() {
    try {
      int startFloor = Integer.parseInt(startFloorInput.getText());
      int endFloor = Integer.parseInt(endFloorInput.getText());

      // Enable addRequest button only if all inputs are valid
      addRequestButton.setEnabled(startFloor >= 0 && startFloor < numFloors
          && endFloor >= 0 && endFloor < numFloors);
    } catch (NumberFormatException e) {
      // If any input is not a valid integer, disable the addRequest button
      addRequestButton.setEnabled(false);
    }
  }

  /**
   * Validate the reset input fields.
   */
  private void validateResetInputs() {
    try {
      int floors = Integer.parseInt(numFloorsInput.getText());
      int elevators = Integer.parseInt(numElevatorsInput.getText());
      int capacity = Integer.parseInt(capacityInput.getText());

      // Enable reset button only if all inputs are valid
      resetButton.setEnabled(floors > 0 && elevators > 0 && capacity > 0);
    } catch (NumberFormatException e) {
      // If any input is not a valid integer, disable the reset button
      resetButton.setEnabled(false);
    }
  }

  @Override
  public void updateBuildingInfo(int numFloors, int numElevators, int capacity,
      String status) {
    numFloorsInput.setText(numFloors + "");
    numElevatorsInput.setText(numElevators + "");
    capacityInput.setText(capacity + "");
    statusLabel.setText("Elevator System Status: " + status);
  }

  @Override
  public void updateElevatorGrid(BuildingReport buildingReport, int numFloors, int numElevators) {
    for (int floor = 0; floor < numFloors; floor++) {
      for (int elevator = 0; elevator < numElevators; elevator++) {
        ElevatorReport report = buildingReport.getElevatorReports()[elevator];
        JButton button = elevatorButtons[floor][elevator];
        if (floor == numFloors - report.getCurrentFloor() - 1) {
          button.setBackground(Color.DARK_GRAY);  // Elevator is at this floor
        } else {
          button.setBackground(Color.WHITE);
        }
      }
    }
  }

  @Override
  public void updateElevatorReports(ElevatorReport[] reports) {
    for (int i = 0; i < reports.length; i++) {
      JLabel reportLabel = elevatorReports.get(i);
      reportLabel.setText("Elevator " + (i + 1) + ": " + reports[i].toString());
    }
  }

  @Override
  public void updateRequestSizeDisplay(List<Request> upRequests, List<Request> downRequests) {
    upRequestsLabel.setText("Up Requests: " + upRequests.size());
    downRequestsLabel.setText("Down Requests: " + downRequests.size());
  }

  @Override
  public void updatePromptLabel(String message) {
    promptLabel.setText(message);
    promptLabel.revalidate();
    promptLabel.repaint();
  }

  @Override
  public String getStartFloorInput() {
    return startFloorInput.getText();
  }

  @Override
  public String getEndFloorInput() {
    return endFloorInput.getText();
  }

  @Override
  public void clearRequestFields() {
    startFloorInput.setText("");
    endFloorInput.setText("");
  }

  @Override
  public int getNumFloorsInput() {
    return Integer.parseInt(numFloorsInput.getText());
  }

  @Override
  public int getNumElevatorsInput() {
    return Integer.parseInt(numElevatorsInput.getText());
  }

  @Override
  public int getCapacityInput() {
    return Integer.parseInt(capacityInput.getText());
  }

  @Override
  public void startNewSimulation() {
    numFloors = getNumFloorsInput();
    numElevators = getNumElevatorsInput();
    elevatorCapacity = getCapacityInput();

    numFloorsInput.setText(String.valueOf(numFloors));
    numElevatorsInput.setText(String.valueOf(numElevators));
    capacityInput.setText(String.valueOf(elevatorCapacity));
    statusLabel.setText("Elevator System Status: Out of Service");
    promptLabel.setText("");

    resetElevatorGrid();
    resetElevatorReports();
  }

  /**
   * Reset the elevator grid.
   */
  private void resetElevatorGrid() {
    leftStatusPanel.removeAll();
    initializeElevatorsGrid();
  }

  /**
   * Reset the elevator reports.
   */
  private void resetElevatorReports() {
    rightStatusPanel.removeAll();
    initializeElevatorReports(numElevators);
  }

  @Override
  public void addActionListener(ActionListener listener) {
    startButton.setActionCommand("Start");
    stopButton.setActionCommand("Stop");
    stepButton.setActionCommand("Step");
    addRequestButton.setActionCommand("Request");
    resetButton.setActionCommand("Reset");

    startButton.addActionListener(listener);
    stopButton.addActionListener(listener);
    stepButton.addActionListener(listener);
    addRequestButton.addActionListener(listener);
    resetButton.addActionListener(listener);
  }
}

