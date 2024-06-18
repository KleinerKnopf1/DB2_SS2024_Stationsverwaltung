package Stationsverwaltung;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public interface Repository {
	
	Id<Ward> WardID();
	  void save(Ward tc) throws Exception;	
	  List<Ward> get(Ward.Filter filter);	
	  Optional<Ward> getWard(Id<Ward> id);
	  


	 Id<Room> RoomID();
	  void save(Room msg) throws Exception;
	  List<Room> get(Room.Filter filter);
	  Optional<Room> Room(Id<Room> id);
	  
	
	 
	 Id<Bed> BedID();
	  void save(Bed msg) throws Exception;
	  List<Room> get(Bed.Filter filter);
	  Optional<Bed> Bed(Id<Bed> id);
	  
	  
	  
	 Id<Staff> StaffID();
	  void save(Staff msg) throws Exception;
	  List<Staff> get(Staff.Filter filter);
	  Optional<Staff> Staff(Id<Staff> id);
	 
	  //Was macht das? Ab hier noch Code Anpassen bisher nur copy paste von clins Repository class
	  public static interface Provider
	  { 
	    Repository instance();
	  }

	  public static Repository loadInstance(){
	    return
	      ServiceLoader.load(Provider.class)
	        .iterator()
	        .next()
	        .instance();
	  }

	}
