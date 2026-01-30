package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Studente;
import java.sql.Connection;

public interface StudenteDAO {
	
	// LOGIN
	Studente findByEmailAndPassword(String emali, String password) throws Exception;
    
	Studente findByMatricola(String matricola) throws Exception;
    
    void update(Studente studente) throws Exception;
    
    boolean existsByEmail(String email) throws Exception;
    
    String insertAndReturnMatricola(Studente s) throws Exception;
    
    String insertAndReturnMatricola(Studente s, Connection conn) throws Exception;
}