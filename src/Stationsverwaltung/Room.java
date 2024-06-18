package Stationsverwaltung;

import java.util.List;
import java.util.Optional;

public  record Room (
		Id<Room> id,
		String roomName,
		Reference<Room> Room
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

	    List<Stationsverwaltung.Room> getRooms(Filter filter);

	    Optional<Stationsverwaltung.Room> getRoom(Id<Stationsverwaltung.Room> id);
	  }
	

}
