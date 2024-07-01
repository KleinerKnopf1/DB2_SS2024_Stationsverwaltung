package de.db2.wardmanagement.dbConnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static de.db2.wardmanagement.dbConnect.sql.*;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Staff;

public class JDBCRepository implements Repository {

	// Service Provider Interface (SPI)
	public static final class Provider implements Repository.Provider {

		@Override
		public Repository instance() {

			return JDBCRepository.instance();
		}

	}

	private final Connection conn;

	private JDBCRepository(Connection conn) {
		this.conn = conn;
	}

	// Factory method
	public static JDBCRepository instance() {
		try {
			var conn = DriverManager.getConnection(System.getProperty("wardmanagement.repo.jdbc.url"),
					System.getProperty("wardmanagement.repo.jdbc.user"),
					System.getProperty("wardmanagement.repo.jdbc.password"));

			var repo = new JDBCRepository(conn);

			repo.setup();

			return repo;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String CREATE_TABLE_WARDS = """
			    CREATE TABLE IF NOT EXISTS wards (
			        id UUID PRIMARY KEY,
			        name VARCHAR(255) NOT NULL
			    );
			""";

	private static final String CREATE_TABLE_ROOMS = """
			    CREATE TABLE IF NOT EXISTS rooms (
			        roomId UUID PRIMARY KEY,
			        roomName VARCHAR(255) NOT NULL,
			        ward UUID REFERENCES wards(id)
			    );
			""";

	private static final String CREATE_TABLE_BEDS = """
			    CREATE TABLE IF NOT EXISTS beds (
			        bedID UUID PRIMARY KEY,
			        patient UUID,
			        roomId INT REFERENCES rooms(roomId)
			    );
			""";

	private static final String CREATE_TABLE_STAFF = """
			    CREATE TABLE IF NOT EXISTS staff (
			        staffID UUID PRIMARY KEY,
			        preName VARCHAR(255),
			        name VARCHAR(255) NOT NULL,
			        birthday DATE,
			        function VARCHAR(255),
			        ward UUID REFERENCES wards(id)
			    );
			""";

	// Set up DB tables etc.
	private void setup() {
		try (var stmt = conn.createStatement()) {

			stmt.execute(CREATE_TABLE_WARDS);
			stmt.execute(CREATE_TABLE_ROOMS);
			stmt.execute(CREATE_TABLE_BEDS);
			stmt.execute(CREATE_TABLE_STAFF);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static String insertSQL(Ward ward) {
		return INSERT_INTO("wards").VALUE("id", ward.id().value()).VALUE("name", ward.name())

				.toString();
	}

	// Room methods
	private static String insertSQL(Room room) {
		return INSERT_INTO("rooms").VALUE("roomId", room.id()).VALUE("roomName", room.name())
				.VALUE("ward", room.ward().id().value()).toString();
	}

	// Bed methods
	private static String insertSQL(Bed bed) {
		return INSERT_INTO("beds").VALUE("bedID", bed.id().value())
				.VALUE("patient", bed.patient().map(p -> p.id().value()).orElse(null)).VALUE("roomId", bed.room())
				.toString();
	}

	// Staff methods
	private static String insertSQL(Staff staff) {
		return INSERT_INTO("staff").VALUE("staffID", staff.id().value()).VALUE("preName", staff.prename())
				.VALUE("name", staff.name()).VALUE("birthday", staff.birthday()).VALUE("function", staff.function())
				.VALUE("ward", staff.ward()).toString();
	}

	private static String updateSQL(Ward ward) {
		return UPDATE("wards").WHERE("id", ward.id().value()).SET("name", ward.name()).toString();
	}

	private static String updateSQL(Room room) {
		return UPDATE("rooms").WHERE("roomId", room.id()).SET("roomName", room.name())
				.SET("ward", room.ward().id().value()).toString();
	}

	private static String updateSQL(Bed bed) {
		return UPDATE("beds").WHERE("bedID", bed.id().value())
				.SET("patient", bed.patient().map(p -> p.id().value()).orElse(null)).SET("roomId", bed.room())
				.toString();
	}

	private static String updateSQL(Staff staff) {
		return UPDATE("staff").WHERE("staffID", staff.id().value()).SET("preName", staff.prename())
				.SET("name", staff.name()).SET("birthday", staff.birthday()).SET("function", staff.function())
				.SET("ward", staff.ward()).toString();
	}

	private static String deleteSQL(Ward ward) {
		return DELETE_FROM("wards").WHERE("id", ward.id().value()).toString();
	}
	
	private static String deleteSQL(Room room) {
		return DELETE_FROM("rooms").WHERE("id", room.id().value()).toString();
	}
	
	private static String deleteSQL(Bed bed) {
		return DELETE_FROM("beds").WHERE("id", bed.id().value()).toString();
	}
	
	private static Ward readWard(ResultSet rs) throws SQLException {

		return new Ward(new Id<>(rs.getString("id")), rs.getString("name"));
	}

	private static Room readRoom(ResultSet rs) throws SQLException {
		return new Room(new Id<>(rs.getString("roomId")), rs.getString("roomName"), Reference.to(rs.getString("ward")));
	}

	private static Bed readBed(ResultSet rs) throws SQLException {
		return new Bed(new Id<>(rs.getString("bedID")), Reference.to(rs.getString("roomId")),
				rs.getString("patient") != null ? Optional.of(Reference.to(rs.getString("patient")))
						: Optional.empty());
	}

	private static Staff readStaff(ResultSet rs) throws SQLException {
		return new Staff(new Id<>(rs.getString("staffID")), rs.getString("preName"), rs.getString("name"),
				rs.getDate("birthday").toLocalDate(), rs.getString("function"), Reference.to(rs.getString("id")));
	}

	@Override
	public Id<Ward> WardID() {
		return new Id<>(UUID.randomUUID().toString());
	}

	@Override
	public Id<Room> RoomID() {
		return new Id<>(UUID.randomUUID().toString());

	}

	@Override
	public Id<Bed> BedID() {
		return new Id<>(UUID.randomUUID().toString());
	}

	@Override
	public Id<Staff> StaffID() {
		return new Id<>(UUID.randomUUID().toString());
	}

	@Override
	public void save(Ward ward) throws Exception {
		try (var stmt = conn.createStatement()) {
			var sql = getWard(ward.id()).isPresent() ? updateSQL(ward) : insertSQL(ward);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(Room room) throws Exception {
		try (var stmt = conn.createStatement()) {
			var sql = getRoom(room.id()).isPresent() ? updateSQL(room) : insertSQL(room);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(Bed bed) throws Exception {
		try (var stmt = conn.createStatement()) {
			var sql = getBed(bed.id()).isPresent() ? updateSQL(bed) : insertSQL(bed);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save(Staff staff) throws Exception {
		try (var stmt = conn.createStatement()) {
			var sql = getStaff(staff.id()).isPresent() ? updateSQL(staff) : insertSQL(staff);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Ward ward) {
		try (var stmt = conn.createStatement()) {
			if(!getWard(ward.id()).isPresent())
				return;
			var sql = deleteSQL(ward);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void delete(Room room) {
		try (var stmt = conn.createStatement()) {
			if(!getRoom(room.id()).isPresent())
				return;
			var sql = deleteSQL(room);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(Bed bed) {
		try (var stmt = conn.createStatement()) {
			if(!getBed(bed.id()).isPresent())
				return;
			var sql = deleteSQL(bed);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}	

	@Override
	public List<Ward> getWard(Ward.Filter filter) {

		var query = SELECT("*").FROM("wards");

		filter.name().ifPresent(ref -> query.WHERE("name", ref));

		try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
			var tcs = new ArrayList<Ward>();

			while (resultSet.next()) {
				tcs.add(readWard(resultSet));
			}

			return tcs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Room> getRoom(Room.Filter filter) {
		var query = SELECT("*").FROM("rooms");

		filter.roomname().ifPresent(ref -> query.WHERE("roomName", ref));
		filter.ward().ifPresent(ref -> query.WHERE("ward", ref.id().value()));

		try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
			var tcs = new ArrayList<Room>();

			while (resultSet.next()) {
				tcs.add(readRoom(resultSet));
			}

			return tcs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Bed> getBed(Bed.Filter filter) {
		var query = SELECT("*").FROM("beds");

		filter.room().ifPresent(ref -> query.WHERE("roomId", ref.id().value()));
		filter.patient().ifPresent(ref -> query.WHERE("patient", ref.id().value()));

		try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
			var tcs = new ArrayList<Bed>();

			while (resultSet.next()) {
				tcs.add(readBed(resultSet));
			}

			return tcs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Staff> getStaff(Staff.Filter filter) {
		var query = SELECT("*").FROM("staff");

		filter.birthday().ifPresent(ref -> query.WHERE("birthday", ref));
		filter.function().ifPresent(ref -> query.WHERE("function", ref));
		filter.name().ifPresent(ref -> query.WHERE("name", ref));
		filter.prename().ifPresent(ref -> query.WHERE("preName", ref));
		filter.ward().ifPresent(ref -> query.WHERE("ward", ref.id().value()));

		try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
			var tcs = new ArrayList<Staff>();

			while (resultSet.next()) {
				tcs.add(readStaff(resultSet));
			}

			return tcs;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<Ward> getWard(Id<Ward> id) {
		try (var stmt = conn.createStatement();
				var rs = stmt.executeQuery("SELECT * FROM wards WHERE id = '" + id.value() + "'")) {
			if (rs.next()) {
				return Optional.of(readWard(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Room> getRoom(Id<Room> id) {
		try (var stmt = conn.createStatement();
				var rs = stmt.executeQuery("SELECT * FROM rooms WHERE roomId = " + id)) {
			if (rs.next()) {
				return Optional.of(readRoom(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Bed> getBed(Id<Bed> id) {
		try (var stmt = conn.createStatement();
				var rs = stmt.executeQuery("SELECT * FROM beds WHERE bedID = '" + id.value() + "'")) {
			if (rs.next()) {
				return Optional.of(readBed(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Staff> getStaff(Id<Staff> id) {
		try (var stmt = conn.createStatement();
				var rs = stmt.executeQuery("SELECT * FROM staff WHERE staffID = '" + id.value() + "'")) {
			if (rs.next()) {
				return Optional.of(readStaff(rs));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return Optional.empty();
	}
}
