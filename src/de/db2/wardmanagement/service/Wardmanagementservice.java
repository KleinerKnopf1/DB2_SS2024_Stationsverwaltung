package de.db2.wardmanagement.service;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.Personmanagement;
import de.db2.wardmanagement.database.Repository;
import de.db2.wardmanagement.entities.Bed;
import de.db2.wardmanagement.entities.Room;
import de.db2.wardmanagement.entities.Ward;
import de.db2.wardmanagement.entities.Ward.Command;
import de.db2.wardmanagement.entities.Ward.Filter;

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
	public Room process(de.db2.wardmanagement.entities.Room.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Room> getRooms(de.db2.wardmanagement.entities.Room.Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<Room> getRoom(Id<Room> id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	@Override
	public Bed process(de.db2.wardmanagement.entities.Bed.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Bed> getBeds(de.db2.wardmanagement.entities.Bed.Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<Bed> getBed(Id<Bed> id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	

}
