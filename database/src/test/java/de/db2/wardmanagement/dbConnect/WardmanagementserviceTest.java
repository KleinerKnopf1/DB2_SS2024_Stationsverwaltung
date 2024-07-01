package de.db2.wardmanagement.dbConnect;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Patient;
import de.db2.wardmanagement.data.Personmanagement;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.data.Wardmanagementservice;

public final class WardmanagementserviceTest {

	private static Repository repo;
	private static Personmanagement personManagement;
	private static Wardmanagementservice service;

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

		Ward ward = createWard("Station 31");
		assertNotNull("Created Ward should not be null", ward);

		var loadedWard = service.getWard(ward.id());
		assertNotNull("Loaded Ward should not be null", loadedWard);
		
		if(ward == null)
			throw new IllegalStateException("There is no Ward to edit!");
		
		String newName = "Station 32";
		ward = updateWard(ward, newName);
		assertTrue("The name of the Ward should have changed to '" + newName + "'!", ward.name() == newName);
		
		loadedWard = service.getWard(ward.id());
		if(loadedWard != null)		
			assertTrue("The name of the Ward should have changed to '" + newName + "'!", loadedWard.name() == newName);

		deleteWard(ward);
		loadedWard = service.getWard(ward.id());
		assertNull("The loaded Ward should now be null!", loadedWard);
		
	}
	
	@Test
	public void testRoomLifeCycle() throws Exception {

		Ward ward = createWard("Station 41");
		String newWardName = "Station 42";
		Ward newWard = createWard(newWardName);
		
		Room room = createRoom("41.1", ward);
		assertNotNull("Created Room should not be null", room);

		var loadedRoom = service.getRoom(room.id());
		assertNotNull("Loaded Room should not be null", loadedRoom);
		
		if(room == null)
			throw new IllegalStateException("There is no Room to edit!");
		
		String newName = "42.1";
		room = updateRoom(room, newName, newWard);
		assertTrue("The name of the Room and the Ward should have changed to '" + newName + "' and '" + newWard.name() + "'!", 
				room.name() == newName && service.getWard(room.ward().id()).name() == newWardName);
		
		loadedRoom = service.getRoom(room.id());
		if(loadedRoom != null)		
			assertTrue("The name of the Room should have changed to '" + newName + "'!", loadedRoom.name() == newName);

		deleteRoom(room);
		loadedRoom = service.getRoom(room.id());
		assertNull("The loaded Room should now be null!", loadedRoom);
		
	}
	
	@Test
	public void testBedLifeCycle() throws Exception {

		Ward ward = createWard("Station 41");
		
		Room room = createRoom("41.1", ward);
		Room room2 = createRoom("41.2", ward);

		
		Bed bed = createBed(room);
		assertNotNull("Created Bed should not be null", bed);
		
		var loadedBed = service.getBed(bed.id());
		assertNotNull("Loaded Bed should not be null", loadedBed);
		
		if(bed == null)
			throw new IllegalStateException("There is no Bed to edit!");
		
		bed = moveBed(bed, room2);
		assertTrue("The Room of the Bed should have changed to '" + room2.name() + "'!", 
				 service.getRoom(bed.room().id()).name() == room2.name());
		
		deleteBed(bed);
		loadedBed = service.getBed(bed.id());
		assertNull("The loaded Bed should now be null!", loadedBed);
		
	}

	private Ward createWard(String name) throws Exception {
		Ward ward = service.process(new Ward.Create(name));
		return ward;
	}

	private Ward updateWard(Ward ward, String newName) throws Exception {
		ward = service.process(new Ward.Update(ward.id(), newName));
		return ward;
	}

	private Ward deleteWard(Ward ward) throws Exception {
		ward = service.process(new Ward.Delete(ward.id()));
		return ward;
	}

	private Room createRoom(String name, Ward ward) throws Exception {
		Room room = service.process(new Room.Create(name, ward));
		return room;
	}

	private Room updateRoom(Room room, String newName, Ward newWard) throws Exception {
		room = service.process(new Room.Update(room.id(), newName, newWard));
		return room;
	}

	private Room deleteRoom(Room room) throws Exception {
		room = service.process(new Room.Delete(room.id()));
		return room;
	}

	private Bed createBed(Room room) throws Exception {
		Bed bed = service.process(new Bed.Create(room, null));
		return bed;
	}

	private Bed moveBed(Bed bed, Room newRoom) throws Exception {
		bed = service.process(new Bed.Move(bed.id(), newRoom));
		return bed;
	}

	private Bed assignBed(Bed bed, Patient patient) throws Exception {
		bed = service.process(new Bed.Assign(bed.id(), patient));
		return bed;
	}

	private Bed unassignBed(Bed bed) throws Exception {
		bed = service.process(new Bed.Unassign(bed.id()));
		return bed;
	}

	private Bed deleteBed(Bed bed) throws Exception {
		bed = service.process(new Bed.Delete(bed.id()));
		return bed;
	}

	private Patient createPatient(String name, String preName) {
		return new Patient(name, preName);
	}

}
