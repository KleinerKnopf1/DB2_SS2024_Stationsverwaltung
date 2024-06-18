package Stationsverwaltung;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record Ward
	(
	  Id<Ward> id,
	  String name,
	  Reference<Staff> staff
	
	)
{

	
	
}