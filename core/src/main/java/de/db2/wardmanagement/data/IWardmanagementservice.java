package de.db2.wardmanagement.data;

import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Ward;

public interface IWardmanagementservice  extends Ward.Operations, Room.Operations, Bed.Operations {

	
}
