package com.dif.orarioauledmi;

/**
 * Created by franz on 09/07/14.
 */
public class Lezione {
    String descrizione;
    String professore;
    String orario;

    public Lezione(String descrizione,String professore, String orario){
    super();
    this.descrizione=descrizione;
    this.professore=professore;
    this.orario=orario;
    }
    public String getDescrizione(){
        return this.descrizione;
    }
    public String getProfessore(){
        return this.professore;
    }
    public String getOrario(){
        return this.orario;
    }
    
}