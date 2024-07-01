package de.db2.wardmanagement.data;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Ward;


import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;

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
				
				repo.delete(deleteWard);
				
				yield deleteWard;
			}
			
			case Ward.Update update -> {
				
				var updateWard  = repo.getWard(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Ward ID"))
						.updateWith(update.name());
				
				repo.save(updateWard);
			
				yield updateWard;
			}
			
		};
	}


	@Override
	public List<Ward> getWard(Ward.Filter filter) {	
		return repo.getWard(filter);
	}

	@Override
	public Ward getWard(Id<Ward> id) {
		Ward ward = null;
		ward = repo.getWard(id).orElseThrow(() -> new IllegalArgumentException("Invalid Ward ID")); 
		return ward;
	}

	@Override
	public Room process(Room.Command cmd) throws Exception {
		return switch(cmd){
		
			case Room.Create crroom -> { 
				
				var rm = new Room(repo.RoomID(), crroom.name(), Reference.to(crroom.ward().id().toString()));
				
				repo.save(rm);
				
				yield rm;
				
			}
			
			case Room.Delete delete -> {
				
				var deleteRoom = repo.getRoom(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Room ID"));
				
				repo.delete(deleteRoom);
				
				yield deleteRoom;
			}
			
			case Room.Update update -> {
				
				var updateRoom  = repo.getRoom(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Room ID"))
						.updateWith(update.name(), update.ward());
				
				repo.save(updateRoom);
			
				yield updateRoom;
			}
			
		};	
	}
	
	@Override
	public List<Room> getRooms(Room.Filter filter) {
		return repo.getRoom(filter);
	}	
	
	@Override
	public Room getRoom(Id<Room> id) {
		Room room = null;
		room = repo.getRoom(id).orElseThrow(() -> new IllegalArgumentException("Invalid Room ID")); 
		return room;
	}

	@Override
	public Bed process(Bed.Command cmd) throws Exception {
		return switch(cmd){
		
			case Bed.Create crbed -> {
				
				var bed = new Bed(repo.BedID(), Reference.to(crbed.room().id().toString()), 
						crbed.patient() == null ? Optional.empty() : Optional.of(Reference.to(crbed.patient().toString())));
				
				repo.save(bed);
				
				yield bed;
				
			}
			
			case Bed.Unassign unassign -> {
				
				var unassignBed = repo.getBed(unassign.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"));
				unassignBed.updateWith(unassignBed.room(), Optional.empty());
				repo.save(unassignBed);
				
				yield unassignBed;
			}
			
			case Bed.Assign assign -> {
				
				var assignBed = repo.getBed(assign.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"));
				assignBed.updateWith(assignBed.room(), Optional.of(Reference.to(assign.patient().toString())));
				repo.save(assignBed);
				
				yield assignBed;
			}
			
			case Bed.Move move -> {
				
				var moveBed  = repo.getBed(move.id()).orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"));
				moveBed.updateWith(Reference.to(move.newRoom().id().toString()), Optional.of(Reference.to(moveBed.patient().toString())));
				
				repo.save(moveBed);
			
				yield moveBed;
			}
			
			case Bed.Delete delete -> {
				
				var deleteBed = repo.getBed(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID"));
				
				repo.delete(deleteBed);
				
				yield deleteBed;
			}
		};	
	
	}
	
	@Override
	public List<Bed> getBeds(Bed.Filter filter) {
		return repo.getBed(filter);
	}
	
	@Override
	public Bed getBed(Id<Bed> id) {
		Bed bed = null;
		bed = repo.getBed(id).orElseThrow(() -> new IllegalArgumentException("Invalid Bed ID")); 
		return bed;
	}  
}


