package de.db2.wardmanagement.backend.type;

import java.util.Optional;

public record Reference <T>
	
(
Id<T> id,
Optional<String> display
)
{

	public Reference<T> withDisplay(String d){ 
	return new Reference<>(
	this.id,
	Optional.of(d)
	);
	}

	public static <T> Reference<T> to(String id){
	return new Reference<>(
	new Id<>(id),
	Optional.empty()
	);
	}
	
	@Override
	public String toString() {
		return id.value().toString();
		
	}

}
