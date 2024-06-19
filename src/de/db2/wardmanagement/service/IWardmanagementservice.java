package de.db2.wardmanagement.service;

import de.db2.wardmanagement.entities.Bed;
import de.db2.wardmanagement.entities.Room;
import de.db2.wardmanagement.entities.Ward;

public interface IWardmanagementservice  extends Ward.Operations, Room.Operations, Bed.Operations {

	
}
