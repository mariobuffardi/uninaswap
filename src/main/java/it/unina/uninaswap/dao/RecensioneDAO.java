package it.unina.uninaswap.dao;

import java.util.List;

import it.unina.uninaswap.model.entity.Recensione;

public interface RecensioneDAO {

    List<Recensione> findRicevuteByStudente(String matricolaRecensito) throws Exception;

    boolean existsByTransazioneAndAutore(int idTransazione, String autore) throws Exception;

    void insert(Recensione recensione) throws Exception;
}