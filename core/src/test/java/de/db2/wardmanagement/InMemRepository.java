package de.db2.wardmanagement;

import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.type.Id;

import java.util.*;
import static java.util.UUID.randomUUID;
import java.util.stream.Collectors;

public final class InMemRepository implements Repository {

    private final Map<Id<Ward>, Ward> wards;
    private final Map<Id<Room>, Room> rooms;
    private final Map<Id<Bed>, Bed> beds;

    // Index of Room IDs to their Ward
    private final Map<Id<Room>, Id<Ward>> roomWardIndex;

    // Index of Bed IDs to their Room
    private final Map<Id<Bed>, Id<Room>> bedRoomIndex;

    public static final class Provider implements Repository.Provider {
        @Override
        public Repository instance() {
            return new InMemRepository();
        }
    }

    private InMemRepository() {
        this.wards = new HashMap<>();
        this.rooms = new HashMap<>();
        this.beds = new HashMap<>();
        this.roomWardIndex = new HashMap<>();
        this.bedRoomIndex = new HashMap<>();
    }

    @Override
    public Id<Ward> wardId() {
        return new Id<>(randomUUID().toString());
    }

    @Override
    public void save(Ward ward) {
        wards.put(ward.id(), ward);
    }

    @Override
    public List<Ward> get(Ward.Filter filter) {
        return wards.values().stream()
                .filter(w -> filter.name().map(n -> n.equals(w.name())).orElse(true))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Ward> getWard(Id<Ward> id) {
        return Optional.ofNullable(wards.get(id));
    }

    @Override
    public void deleteWard(Id<Ward> id) {
        wards.remove(id);
        rooms.values().removeIf(room -> roomWardIndex.get(room.id()).equals(id));
        roomWardIndex.values().removeIf(wardId -> wardId.equals(id));
    }

    @Override
    public Id<Room> roomId() {
        return new Id<>(randomUUID().toString());
    }

    @Override
    public void save(Room room) {
        rooms.put(room.id(), room);
        roomWardIndex.put(room.id(), room.wardId());
    }

    @Override
    public List<Room> get(Room.Filter filter) {
        return rooms.values().stream()
                .filter(r -> filter.name().map(n -> n.equals(r.name())).orElse(true))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Room> getRoom(Id<Room> id) {
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public void deleteRoom(Id<Room> id) {
        rooms.remove(id);
        beds.values().removeIf(bed -> bedRoomIndex.get(bed.id()).equals(id));
        bedRoomIndex.values().removeIf(roomId -> roomId.equals(id));
    }

    @Override
    public Id<Bed> bedId() {
        return new Id<>(randomUUID().toString());
    }

    @Override
    public void save(Bed bed) {
        beds.put(bed.id(), bed);
        bedRoomIndex.put(bed.id(), bed.roomId());
    }

    @Override
    public List<Bed> get(Bed.Filter filter) {
        return beds.values().stream()
                .filter(b -> filter.name().map(n -> n.equals(b.name())).orElse(true))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Bed> getBed(Id<Bed> id) {
        return Optional.ofNullable(beds.get(id));
    }

    @Override
    public void deleteBed(Id<Bed> id) {
        beds.remove(id);
        bedRoomIndex.remove(id);
    }
}
