package Stationsverwaltung;


public  record Room (
		Id<Room> id,
		String roomName,
		Reference<Ward> ward
		)

{
	

}
