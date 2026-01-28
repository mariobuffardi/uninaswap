package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.FotoDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Foto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FotoDAOImpl implements FotoDAO {

    @Override
    public List<Foto> findByAnnuncio(int idAnnuncio) throws Exception {
        String sql = "SELECT id, path, is_principale, id_annuncio " +
                     "FROM foto WHERE id_annuncio = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnnuncio);

            List<Foto> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Foto f = new Foto();
                    f.setId(rs.getInt("id"));
                    f.setPath(rs.getString("path"));
                    f.setPrincipale(rs.getBoolean("is_principale"));
                    f.setIdAnnuncio(rs.getInt("id_annuncio"));
                    result.add(f);
                }
            }
            return result;
        }
    }

    @Override
    public Foto findPrincipaleOrFirstByAnnuncio(int idAnnuncio) throws Exception {
        // 1) Provo principale
        String sqlMain = "SELECT id, path, is_principale, id_annuncio " +
                "FROM foto WHERE id_annuncio = ? AND is_principale = TRUE " +
                "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMain)) {

            ps.setInt(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Foto f = new Foto();
                    f.setId(rs.getInt("id"));
                    f.setPath(rs.getString("path"));
                    f.setPrincipale(rs.getBoolean("is_principale"));
                    f.setIdAnnuncio(rs.getInt("id_annuncio"));
                    return f;
                }
            }
        }

        // 2) Fallback: prima foto disponibile
        String sqlFirst = "SELECT id, path, is_principale, id_annuncio " +
                "FROM foto WHERE id_annuncio = ? " +
                "ORDER BY id ASC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlFirst)) {

            ps.setInt(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Foto f = new Foto();
                    f.setId(rs.getInt("id"));
                    f.setPath(rs.getString("path"));
                    f.setPrincipale(rs.getBoolean("is_principale"));
                    f.setIdAnnuncio(rs.getInt("id_annuncio"));
                    return f;
                }
            }
        }

        return null;
    }

    @Override
    public void deleteByAnnuncio(int idAnnuncio) throws Exception {
        String sql = "DELETE FROM foto WHERE id_annuncio = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnnuncio);
            ps.executeUpdate();
        }
    }

    @Override
    public void insert(Foto foto) throws Exception {
        String sql = "INSERT INTO foto (path, is_principale, id_annuncio) " +
                     "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, foto.getPath());
            ps.setBoolean(2, foto.isPrincipale());
            ps.setInt(3, foto.getIdAnnuncio());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    foto.setId(rs.getInt(1));
                }
            }
        }
    }
}