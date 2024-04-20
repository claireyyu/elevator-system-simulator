# CS 5004 Object-Oriented Design - Final Project

The goal of this project is to design and implement a simulation of an elevator system using object-oriented principles. The program models the behavior of multiple elevators within a building, handling requests from users and providing contol options to start, step and stop the building.

## Overview
- Building Information: Displays the number of floors and elevators in the building.
- Multi-Elevator Support: Simulates multiple elevators operating independently within a building.
- Simulation Controls: Start, stop, and step through the simulation using control buttons.
- User Requests: Users can input requests for elevators from any floor to any other floor.
- Graphical User Interface: Provides a real-time graphical display of elevator and building status using Java Swing.
- Error Handling: Robust error checking and handling for invalid input.

## How To Run

- To run the jar file, use the following command in a terminal or command prompt:
```
java -jar BuildingElevatorSystem.jar
```
- There are no additional arguments needed to run the jar file.

## How to Use the Program

1. Starting the Simulation: Click the 'Start' button to initiate the elevator simulation.
2. Requesting an Elevator: Enter the start and destination floors in the designated text fields and press 'Add Request'.
3. Pausing the Simulation: Use the 'Stop' button to pause the simulation at any time.
4. Stepping Through the Simulation: The 'Step' button allows users to advance the simulation one step at a time for detailed analysis.

## Design

Initial designs utilized a simpler, less interactive model without a GUI. The new Java Swing interface allowed real-time interaction and better visualization of the elevator system, which significantly improved usability.

## Assumptions

- The building has a minimum of 1 floor, 1 elevator and 1 user.
- The building's up and down requests must be within the bounds of the building's floors.
- Reset and addRequest buttons are disabled if the input values are invalid.
- When the building is stopped or stopping, a warning will show that requests cannot be added.

## Limitations

- The current optimization algorithm does not fully minimize waiting time under high traffic conditions.
- The GUI does not dynamically scale with an extremely large number of floors or elevators.

## Citations

- Java Swing Documentation: https://docs.oracle.com/javase/8/docs/api/javax/swing/package-summary.html


