package Stationsverwaltung;

import java.util.List;
import java.util.Optional;

import Stationsverwaltung.Ward.Command;
import Stationsverwaltung.Ward.Filter;

public class Wardmanagementservice implements IWardmanagementservice {
	
	private final Repository repo;
	private final Personmanagement pm;
	
	private Wardmanagementservice(
		    Repository repo,
		    Personmanagement pm
){ 
    this.repo = repo;
    this.pm = pm;
  }

  private static final Wardmanagementservice INSTANCE =
    new Wardmanagementservice(
      Repository.loadInstance(),
      Personmanagement.loadInstance()
    );

  public Wardmanagementservice instance(){
    return INSTANCE;
  }  

	
	@Override
	public Ward process(Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Ward> getWard(Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<Ward> getWard(Id<Ward> id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	@Override
	public Room process(Stationsverwaltung.Room.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Room> getRooms(Stationsverwaltung.Room.Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<Room> getRoom(Id<Room> id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	@Override
	public Bed process(Stationsverwaltung.Bed.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Bed> getBeds(Stationsverwaltung.Bed.Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<Bed> getBed(Id<Bed> id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	

}
