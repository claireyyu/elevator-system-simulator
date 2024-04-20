package building;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import building.enums.ElevatorSystemStatus;
import org.junit.Test;
import scanerzus.Request;

/**
 * A JUnit test class for the Building class.
 */
public class BuildingTest {

  @Test
  public void testConstructor() {
    Building building = new Building(4, 1, 3);
    assertEquals(4, building.getNumFloors());
    assertEquals(1, building.getNumElevators());
    assertEquals(3, building.getElevatorCapacity());
    assertEquals(ElevatorSystemStatus.outOfService,
        building.getElevatorSystemStatus().getSystemStatus());
    assertEquals(0, building.getElevatorSystemStatus().getUpRequests().size());
    assertEquals(0, building.getElevatorSystemStatus().getDownRequests().size());
    assertEquals(1, building.getElevatorSystemStatus().getElevatorReports().length);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorInvalidFloors() {
    new Building(0, 1, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorInvalidElevators() {
    new Building(4, 0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorInvalidCapacity() {
    new Building(4, 1, 0);
  }

  @Test
  public void startElevatorSystem() {
    Building building = new Building(4, 1, 3);
    assertTrue(building.startElevatorSystem());
    assertEquals(ElevatorSystemStatus.running,
        building.getElevatorSystemStatus().getSystemStatus());
  }

  @Test(expected = IllegalStateException.class)
  public void startElevatorSystemAlreadyRunning() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.startElevatorSystem();
  }

  @Test(expected = IllegalStateException.class)
  public void startElevatorSystemStillStopping() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.stopElevatorSystem();
    building.startElevatorSystem();
  }

  @Test
  public void addRequestWhenRunning() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    assertTrue(building.addRequest(new Request(1, 2)));
    assertTrue(building.addRequest(new Request(2, 1)));
    assertEquals(1, building.getElevatorSystemStatus().getUpRequests().size());
    assertEquals(1, building.getElevatorSystemStatus().getDownRequests().size());
  }

  @Test
  public void addRequestNotRunning() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.stopElevatorSystem();
    assertFalse(building.addRequest(new Request(1, 2)));
  }

  @Test(expected = IllegalStateException.class)
  public void stepElevatorsWhenOutOfService() {
    Building building = new Building(4, 1, 3);
    building.stepElevatorSystem();
  }

  @Test
  public void stepElevatorsWhenRunningUpRequests() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.addRequest(new Request(1, 2));
    building.stepElevatorSystem();  // immediately go to floor 1
    assertEquals(1, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    building.stepElevatorSystem();  // stop at floor 1 & open door
    building.stepElevatorSystem();  // wait steps 1/3
    building.stepElevatorSystem();  // wait steps 2/3
    building.stepElevatorSystem();  // wait steps 3/3
    building.stepElevatorSystem();  // immediately go to floor 2
    assertEquals(2, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
  }

  @Test
  public void stepElevatorsWhenRunningDownRequests() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.addRequest(new Request(2, 1));
    building.addRequest(new Request(3, 2));
    building.addRequest(new Request(3, 1));
    building.addRequest(new Request(2, 1));
    assertEquals(4, building.getElevatorSystemStatus().getDownRequests().size());
    building.stepElevatorSystem();
    building.stepElevatorSystem();
    building.stepElevatorSystem();
    building.stepElevatorSystem();
    building.stepElevatorSystem();  // waiting time's up (5/5)
    building.stepElevatorSystem();  // floor 1
    building.stepElevatorSystem();  // floor 2
    building.stepElevatorSystem();  // floor 3
    assertEquals(3, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    building.stepElevatorSystem();  // stops at floor 3 & open door
    building.stepElevatorSystem();  // top floor & downRequests not empty -> distribute requests
    assertEquals(3, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    assertEquals(1, building.getElevatorSystemStatus().getDownRequests().size());
  }

  @Test
  public void stepElevatorsWhenStopping() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.addRequest(new Request(1, 2));
    building.stepElevatorSystem();  // immediately go to floor 1
    building.stepElevatorSystem();  // open door
    building.stepElevatorSystem();  // wait 1/3
    building.stepElevatorSystem();  // wait 2/3
    building.stepElevatorSystem();  // wait 3/3
    building.stepElevatorSystem();  // immediately go to floor 2
    building.stopElevatorSystem();
    assertEquals(ElevatorSystemStatus.stopping,
        building.getElevatorSystemStatus().getSystemStatus());
    assertEquals(2, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    building.stepElevatorSystem();  // immediately go to floor 1
    assertEquals(1, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    building.stepElevatorSystem();  // stops at floor 1 & open door
    assertEquals(0, building.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    assertEquals(ElevatorSystemStatus.outOfService,
        building.getElevatorSystemStatus().getSystemStatus());
    assertFalse(building.getElevatorSystemStatus().getElevatorReports()[0].isDoorClosed());
  }

  @Test
  public void takeElevatorsOutOfService() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.addRequest(new Request(1, 2));
    building.stepElevatorSystem();  // immediately go to floor 1
    building.stepElevatorSystem();  // open door
    building.stepElevatorSystem();  // wait 1/3
    building.stepElevatorSystem();  // wait 2/3
    building.stepElevatorSystem();  // wait 3/3
    building.stepElevatorSystem();  // immediately go to floor 2
    assertFalse(building.getElevatorSystemStatus().getElevatorReports()[0].isOutOfService());

    building.stopElevatorSystem();  // take elevator out of service
    assertTrue(building.getElevatorSystemStatus().getElevatorReports()[0].isOutOfService());
  }

  @Test
  public void getNumFloors() {
    Building building = new Building(4, 1, 3);
    assertEquals(4, building.getNumFloors());
  }

  @Test
  public void getNumElevators() {
    Building building = new Building(4, 1, 3);
    assertEquals(1, building.getNumElevators());
  }

  @Test
  public void getElevatorCapacity() {
    Building building = new Building(4, 1, 3);
    assertEquals(3, building.getElevatorCapacity());
  }

  @Test
  public void getElevatorSystemStatus() {
    Building building = new Building(4, 1, 3);
    assertEquals(ElevatorSystemStatus.outOfService,
        building.getElevatorSystemStatus().getSystemStatus());  // before start is out of service
    building.startElevatorSystem();
    assertEquals(ElevatorSystemStatus.running,
        building.getElevatorSystemStatus().getSystemStatus()); // after start is running
    building.addRequest(new Request(0, 1));
    building.stepElevatorSystem();
    building.stopElevatorSystem();
    assertEquals(ElevatorSystemStatus.stopping,
        building.getElevatorSystemStatus().getSystemStatus());  // after stop is stopping
  }

  @Test(expected = IllegalStateException.class)
  public void stopElevatorSystemWhenOutOfService() {
    Building building = new Building(4, 1, 3);
    building.stopElevatorSystem();
  }

  @Test(expected = IllegalStateException.class)
  public void stopElevatorSystemWhenStopping() {
    Building building = new Building(4, 1, 3);
    building.startElevatorSystem();
    building.addRequest(new Request(1, 2));
    building.stepElevatorSystem();
    building.stopElevatorSystem();
    building.stopElevatorSystem();
  }
}