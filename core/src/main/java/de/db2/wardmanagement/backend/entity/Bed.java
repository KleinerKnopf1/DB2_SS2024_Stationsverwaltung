package de.db2.wardmanagement.backend.entity;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;

public record Bed(Id<Bed> id, Reference<Room> room, Optional<Reference<Patient>> patient

)

{
	public static sealed interface Command permits Create, Delete, Move, Unassign, Assign {
	}

	public static record Create(Room room, Patient patient) implements Command {
	}

	public static record Delete(Id<Bed> id) implements Command {
	}

	public static record Move(Id<Bed> id, Room newRoom) implements Command {
	}

	public static record Unassign(Id<Bed> id) implements Command {
	}

	public static record Assign(Id<Bed> id, Patient patient) implements Command {
	}

	public static record Filter(Optional<Reference<Room>> room, Optional<Reference<Patient>> patient) {
	}

	public static interface Operations {
		Bed process(Command cmd) throws Exception;

		List<Bed> getBeds(Filter filter);

		Bed getBed(Id<Bed> id);
	}

	public Bed updateWith(Room newRoom, Patient newPatient) {
		return new Bed(this.id, Reference.to(newRoom.id().toString()),
				newPatient == null ? Optional.empty() : Optional.of(Reference.to(newPatient.toString())));
	}

	public Bed updateWith(Reference<Room> newRoom, Optional<Reference<Patient>> newPatient) {
		return new Bed(this.id, newRoom, newPatient);
	}

}
