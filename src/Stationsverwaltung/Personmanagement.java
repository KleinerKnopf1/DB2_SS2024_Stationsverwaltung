package Stationsverwaltung;


import java.util.ServiceLoader;

public interface Personmanagement
{

  boolean patientExists(Reference<Patient> ref);



  public static interface Provider
  { 
	  Personmanagement instance();
  }

  public static Personmanagement loadInstance(){
    return
      ServiceLoader.load(Provider.class)
        .iterator()
        .next()
        .instance();
  }

}
