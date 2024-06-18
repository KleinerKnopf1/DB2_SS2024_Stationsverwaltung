package Stationsverwaltung;

import java.util.List;
import java.util.Optional;
public record Ward
	(
	  Id<Ward> id,
	  String name,
	  Reference<Staff> staff
	
	)
{

	 public static sealed interface Command permits Create, Update, Delete  {}

	  public static record Create
	  (
		Id<Ward> id,
		String name,
	    Reference<Room> room, 
	    Reference<Staff> staff
	  )
	  implements Command {}
	  
	  public static record Update
	  (
		Id<Ward> id,
		String name,
		Reference<Room> room, 
		Reference<Staff> staff
	  )
	  implements Command {}


	  public static record Delete
	  (
		Id<Ward> id,
		String name,
		Reference<Room> room, 
		Reference<Staff> staff
	  )
	  implements Command {}


	  public static record Filter
	  (
	    Optional<String> name
	  )
	  {}


	  public static interface Operations
	  {
	    Ward process(Command cmd) throws Exception;

	    List<Stationsverwaltung.Ward> getWards(Filter filter);

	    Optional<Stationsverwaltung.Ward> getWard(Id<Stationsverwaltung.Ward> id);
	  }


}