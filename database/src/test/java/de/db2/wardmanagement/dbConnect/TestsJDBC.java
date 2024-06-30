package de.db2.wardmanagement.dbConnect;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.data.Personmanagement;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.data.Wardmanagementservice;

public final class WardmanagementserviceTest {

    private static Repository repo;
    private static Personmanagement personManagement;
    private static Wardmanagementservice service;

    @BeforeClass
    public static void init() {

        System.setProperty("wardmanagement.repo.jdbc.url", "jdbc:postgresql:wardmanagement");
        System.setProperty("wardmanagement.repo.jdbc.user", "username");
        System.setProperty("wardmanagement.repo.jdbc.password", "password");

        repo = Repository.loadInstance();
        personManagement = Personmanagement.loadInstance();
        service = new Wardmanagementservice(repo, personManagement);
    }

    @AfterClass
    public static void cleanUp() {
        // Any cleanup if necessary
    }

    @Test
    public void testWardLifeCycle() throws Exception {
        Ward.Command wardCmd = new Ward.Command();
        wardCmd.setName("Test Ward");
        Ward createdWard = service.process(wardCmd);
        assertNotNull("Created Ward should not be null", createdWard);

        var loadedWard = service.getWard(new Id<>(createdWard.getId()));
        assertTrue("Loaded Ward should be present", loadedWard.isPresent());

        service.deleteWard(new Id<>(createdWard.getId()));
        loadedWard = service.getWard(new Id<>(createdWard.getId()));
        assertFalse("Loaded Ward should not be present after deletion", loadedWard.isPresent());
    }

    @Test
    public void testRoomLifeCycle() throws Exception {
        Ward.Command wardCmd = new Ward.Command();
        wardCmd.setName("Test Ward");
        Ward createdWard = service.process(wardCmd);
        assertNotNull("Created Ward should not be null", createdWard);

        Room.Command roomCmd = new Room.Command();
        roomCmd.setWardId(new Id<>(createdWard.getId()));
        roomCmd.setName("Test Room");
        Room createdRoom = service.process(roomCmd);
        assertNotNull("Created Room should not be null", createdRoom);

        var loadedRoom = service.getRoom(new Id<>(createdRoom.getId()));
        assertTrue("Loaded Room should be present", loadedRoom.isPresent());

        service.deleteRoom(new Id<>(createdRoom.getId()));
        loadedRoom = service.getRoom(new Id<>(createdRoom.getId()));
        assertFalse("Loaded Room should not be present after deletion", loadedRoom.isPresent());

        service.deleteWard(new Id<>(createdWard.getId()));
    }

    @Test
    public void testBedLifeCycle() throws Exception {
        Ward.Command wardCmd = new Ward.Command();
        wardCmd.setName("Test Ward");
        Ward createdWard = service.process(wardCmd);
        assertNotNull("Created Ward should not be null", createdWard);

        Room.Command roomCmd = new Room.Command();
        roomCmd.setWardId(new Id<>(createdWard.getId()));
        roomCmd.setName("Test Room");
        Room createdRoom = service.process(roomCmd);
        assertNotNull("Created Room should not be null", createdRoom);

        Bed.Command bedCmd = new Bed.Command();
        bedCmd.setRoomId(new Id<>(createdRoom.getId()));
        bedCmd.setName("Test Bed");
        Bed createdBed = service.process(bedCmd);
        assertNotNull("Created Bed should not be null", createdBed);

        var loadedBed = service.getBed(new Id<>(createdBed.getId()));
        assertTrue("Loaded Bed should be present", loadedBed.isPresent());

        service.deleteBed(new Id<>(createdBed.getId()));
        loadedBed = service.getBed(new Id<>(createdBed.getId()));
        assertFalse("Loaded Bed should not be present after deletion", loadedBed.isPresent());

        service.deleteRoom(new Id<>(createdRoom.getId()));
        service.deleteWard(new Id<>(createdWard.getId()));
    }

    public static void main(String[] args) {
        WardmanagementserviceTest test = new WardmanagementserviceTest();
        init();

        // Test Ward lifecycle
        try {
            test.testWardLifeCycle();
            System.out.println("testWardLifeCycle passed");
        } catch (Exception e) {
            System.err.println("testWardLifeCycle failed: " + e.getMessage());
        }

        // Test Room lifecycle
        try {
            test.testRoomLifeCycle();
            System.out.println("testRoomLifeCycle passed");
        } catch (Exception e) {
            System.err.println("testRoomLifeCycle failed: " + e.getMessage());
        }

        // Test Bed lifecycle
        try {
            test.testBedLifeCycle();
            System.out.println("testBedLifeCycle passed");
        } catch (Exception e) {
            System.err.println("testBedLifeCycle failed: " + e.getMessage());
        }

        cleanUp();
    }
}
