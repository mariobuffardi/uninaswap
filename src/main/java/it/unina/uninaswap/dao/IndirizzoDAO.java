package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Indirizzo;
import java.util.List;
import java.sql.Connection;

public interface IndirizzoDAO {

    List<Indirizzo> findByStudente(String matricolaStudente) throws Exception;
    
    void insert(Indirizzo i) throws Exception;

    void insert(Indirizzo i, Connection conn) throws Exception;


}