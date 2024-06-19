package de.db2.wardmanagement;


import java.util.ServiceLoader;

import de.db2.wardmanagement.entities.Patient;
import de.db2.wardmanagement.type.Reference;

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
