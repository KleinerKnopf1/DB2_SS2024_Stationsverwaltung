package de.db2.wardmanagement.entities;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.db2.wardmanagement.type.Id;
import de.db2.wardmanagement.type.Reference;

public  record Bed (
		Id<Bed> id,
		String name,
		Reference<Room> room
		)

{
	public static sealed interface Command permits Create, Delete, Move, Assign, Unassign  {}

	  public static record Create
	  (
		Id<Bed> id,
	    Reference<Room> room
	  )
	  implements Command {}
	  
	  public static record Delete
	  (
		Id<Bed> id
	  )
	  implements Command {}


	  public static record Move
	  (
		Id<Bed> id,
		Reference<Room> room
	  )
	  implements Command {}

	  public static record Assign
	  (
		Id<Bed> id,
		Reference<Patient> patient
	  )
	  implements Command {}
	  
	  public static record Unassign
	  (
		Id<Bed> id
	  )
	  implements Command {}

	  public static record Filter
	  (
	    Optional<Reference<Room>> room,
	    Optional<Reference<Ward>> ward,
	    Set<Boolean> assigned
	  )
	  {}


	  public static interface Operations
	  {
	    Bed process(Command cmd) throws Exception;

	    List<de.db2.wardmanagement.entities.Bed> getBeds(Filter filter);

	    Optional<de.db2.wardmanagement.entities.Bed> getBed(Id<de.db2.wardmanagement.entities.Bed> id);
	  }

}
