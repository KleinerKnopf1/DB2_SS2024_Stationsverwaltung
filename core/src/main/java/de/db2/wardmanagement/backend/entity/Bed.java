package de.db2.wardmanagement.backend.entity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;

public  record Bed (
		Id<Bed> id,
		Reference<Room> room,
		Optional<Reference<Patient>> patient
		
		)

{
	public static sealed interface Command permits Create, Delete, Move, Unassign  {}

	  public static record Create
	  (
		Id<Bed> id,
	    Reference<Room> room,
	    Optional<Reference<Patient>> patient
	    
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
		Reference<Room> room,
		Optional<Reference<Patient>> patient
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

	    List<Bed> getBeds(Filter filter);

	    Optional<Bed> getBed(Id<Bed> id);
	  }
	  
	  public Bed updateWith(Reference<Room> newRoom, Optional<Reference<Patient>> newPatient){
			return new Bed(this.id, newRoom, newPatient);
		}

}
