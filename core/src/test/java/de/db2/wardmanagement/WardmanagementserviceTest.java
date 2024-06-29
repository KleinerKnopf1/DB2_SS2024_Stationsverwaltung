package de.db2.wardmanagement;
import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Staff;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Ward.Filter;
import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.data.Personmanagement;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.data.Wardmanagementservice;

public class WardmanagementserviceTest {
	private Wardmanagementservice service;

	public void setUp() {
		service = new Wardmanagementservice(Repository.loadInstance(), Personmanagement.loadInstance());
	}

	@Test
	public void testInstanceNotNull() {
		assertNotNull("Instance should not be null", service);
	}

	@Test
	public void testProcessWardCommand() throws Exception {
		Ward.Command cmd = new Ward.Command();
		// Set required fields for cmd
		Ward result = service.process(cmd);
		assertNull("Result should be null", result);
	}

	@Test
	public void testGetWard() {
		List<Ward> result = service.getWard(new Ward.Filter());
		assertNotNull("Result should not be null", result);
	}

	@Test
	public void testGetWardById() {
		Optional<Ward> result = service.getWard(new Id<Ward>());
		assertNotNull("Result should not be null", result);
	}

	@Test
	public void testProcessRoomCommand() throws Exception {
		Room.Command cmd = new Room.Command();
		// Set required fields for cmd
		Room result = service.process(cmd);
		assertNull("Result should be null", result);
	}

	@Test
	public void testGetRooms() {
		List<Room> result = service.getRooms(new Room.Filter());
		assertNotNull("Result should not be null", result);
	}

	@Test
	public void testGetRoomById() {
		Optional<Room> result = service.getRoom(new Id<Room>());
		assertNotNull("Result should not be null", result);
	}

	@Test
	public void testProcessBedCommand() throws Exception {
		Bed.Command cmd = new Bed.Command();
		// Set required fields for cmd
		Bed result = service.process(cmd);
		assertNull("Result should be null", result);
	}

	@Test
	public void testGetBeds() {
		List<Bed> result = service.getBeds(new Bed.Filter());
		assertNotNull("Result should not be null", result);
	}

	@Test
	public void testGetBedById() {
		Optional<Bed> result = service.getBed(new Id<Bed>());
		assertNotNull("Result should not be null", result);
	}
}
