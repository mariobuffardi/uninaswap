package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Annuncio;

import java.math.BigDecimal;
import java.util.List;

public interface AnnuncioDAO {

	List<Annuncio> findAttivi( String search, String categoria, String tipologia, BigDecimal prezzoMin, BigDecimal prezzoMax, Boolean offreSpedizione, Boolean offreInUni) throws Exception;
	
	List<Annuncio> findAttiviEsclusoStudente(String matricola, String search, String categoria, String tipologia, BigDecimal prezzoMin, BigDecimal prezzoMax, Boolean offreSpedizione, Boolean offreInUni)throws Exception;

	Annuncio findById(int id) throws Exception;

	List<Annuncio> findByStudente(String matricolaStudente) throws Exception;

	// ultimi N annunci dello studente (per preview profilo)
	List<Annuncio> findUltimiByStudente(String matricolaStudente, int limit) throws Exception;

	void update(Annuncio annuncio) throws Exception;
	
	void insert(Annuncio annuncio) throws Exception;
	
    void setConcluso(int idAnnuncio, boolean concluso) throws Exception;
    
    boolean delete(int idAnnuncio, String matricolaVenditore) throws Exception;

}