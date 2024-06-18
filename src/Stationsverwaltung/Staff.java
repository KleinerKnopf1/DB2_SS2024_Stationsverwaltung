package Stationsverwaltung;

import java.time.LocalDateTime;

public record Staff
(
  Id<Staff> id,
  String prename,
  String name,
  LocalDateTime birthday,
  String function
)
{



}