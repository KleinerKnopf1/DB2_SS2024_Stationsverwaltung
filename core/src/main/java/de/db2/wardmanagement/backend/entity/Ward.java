package de.db2.wardmanagement.backend.entity;

import java.util.List;
import java.util.Optional;

import de.db2.wardmanagement.backend.type.Id;
public record Ward(Id<Ward> id, String name) {
	
	public static sealed interface Command permits Create, Update, Delete  {}
	
	public static record Create(Id<Ward> id, String name) implements Command {}
	
	public static record Update(Id<Ward> id, String name) implements Command {}
	
	public static record Delete(Id<Ward> id, String name) implements Command {}

	public static record Filter(Optional<String> name) {}
	
	public static interface Operations {
		
		Ward process(Command cmd) throws Exception;
		
		List<Ward> getWard(Filter filter);
		
		Optional<Ward> getWard(Id<Ward> id);
	}
	
	
	public Ward updateWith(String newName){
		return new Ward(this.id, newName);
	}
}