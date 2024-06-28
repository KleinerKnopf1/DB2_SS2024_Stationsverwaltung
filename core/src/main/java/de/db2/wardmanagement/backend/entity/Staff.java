package de.db2.wardmanagement.backend.entity;

import java.time.LocalDate;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;


public record Staff
(
  Id<Staff> id,
  String prename,
  String name,
  LocalDate birthday,
  String function,
  Reference<Ward> ward
)
{
	
	  public static record Filter
	  (
	    Optional<String> name,
	    Optional<String> prename,
	    Optional<LocalDate> birthday,
	    Optional<String> function
	    
	  )
	  {}

}