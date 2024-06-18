package Stationsverwaltung;


public  record Bed (
		Id<Ward> id,
		String name,
		Reference<Room> room
		)

{
	

}
