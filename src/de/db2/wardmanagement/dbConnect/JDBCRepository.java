package de.db2.wardmanagement.dbConnect;


import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;
import static java.util.Map.entry;
import static java.util.UUID.randomUUID;
import static de.db2.wardmanagement.dbConnect.sql.*;

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.data.Repository.Provider;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Ward.Filter;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Staff;



public class JDBCRepository implements Repository
{

  // Service Provider Interface (SPI)  
  public static final class Provider implements Repository.Provider
  {

    @Override
     public Repository instance(){

      return JDBCRepository.instance();
    }

  }

  
  private final Connection conn;



  private JDBCRepository(Connection conn){ 
    this.conn = conn;
  }


  // Factory method
  static JDBCRepository instance(){
    try {
      var conn =
        DriverManager.getConnection(
          System.getProperty("wardstation.repo.jdbc.url"),
          System.getProperty("wardstation.repo.jdbc.user"),
          System.getProperty("wardstation.repo.jdbc.password")
        );

      var repo = new JDBCRepository(conn);

      repo.setup();

      return repo;

    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
  

  private static final String CREATE_TABLE_WARDS = """
	        CREATE TABLE IF NOT EXISTS wards (
	            id UUID PRIMARY KEY,
	            name VARCHAR(255) NOT NULL,
	            lastUpdate TIMESTAMP NOT NULL
	        );
	    """;

  private static final String CREATE_TABLE_ROOMS = """
	        CREATE TABLE IF NOT EXISTS rooms (
	            roomNr INT PRIMARY KEY,
	            roomName VARCHAR(255) NOT NULL,
	            ward UUID REFERENCES wards(id),
	            lastUpdate TIMESTAMP NOT NULL
	        );
	    """;
  
  private static final String CREATE_TABLE_BEDS = """
	        CREATE TABLE IF NOT EXISTS beds (
	            bedID UUID PRIMARY KEY,
	            patient UUID,
	            roomNr INT REFERENCES rooms(roomNr),
	            lastUpdate TIMESTAMP NOT NULL
	        );
	    """;
  
  private static final String CREATE_TABLE_STAFF = """
	        CREATE TABLE IF NOT EXISTS staff (
	            staffID UUID PRIMARY KEY,
	            preName VARCHAR(255),
	            name VARCHAR(255) NOT NULL,
	            birthday DATE,
	            function VARCHAR(255),
	            lastUpdate TIMESTAMP NOT NULL
	        );
	    """;



  // Set up DB tables etc.
  private void setup(){
    try (var stmt = conn.createStatement()){

    	stmt.execute(CREATE_TABLE_WARDS);
        stmt.execute(CREATE_TABLE_ROOMS);
        stmt.execute(CREATE_TABLE_BEDS);
        stmt.execute(CREATE_TABLE_STAFF);
    } catch (SQLException e) {
    	throw new RuntimeException(e);
    }
  }

  private static String insertSQL(Ward ward) {
	  return INSERT_INTO("wards")
              .VALUE("id", ward.id().value())
              .VALUE("name", ward.name())
              
              .toString();
  }
  
  private static String updateSQL(Ward ward) {
      return UPDATE("wards")
              .WHERE("id", ward.id().value())
              .SET("name", ward.name())      
              .toString();
  }


  private static Ward readWard(ResultSet rs) throws SQLException {
      return new Ward(
              new Id<>(rs.getString("id")),
              rs.getString("name"),
              rs.getTimestamp("lastUpdate").toInstant()
      );
  }


  @Override
  public Id<Ward> wardId() {
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

  private Optional<Ward> getWard(Id<Ward> id) {
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
  
//Room methods
  private static String insertSQL(Room room) {
      return INSERT_INTO("rooms")
              .VALUE("roomNr", room.id())
              .VALUE("roomName", room.roomName())
              .VALUE("ward", room.ward().id().value())
              .toString();
  }

  private static String updateSQL(Room room) {
      return UPDATE("rooms")
              .WHERE("roomNr", room.id())
              .SET("roomName", room.roomName())
              .SET("ward", room.ward().id().value())
              .toString();
  }

  private static Room readRoom(ResultSet rs) throws SQLException {
      return new Room(
              rs.getInt("roomNr"),
              rs.getString("roomName"),
              Reference.to(rs.getString("ward")),
              rs.getTimestamp("lastUpdate").toInstant()
      );
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

  private Optional<Room> getRoom(Id<de.db2.wardmanagement.backend.entity.Room> id) {
      try (var stmt = conn.createStatement();
           var rs = stmt.executeQuery("SELECT * FROM rooms WHERE roomNr = " + id)) {
          if (rs.next()) {
              return Optional.of(readRoom(rs));
          }
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
      return Optional.empty();
  }

  // Bed methods
  private static String insertSQL(Bed bed) {
      return INSERT_INTO("beds")
              .VALUE("bedID", bed.id().value())
              .VALUE("patient", bed.patient().map(p -> p.id().value()).orElse(null))
              .VALUE("roomNr", bed.room())
              .toString();
  }

  private static String updateSQL(Bed bed) {
      return UPDATE("beds")
              .WHERE("bedID", bed.id().value())
              .SET("patient", bed.patient().map(p -> p.id().value()).orElse(null))
              .SET("roomNr", bed.room())
              .toString();
  }

  private static Bed readBed(ResultSet rs) throws SQLException {
      return new Bed(
              new Id<>(rs.getString("bedID")),
              rs.getString("patient") != null ? Optional.of(Reference.to(rs.getString("patient"))) : Optional.empty(),
              rs.getInt("roomNr"),
              rs.getTimestamp("lastUpdate").toInstant()
      );
  }

  @Override
  public Id<Bed> id() {
      return new Id<>(UUID.randomUUID().toString());
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

  private Optional<Bed> getBed(Id<Bed> id) {
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

  // Staff methods
  private static String insertSQL(Staff staff) {
      return INSERT_INTO("staff")
              .VALUE("staffID", staff.id().value())
              .VALUE("preName", staff.prename())
              .VALUE("name", staff.name())
              .VALUE("birthday", staff.birthday())
              .VALUE("function", staff.function())
              .VALUE("ward", staff.ward())
              .toString();
  }

  private static String updateSQL(Staff staff) {
      return UPDATE("staff")
              .WHERE("staffID", staff.id().value())
              .SET("preName", staff.prename())
              .SET("name", staff.name())
              .SET("birthday", staff.birthday())
              .SET("function", staff.function())
              .SET("ward", staff.ward())
              .toString();
  }

  private static Staff readStaff(ResultSet rs) throws SQLException {
      return new Staff(
              new Id<>(rs.getString("staffID")),
              rs.getString("preName"),
              rs.getString("name"),
              rs.getDate("birthday").toLocalDate(),
              rs.getString("function"),
              rs.getTimestamp("lastUpdate").toInstant()
      );
  }

  @Override
  public Id<Staff> staffId() {
      return new Id<>(UUID.randomUUID().toString());
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

  private Optional<Staff> getStaff(Id<Staff> id) {
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


@Override
public Id<Ward> WardID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Id<de.db2.wardmanagement.backend.entity.Room> RoomID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Optional<de.db2.wardmanagement.backend.entity.Room> Room(Id<de.db2.wardmanagement.backend.entity.Room> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}


@Override
public Id<de.db2.wardmanagement.backend.entity.Bed> BedID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Optional<de.db2.wardmanagement.backend.entity.Bed> Bed(Id<de.db2.wardmanagement.backend.entity.Bed> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}


@Override
public Id<de.db2.wardmanagement.backend.entity.Staff> StaffID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Optional<de.db2.wardmanagement.backend.entity.Staff> Staff(Id<de.db2.wardmanagement.backend.entity.Staff> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}


@Override
public List<Ward> get(Filter arg0) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Room> get(de.db2.wardmanagement.backend.entity.Room.Filter arg0) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Bed> get(de.db2.wardmanagement.backend.entity.Bed.Filter arg0) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Staff> get(de.db2.wardmanagement.backend.entity.Staff.Filter arg0) {
	// TODO Auto-generated method stub
	return null;
}
}
