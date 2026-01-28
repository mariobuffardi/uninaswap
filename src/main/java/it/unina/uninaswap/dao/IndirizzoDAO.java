package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Indirizzo;
import java.util.List;

public interface IndirizzoDAO {

    List<Indirizzo> findByStudente(String matricolaStudente) throws Exception;

}