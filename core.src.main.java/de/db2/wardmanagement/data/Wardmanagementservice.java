package de.db2.wardmanagement.data;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Ward;

import de.db2.wardmanagement.backend.type.Id;

public class Wardmanagementservice implements IWardmanagementservice {
	
	
	private final Repository repo;
	@SuppressWarnings("unused")
	private final Personmanagement pm;
	
	
	private Wardmanagementservice(Repository repo, Personmanagement pm){ 
		this.repo = repo;
		this.pm = pm;
	}
	
	
	private static final Wardmanagementservice INSTANCE = new Wardmanagementservice(
			Repository.loadInstance(), 
			Personmanagement.loadInstance()
			);

	
	public Wardmanagementservice instance(){
		return INSTANCE;
	}

	
	@Override
	public Ward process(Ward.Command cmd) throws Exception {
		return switch(cmd){
		
		case Ward.Create crwd -> { 
			
			var wd = new Ward(repo.WardID(), crwd.name());
			
			repo.save(wd);
			
			yield wd;
			
		}
		
		case Ward.Delete delete -> {
			
			var deleteWard = repo.getWard(delete.id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid TeleConsultation ID"));
			
			yield deleteWard;
		}
		
		case Ward.Update update -> {
			
			var updateWard  = repo.getWard(update.id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Message ID"))
					.updateWith(update.name());
			
			repo.save(updateWard);
		
			yield updateWard;
		}};
	}

	@Override
	public List<Ward> getWard(Ward.Filter filter) {	
		return repo.get(filter);
	}

	@Override
	public Optional<Ward> getWard(Id<Ward> id) {
		return repo.getWard(id);
	}

	@Override
	public Room process(Room.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Room> getRooms(Room.Filter filter) {
		return repo.get(filter);
	}	
	
	@Override
	public Optional<Room> getRoom(Id<Room> id) {
		return repo.Room(id);
	}

	@Override
	public Bed process(Bed.Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Bed> getBeds(Bed.Filter filter) {
		return repo.get(filter);
	}
	
	@Override
	public Optional<Bed> getBed(Id<Bed> id) {
		return repo.Bed(id);
	}  
}


