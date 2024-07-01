package de.db2.wardmanagement.backend.entity;

public record Patient (String name, String preName) {
	 
	@Override
	public final String toString() {
		return name + " " + preName;
	}
}