package de.db2.wardmanagement.data;


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

import de.db2.wardmanagement.backend.type.Id;
import de.db2.wardmanagement.backend.type.Reference;
import de.db2.wardmanagement.data.Repository;
import de.db2.wardmanagement.backend.entity.Ward;
import de.db2.wardmanagement.backend.entity.Ward.Filter;
import de.db2.wardmanagement.backend.entity.Room;
import de.db2.wardmanagement.backend.entity.Bed;
import de.db2.wardmanagement.backend.entity.Staff;



class JDBCRepository implements Repository
{

  // Service Provider Interface (SPI)  
  public static final class Provider implements Repository.Provider
  {

    @Override
     public Repository instance(){

      return JDBCRepository.instance();
    }

  }

  private static final Base64.Decoder DECODER = Base64.getDecoder();
  private static final Base64.Encoder ENCODER = Base64.getEncoder();

  private final Connection conn;



  private JDBCRepository(Connection conn){ 
    this.conn = conn;
  }


  // Factory method
  static JDBCRepository instance(){
    try {
      var conn =
        DriverManager.getConnection(
          System.getProperty("stationsverwaltung.repo.jdbc.url"),
          System.getProperty("stationsverwaltung.repo.jdbc.user"),
          System.getProperty("stationsverwaltung.repo.jdbc.password")
        );

      var repo = new JDBCRepository(conn);

      repo.setup();

      return repo;

    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }
  

  // NOTE: 
  // Column renamed explicitly "end_ts", because "end" seems to be a reserved keyword in SQL and leads to SQL syntax errors
  private static final String CREATE_TABLE_STATIONSVERWALTUNG = """
    CREATE TABLE IF NOT EXISTS teleconsultations (
      id UUID PRIMARY KEY,
      patient UUID NOT NULL,
      doctor UUID NOT NULL,
      status VARCHAR(10) NOT NULL,
      start_ts TIMESTAMP NOT NULL,
      end_ts TIMESTAMP, 
      lastUpdate TIMESTAMP NOT NULL
    );
  """;

  private static final String CREATE_TABLE_MESSAGES = """
    CREATE TABLE IF NOT EXISTS messages (
      id UUID PRIMARY KEY,
      teleconsultation UUID NOT NULL REFERENCES teleconsultations(id),
      author UUID NOT NULL,
      posted_at TIMESTAMP NOT NULL,
      text VARCHAR NOT NULL,
      attachment VARCHAR, 
      lastUpdate TIMESTAMP NOT NULL
    );
  """;


  // Set up DB tables etc.
  private void setup(){
    try (var stmt = conn.createStatement()){

      stmt.execute(CREATE_TABLE_STATIONSVERWALTUNG);
      stmt.execute(CREATE_TABLE_MESSAGES);
 
    } catch (SQLException e){
      throw new RuntimeException(e);
    }
  }

/*
  @SafeVarargs
  private static String csv(String... values){
    return
      Stream.of(values)
        .filter(s -> !s.isBlank())
        .reduce("", (s,t) -> s + "," + t);
  }


  private static String quoted(String s){
    return String.format("'%s'",s);
  }

  private static String sqlValue(Object obj){

    return switch(obj){
//      case Optional<?> opt  -> opt.map(t -> sqlValue(t)).orElse(""); 
      case Id<?> id         -> quoted(id.value());
      case LocalDate date   -> quoted(Date.valueOf(date).toString());
      case LocalDateTime dt -> quoted(Timestamp.valueOf(dt).toString());
      case Instant t        -> quoted(Timestamp.from(t).toString());
      case Integer n        -> Integer.toString(n);
      case Long n           -> Long.toString(n);
      case Double n         -> Double.toString(n);
      default               -> quoted(obj.toString());
    };

  }


  private static String insertSQL(TeleConsultation tc){ 

    return "INSERT INTO teleconsultations(" +
      csv(
        "id",
        "patient",
        "doctor",
        "status",
        "start_ts",
        tc.period().end().map(e -> "end_ts").orElse(""),
        "lastUpdate"
      ) +
     ") VALUES (" +
      csv(
        sqlValue(tc.id()),
        sqlValue(tc.patient().id().value()),
        sqlValue(tc.doctor().id().value()),
        sqlValue(tc.status()),
        sqlValue(tc.period().start()),
        tc.period().end().map(d -> sqlValue(d)).orElse(""), 
        sqlValue(tc.lastUpdate())
      ) +
      ");";

  }

  private static String updateSQL(TeleConsultation tc){ 

    return "UPDATE teleconsultations SET" +
      csv(
        "status = " + sqlValue(tc.status()),
        "start_ts = " + sqlValue(tc.period().start()),
        tc.period().end().map(e -> "end_ts = " + sqlValue(e)).orElse(""),
        "lastUpdate = " + sqlValue(tc.lastUpdate()) 
      ) +
      " WHERE id = " + sqlValue(tc.id()) + ";";

  }
*/

  private static String insertSQL(TeleConsultation tc){

    var	insert =
      INSERT_INTO("stationsverwaltung")
        .VALUE("id",tc.id().value())
        .VALUE("patient",tc.patient().id().value())
        .VALUE("doctor",tc.doctor().id().value())
        .VALUE("status",tc.status())
        .VALUE("start_ts",tc.period().start())
        .VALUE("lastUpdate",tc.lastUpdate());

     tc.period().end().ifPresent(
       end -> insert.VALUE("end_ts",end)
     );

     return insert.toString();	
  }

  private static String updateSQL(TeleConsultation tc){

    var update =
      UPDATE("teleconsultations")
        .WHERE("id",tc.id().value())
        .SET("status",tc.status())
        .SET("start_ts",tc.period().start())
        .SET("lastUpdate",tc.lastUpdate());

    tc.period().end().ifPresent(
      end -> update.SET("end_ts",end)
    );
     
    return update.toString();
  }


  private static TeleConsultation readTeleConsultation(ResultSet rs) throws SQLException {

    return new TeleConsultation(
      new Id<>(rs.getString("id")),
      Reference.to(rs.getString("patient")),
      Reference.to(rs.getString("doctor")),
      TeleConsultation.Status.valueOf(rs.getString("status")),
      new Period<>(
        rs.getTimestamp("start_ts").toLocalDateTime(),
        Optional.ofNullable(rs.getTimestamp("end_ts")).map(Timestamp::toLocalDateTime)
      ), 
      rs.getTimestamp("lastUpdate").toInstant()
    );

  }

  private static Message readMessage(ResultSet rs) throws SQLException {

    return new Message(
      new Id<>(rs.getString("id")),
      Reference.to(rs.getString("teleconsultation")),
      Reference.to(rs.getString("author")),
      rs.getTimestamp("posted_at").toLocalDateTime(),
      rs.getString("text"),
      Optional.ofNullable(rs.getString("attachment")).map(DECODER::decode),
      rs.getTimestamp("lastUpdate").toInstant()
    );

  }


  @Override
  public Id<TeleConsultation> teleConsultationId(){ 
    return new Id<>(randomUUID().toString());
  }


  @Override
  public void save(TeleConsultation tc) throws Exception {

    try (
      var stmt = conn.createStatement()
    ){

      var sql =
        getTeleConsultation(tc.id()).isPresent() ?
          updateSQL(tc) :
          insertSQL(tc);

      stmt.executeUpdate(sql);

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }


  @Override
  public Optional<TeleConsultation> getTeleConsultation(Id<TeleConsultation> id){

    var sql =
      SELECT("*")
        .FROM("teleconsultations")
	.WHERE("id",id.value());

    try (
      var result =
        conn.createStatement().executeQuery(sql.toString())
    ){
      return
        result.next() ?
          Optional.of(readTeleConsultation(result)) :
          Optional.empty();

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }

  @Override
  public List<TeleConsultation> get(TeleConsultation.Filter filter){

    var query =
      SELECT("*").FROM("teleconsultations");

    filter.patient().ifPresent(
      ref -> query.WHERE("patient",ref.id().value())
    );

    filter.doctor().ifPresent(
      ref -> query.WHERE("doctor",ref.id().value())
    );

    filter.status().ifPresent(
      set -> query.WHERE("status",IN,set)
    );

    try (
      var resultSet =
        conn.createStatement().executeQuery(query.toString())
    ){
      var tcs = new ArrayList<TeleConsultation>();

      while (resultSet.next()){
        tcs.add(readTeleConsultation(resultSet));
      }

      return tcs;

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }




  @Override
  public Id<Message> messageId(){

    return new Id<>(randomUUID().toString());
  }


  private static String insertSQL(Message msg){

    var insert =
      INSERT_INTO("messages")
        .VALUE("id",msg.id().value())
        .VALUE("teleConsultation",msg.teleConsultation().id().value())
        .VALUE("author",msg.author().id().value())
        .VALUE("posted_at",msg.postedAt())
        .VALUE("text",msg.text())
        .VALUE("lastUpdate",msg.lastUpdate());

    msg.attachment().ifPresent(
      bytes -> insert.VALUE("attachment",ENCODER.encodeToString(bytes))
    );

    return insert.toString();

  }

  private static String updateSQL(Message msg){
    var update =
      UPDATE("messages")
        .SET("text",msg.text())
        .SET("lastUpdate",msg.lastUpdate());

    msg.attachment().ifPresent(
      bytes -> update.SET("attachment",ENCODER.encodeToString(bytes))
    );

    return update.WHERE("id",msg.id()).toString();
  }



  @Override
  public void save(Message msg) throws Exception {

    try (
      var stmt = conn.createStatement()
    ){

      var sql =
        getMessage(msg.id()).isPresent() ?
          updateSQL(msg) :
          insertSQL(msg);

      stmt.executeUpdate(sql);

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }

  @Override
  public List<Message> get(Message.Filter filter){

    var query =
      SELECT("*")
        .FROM("messages")
        .WHERE("teleConsultation",filter.teleConsultation().id().value())
        .WHERE(COLUMN("posted_at").greaterThanOrEqual(filter.period().start()))
	.ORDER_BY("posted_at");

    filter.period().end().ifPresent(
      end -> query.WHERE(COLUMN("posted_at").lessThanOrEqual(end))
    );

    try (
      var resultSet =
        conn.createStatement().executeQuery(query.toString())
    ){

      var tcs = new ArrayList<Message>();

      while (resultSet.next()){
        tcs.add(readMessage(resultSet));
      }

      return tcs;

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }

  @Override
  public Optional<Message> getMessage(Id<Message> id){

    try (
      var result =
        conn.createStatement().executeQuery(
          SELECT("*")
            .FROM("messages")
            .WHERE("id",id.value())
            .toString()
        )
    ){
      return
        result.next() ?
          Optional.of(readMessage(result)) :
          Optional.empty();

    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }

  @Override
  public void delete(Id<Message> id){

    try (
      var stmt = conn.createStatement()
    ){
      stmt.executeUpdate(
        DELETE_FROM("messages")
          .WHERE("id",id)
          .toString()
      );
    } catch (SQLException e){
      throw new RuntimeException(e);
    }

  }


@Override
public Id<Ward> WardID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public void save(Ward ward) throws Exception {
	// TODO Auto-generated method stub
	
}


@Override
public List<Ward> get(Filter filter) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Room> get(de.db2.wardmanagement.backend.entity.Room.Filter filter) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Room> get(de.db2.wardmanagement.backend.entity.Bed.Filter filter) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public List<de.db2.wardmanagement.backend.entity.Staff> get(de.db2.wardmanagement.backend.entity.Staff.Filter filter) {
	// TODO Auto-generated method stub
	return null;
}


@Override
public Optional<Ward> getWard(Id<Ward> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}


@Override
public Id<de.db2.wardmanagement.backend.entity.Room> RoomID() {
	// TODO Auto-generated method stub
	return null;
}


@Override
public void save(de.db2.wardmanagement.backend.entity.Room room) throws Exception {
	// TODO Auto-generated method stub
	
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
public void save(de.db2.wardmanagement.backend.entity.Bed bed) throws Exception {
	// TODO Auto-generated method stub
	
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
public void save(de.db2.wardmanagement.backend.entity.Staff staff) throws Exception {
	// TODO Auto-generated method stub
	
}


@Override
public Optional<de.db2.wardmanagement.backend.entity.Staff> Staff(Id<de.db2.wardmanagement.backend.entity.Staff> id) {
	// TODO Auto-generated method stub
	return Optional.empty();
}

}
