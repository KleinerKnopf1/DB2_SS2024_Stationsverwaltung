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
	
	
	public Wardmanagementservice(Repository repo, Personmanagement pm){ 
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
						.orElseThrow(() -> new IllegalArgumentException("Invalid Ward ID"));
				
				repo.deleteWard(delete.id());
				
				yield deleteWard;
			}
			
			case Ward.Update update -> {
				
				var updateWard  = repo.getWard(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Ward ID"))
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
		return switch(cmd){
		
			case Room.Create crroom -> { 
				
				var rm = new Room(repo.RoomID(), crroom.name());
				
				repo.save(rm);
				
				yield rm;
				
			}
			
			case Room.Delete delete -> {
				
				var deleteRoom = repo.Room(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Room ID"));
				
				repo.deleteRoom(delete.id());
				
				yield deleteRoom;
			}
			
			case Room.Update update -> {
				
				var updateRoom  = repo.Room(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Room ID"))
						.updateWith(update.name(), update.ward());
				
				repo.save(updateRoom);
			
				yield updateRoom;
			}};	
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
		return switch(cmd){
		
			case Bed.Create crbed -> {
				
				var bed = new Bed(repo.BedID(), crbed.patient, crbed.room());
				
				repo.save(bed);
				
				yield bed;
				
			}
			
			case Bed.Unassign unassign -> {
				
				var unassignBed = repo.Bed(unassign.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"));
				
				repo.unassignBed(unassignBed.id());
				
				yield unassignBed;
			}
			
			case Bed.Assign assign -> {
				
				var assignBed  = repo.Bed(assign.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"))
						.updateWith(update.patient());
				
				repo.save(assignBed);
			
				yield assignBed;
			}
			
			case Bed.Move move -> {
				
				var moveBed  = repo.Bed(move.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"))
						.updateWith(update.room());
				
				repo.save(moveBed);
			
				yield moveBed;
			}};	
	
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


