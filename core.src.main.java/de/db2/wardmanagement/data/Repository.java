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

	void save(Ward ward) throws Exception;

	List<Ward> get(Ward.Filter filter);

	Optional<Ward> getWard(Id<Ward> id);

	Id<Room> RoomID();

	void save(Room room) throws Exception;

	List<Room> get(Room.Filter filter);

	Optional<Room> Room(Id<Room> id);

	Id<Bed> BedID();

	void save(Bed bed) throws Exception;

	List<Bed> get(Bed.Filter filter);

	Optional<Bed> Bed(Id<Bed> id);

	Id<Staff> StaffID();

	void save(Staff staff) throws Exception;

	List<Staff> get(Staff.Filter filter);

	Optional<Staff> Staff(Id<Staff> id);

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