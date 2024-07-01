package de.db2.wardmanagement.backend.entity;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;

public record Room(Id<Room> id, String name, Reference<Ward> ward) {
	public static sealed interface Command permits Create, Update, Delete {
	}

	public static record Create(String name, Ward ward) implements Command {
	}

	public static record Update(Id<Room> id, String name, Ward ward) implements Command {
	}

	public static record Delete(Id<Room> id) implements Command {
	}

	public static record Filter(Optional<Reference<Ward>> ward, Optional<String> roomname) {
	}

	public static interface Operations {
		Room process(Command cmd) throws Exception;

		List<Room> getRooms(Filter filter);

		Room getRoom(Id<Room> id);
	}

	public Room updateWith(String newName, Ward newWard) {
		return new Room(this.id, newName, Reference.to(newWard.id().toString()));
	}
}
