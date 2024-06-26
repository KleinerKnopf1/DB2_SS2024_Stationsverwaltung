package de.db2.wardmanagement.backend.entity;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;

public  record Room (
		Id<Room> id,
		String roomName,
		Reference<Ward> ward
		)

{
	 public static sealed interface Command permits Create, Update, Delete  {}

	  public static record Create
	  (
		Id<Room> id,
		String name
	  )
	  implements Command {}
	  
	  public static record Update
	  (
		Id<Room> id,
		String name
	  )
	  implements Command {}


	  public static record Delete
	  (
		Id<Room> id
	  )
	  implements Command {}


	  public static record Filter
	  (
		Optional<Reference<Ward>> ward,
		Optional<String> roomname 
	  )
	  {}


	  public static interface Operations
	  {
	    Room process(Command cmd) throws Exception;

	    List<Room> getRooms(Filter filter);

	    Optional<Room> getRoom(Id<Room> id);
	  }
	

}
