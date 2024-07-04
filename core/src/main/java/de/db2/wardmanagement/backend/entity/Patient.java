package de.db2.wardmanagement.backend.entity;

import de.db2.wardmanagement.backend.type.Id;

public record Patient (Id<Patient> id, String name, String preName) {
	 
	@Override
	public final String toString() {
		return id.toString();
	}
}