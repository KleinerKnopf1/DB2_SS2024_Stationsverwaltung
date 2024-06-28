package de.db2.wardmanagement.data;


import java.util.ServiceLoader;

import de.db2.wardmanagement.backend.entity.Patient;
import de.db2.wardmanagement.backend.type.Reference;

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
