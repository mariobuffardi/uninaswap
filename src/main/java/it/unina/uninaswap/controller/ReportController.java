package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.OffertaDAO;
import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.enums.TipoAnnuncio;
import it.unina.uninaswap.model.enums.TipoCategoria;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.ReportView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class ReportController {

    private final AnnunciMainView view;
    private final OffertaDAO offertaDAO;
    private final TransazioneDAO transazioneDAO;
    private final Studente loggedIn;

    public ReportController(AnnunciMainView view, OffertaDAO offertaDAO, TransazioneDAO transazioneDAO, Studente loggedIn) {
        this.view = view;
        this.offertaDAO = offertaDAO;
        this.transazioneDAO = transazioneDAO;
        this.loggedIn = loggedIn;
    }

    public void refreshReportView() {
        ReportView reportView = view.getReportView();
        if (reportView == null)
            return;

        try {
            ReportView.ReportData data = new ReportView.ReportData();


            for (TipoCategoria cat : TipoCategoria.values()) {
                int arrivate = offertaDAO.countOfferteArrivatePerCategoria(loggedIn.getMatricola(), cat);
                int accettate = offertaDAO.countVenditeAccettatePerCategoria(loggedIn.getMatricola(), cat);

                data.venditePerCategoria.putArrivate(cat, arrivate);
                data.venditePerCategoria.putAccettate(cat, accettate);
            }
            data.venditePerCategoria.computeTotals();

            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                int arrivate = offertaDAO.countOfferteArrivatePerTipologia(loggedIn.getMatricola(), tipo);
                int accettate = offertaDAO.countVenditeAccettatePerTipologia(loggedIn.getMatricola(), tipo);

                data.venditePerTipologia.putArrivate(tipo, arrivate);
                data.venditePerTipologia.putAccettate(tipo, accettate);
            }
            data.venditePerTipologia.computeTotals();


            for (TipoCategoria cat : TipoCategoria.values()) {
                int inviate = offertaDAO.countOfferteInviatePerCategoria(loggedIn.getMatricola(), cat);
                int accettate = offertaDAO.countAcquistiAccettatiPerCategoria(loggedIn.getMatricola(), cat);

                data.acquistiPerCategoria.putArrivate(cat, inviate); // "arrivate" = "inviate" in questa sezione
                data.acquistiPerCategoria.putAccettate(cat, accettate);
            }
            data.acquistiPerCategoria.computeTotals();

            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                int inviate = offertaDAO.countOfferteInviatePerTipologia(loggedIn.getMatricola(), tipo);
                int accettate = offertaDAO.countAcquistiAccettatiPerTipologia(loggedIn.getMatricola(), tipo);

                data.acquistiPerTipologia.putArrivate(tipo, inviate);
                data.acquistiPerTipologia.putAccettate(tipo, accettate);
            }
            data.acquistiPerTipologia.computeTotals();


            data.mediaVendite = transazioneDAO.getMediaImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());
            data.minVendite = transazioneDAO.getMinImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());
            data.maxVendite = transazioneDAO.getMaxImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());

            data.mediaAcquisti = transazioneDAO.getMediaImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());
            data.minAcquisti = transazioneDAO.getMinImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());
            data.maxAcquisti = transazioneDAO.getMaxImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());


            DefaultCategoryDataset dsVenditeCat = new DefaultCategoryDataset();
            for (TipoCategoria cat : TipoCategoria.values()) {
                dsVenditeCat.addValue(data.venditePerCategoria.getArrivate(cat), "Offerte arrivate", cat.toString());
                dsVenditeCat.addValue(data.venditePerCategoria.getAccettate(cat), "Accettate", cat.toString());
            }
            JFreeChart chartVenditeCat = ChartFactory.createBarChart(
                    "Vendite - per categoria", "Categoria", "Numero",
                    dsVenditeCat, PlotOrientation.VERTICAL, true, true, false);

            DefaultCategoryDataset dsVenditeTipo = new DefaultCategoryDataset();
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                dsVenditeTipo.addValue(data.venditePerTipologia.getArrivate(tipo), "Offerte arrivate", tipo.toString());
                dsVenditeTipo.addValue(data.venditePerTipologia.getAccettate(tipo), "Accettate", tipo.toString());
            }
            JFreeChart chartVenditeTipo = ChartFactory.createBarChart(
                    "Vendite - per tipologia", "Tipologia", "Numero",
                    dsVenditeTipo, PlotOrientation.VERTICAL, true, true, false);

            DefaultCategoryDataset dsAcquistiCat = new DefaultCategoryDataset();
            for (TipoCategoria cat : TipoCategoria.values()) {
                dsAcquistiCat.addValue(data.acquistiPerCategoria.getArrivate(cat), "Offerte inviate", cat.toString());
                dsAcquistiCat.addValue(data.acquistiPerCategoria.getAccettate(cat), "Accettate", cat.toString());
            }
            JFreeChart chartAcquistiCat = ChartFactory.createBarChart(
                    "Acquisti - per categoria", "Categoria", "Numero",
                    dsAcquistiCat, PlotOrientation.VERTICAL, true, true, false);

            DefaultCategoryDataset dsAcquistiTipo = new DefaultCategoryDataset();
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                dsAcquistiTipo.addValue(data.acquistiPerTipologia.getArrivate(tipo), "Offerte inviate",
                        tipo.toString());
                dsAcquistiTipo.addValue(data.acquistiPerTipologia.getAccettate(tipo), "Accettate", tipo.toString());
            }
            JFreeChart chartAcquistiTipo = ChartFactory.createBarChart(
                    "Acquisti - per tipologia", "Tipologia", "Numero",
                    dsAcquistiTipo, PlotOrientation.VERTICAL, true, true, false);

            reportView.setData(data);
            reportView.setVenditeChartCategoria(chartVenditeCat);
            reportView.setVenditeChartTipologia(chartVenditeTipo);
            reportView.setAcquistiChartCategoria(chartAcquistiCat);
            reportView.setAcquistiChartTipologia(chartAcquistiTipo);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento report:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}