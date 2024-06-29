package de.db2.wardmanagement;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.data.Personmanagement;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.data.Wardmanagementservice;
import java.util.List;
import java.util.Optional;

public class Main {
    private Wardmanagementservice service;

    public static void main(){
    	Main main = new Main();
    	main.run();
    }
    
   public void run() {
	   setUp();
	   testInstanceNotNull(); 
	   testGetWard();
	   testGetWardById();
	   testGetRooms();
	   testGetRoomById();
	   testGetBeds();
	   testGetBedById();
	   
   }
    
    public void setUp() {
        service = new Wardmanagementservice(Repository.loadInstance(), Personmanagement.loadInstance());
    }

    public void testInstanceNotNull() {
        if (service == null) {
            throw new AssertionError("Instance should not be null");
        }
    }

    public void testProcessWardCommand() throws Exception {
        Ward.Command cmd = new Ward.Command();
        // Set required fields for cmd
        Ward result = service.process(cmd);
        if (result != null) {
            throw new AssertionError("Result should be null");
        }
    }

    public void testGetWard() {
        List<Ward> result = service.getWard(new Ward.Filter());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public void testGetWardById() {
        Optional<Ward> result = service.getWard(new Id<Ward>());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public void testProcessRoomCommand() throws Exception {
        Room.Command cmd = new Room.Command();
        // Set required fields for cmd
        Room result = service.process(cmd);
        if (result != null) {
            throw new AssertionError("Result should be null");
        }
    }

    public void testGetRooms() {
        List<Room> result = service.getRooms(new Room.Filter());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public void testGetRoomById() {
        Optional<Room> result = service.getRoom(new Id<Room>());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public void testProcessBedCommand() throws Exception {
        Bed.Command cmd = new Bed.Command();
        // Set required fields for cmd
        Bed result = service.process(cmd);
        if (result != null) {
            throw new AssertionError("Result should be null");
        }
    }

    public void testGetBeds() {
        List<Bed> result = service.getBeds(new Bed.Filter());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public void testGetBedById() {
        Optional<Bed> result = service.getBed(new Id<Bed>());
        if (result == null) {
            throw new AssertionError("Result should not be null");
        }
    }

    public static void main(String[] args) {
        WardmanagementserviceTest test = new WardmanagementserviceTest();
        test.setUp();

        // Test instance not null
        try {
            test.testInstanceNotNull();
            System.out.println("testInstanceNotNull passed");
        } catch (AssertionError e) {
            System.err.println("testInstanceNotNull failed: " + e.getMessage());
        }

        // Test process ward command
        try {
            test.testProcessWardCommand();
            System.out.println("testProcessWardCommand passed");
        } catch (Exception e) {
            System.err.println("testProcessWardCommand failed: " + e.getMessage());
        } catch (AssertionError e) {
            System.err.println("testProcessWardCommand failed: " + e.getMessage());
        }

        // Test get ward
        try {
            test.testGetWard();
            System.out.println("testGetWard passed");
        } catch (AssertionError e) {
            System.err.println("testGetWard failed: " + e.getMessage());
        }

        // Test get ward by id
        try {
            test.testGetWardById();
            System.out.println("testGetWardById passed");
        } catch (AssertionError e) {
            System.err.println("testGetWardById failed: " + e.getMessage());
        }

        // Test process room command
        try {
            test.testProcessRoomCommand();
            System.out.println("testProcessRoomCommand passed");
        } catch (Exception e) {
            System.err.println("testProcessRoomCommand failed: " + e.getMessage());
        } catch (AssertionError e) {
            System.err.println("testProcessRoomCommand failed: " + e.getMessage());
        }

        // Test get rooms
        try {
            test.testGetRooms();
            System.out.println("testGetRooms passed");
        } catch (AssertionError e) {
            System.err.println("testGetRooms failed: " + e.getMessage());
        }

        // Test get room by id
        try {
            test.testGetRoomById();
            System.out.println("testGetRoomById passed");
        } catch (AssertionError e) {
            System.err.println("testGetRoomById failed: " + e.getMessage());
        }

        // Test process bed command
        try {
            test.testProcessBedCommand();
            System.out.println("testProcessBedCommand passed");
        } catch (Exception e) {
            System.err.println("testProcessBedCommand failed: " + e.getMessage());
        } catch (AssertionError e) {
            System.err.println("testProcessBedCommand failed: " + e.getMessage());
        }

        // Test get beds
        try {
            test.testGetBeds();
            System.out.println("testGetBeds passed");
        } catch (AssertionError e) {
            System.err.println("testGetBeds failed: " + e.getMessage());
        }

        // Test get bed by id
        try {
            test.testGetBedById();
            System.out.println("testGetBedById passed");
        } catch (AssertionError e) {
            System.err.println("testGetBedById failed: " + e.getMessage());
        }
    }
}
