package de.db2.wardmanagement.backend.entity;

import java.time.LocalDateTime;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;


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