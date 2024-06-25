package de.db2.wardmanagement.data;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Ward.Command;
import de.db2.wardmanagement.backend.entity.Ward.Filter;
import de.db2.wardmanagement.backend.type.Id;

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
public Ward process(Command cmd) {
	return switch(cmd){

    case Ward.Create crwd -> { 

    var wd = new Ward(
    		repo.WardID(),
    	    post.author(),
    	    Instant.now()
    	            );
      
        repo.save(wd);
      
        yield wd;

    }

    case Ward.Delete delete -> { 

        var deleteWard =
          repo.getWard(delete.id());
        
        yield deleteWard;
      }
  };
}

@Override
public List<Ward> getWards(Filter filter) {
	
	return repo.get(filter);
}

@Override
public Optional<Ward> getWard(Id<Ward> id) {
	
	return repo.getWard(id);
}

@Override
public Room process(de.db2.wardmanagement.backend.entity.Room.Command cmd) throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public List<Room> getRooms(de.db2.wardmanagement.backend.entity.Room.Filter filter) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Optional<Room> getRoom(Id<Room> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}

@Override
public Bed process(de.db2.wardmanagement.backend.entity.Bed.Command cmd) throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public List<Bed> getBeds(de.db2.wardmanagement.backend.entity.Bed.Filter filter) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Optional<Bed> getBed(Id<Bed> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}  

	
	
	
	

}
