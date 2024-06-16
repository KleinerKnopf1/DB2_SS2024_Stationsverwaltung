package Stationsverwaltung;

public interface Repository {
	
	
	public int WardID();
	public int RoomID();
	public int BedID();
	public int StaffID();
	public Ward  saveWard();
	public Room saveRoom();
	
	
}


