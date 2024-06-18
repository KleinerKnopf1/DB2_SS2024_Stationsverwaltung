package Stationsverwaltung;

import java.time.LocalDateTime;
import java.util.Optional;


public record Staff
(
  Id<Staff> id,
  String prename,
  String name,
  LocalDateTime birthday,
  String function
)
{
	
	  public static record Filter
	  (
	    Optional<String> name,
	    Optional<String> prename,
	    Optional<LocalDateTime> birthday,
	    Optional<String> function
	    
	  )
	  {}

}