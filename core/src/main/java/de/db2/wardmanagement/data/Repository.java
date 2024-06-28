package de.db2.wardmanagement.data;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Staff;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.type.Id;

public interface Repository {

	Id<Ward> WardID();
	Id<Room> RoomID();
	Id<Bed> BedID();
	Id<Staff> StaffID();
	
	void save(Ward ward) throws Exception;
	void save(Room room) throws Exception;
	void save(Bed bed) throws Exception;
	void save(Staff staff) throws Exception;

	void delete(Ward ward);
	void delete (Room room);
	void delete (Bed bed);

	List<Ward> getWard(Ward.Filter filter);
	List<Room> getRoom(Room.Filter filter);
	List<Bed> getBed(Bed.Filter filter);
	List<Staff> getStaff(Staff.Filter filter);

	Optional<Ward> getWard(Id<Ward> id);
	Optional<Room> getRoom(Id<Room> id);
	Optional<Bed> getBed(Id<Bed> id);
	Optional<Staff> getStaff(Id<Staff> id);


	public static interface Provider {
		Repository instance();
	}

	public static Repository loadInstance(){
		return ServiceLoader.load(Provider.class)
				.iterator()
				.next()
				.instance();
	}
}