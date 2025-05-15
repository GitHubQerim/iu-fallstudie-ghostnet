package com.example.fallstudieghostnet.controller;

import com.example.fallstudieghostnet.model.GhostNet;
import com.example.fallstudieghostnet.model.Person;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Backing Bean für die JSF-Seite {@code assigned_nets.xhtml}.
 * Diese Bean ist verantwortlich für das Laden und Anzeigen von Geisternetzen,
 * die bereits einer Person zur Bergung zugewiesen wurden. Sie bietet zudem
 * die Funktionalität, eine solche Zuweisung wieder aufzuheben (Freigabe der Bergung).
 * Die Bean ist {@link RequestScoped}.
 *
 * @author IhrName // Bitte anpassen
 * @version 1.2
 */
@Named
@RequestScoped
public class AssignedNetListBean {

    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    private List<GhostNet> assignedNets;

    /**
     * Initialisiert die Bean nach der Konstruktion und Injektion von Abhängigkeiten.
     * Lädt die Liste der zugewiesenen Geisternetze aus der Datenbank.
     * Die Netze werden absteigend nach ihrer ID sortiert und die zugehörigen
     * {@code bergendePerson}- sowie {@code meldendePerson}-Entitäten werden
     * mittels {@code JOIN FETCH} effizient mitgeladen, um N+1 Select-Problemen vorzubeugen.
     * Fehlermeldungen werden über {@link FacesContext} an die UI weitergegeben.
     */
    @PostConstruct
    public void loadAssignedNets() {
        System.out.println("Lade zugewiesene Netze...");
        try {
            String jpql = "SELECT gn FROM GhostNet gn " +
                    "JOIN FETCH gn.bergendePerson " +
                    "LEFT JOIN FETCH gn.meldendePerson " +
                    "WHERE gn.bergendePerson IS NOT NULL " +
                    "ORDER BY gn.id DESC";

            TypedQuery<GhostNet> query = em.createQuery(jpql, GhostNet.class);
            this.assignedNets = query.getResultList();
            System.out.println("Anzahl gefundener zugewiesener Netze: " + (this.assignedNets != null ? this.assignedNets.size() : 0));
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der zugewiesenen Netze: " + e.getMessage());
            e.printStackTrace();
            this.assignedNets = Collections.emptyList();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Laden", "Die zugewiesenen Netze konnten nicht geladen werden."));
        }
    }

    /**
     * Hebt die Zuweisung eines Geisternetzes zu einer bergenden Person auf.
     * Diese Methode ist {@link Transactional}, um die Datenkonsistenz zu gewährleisten.
     * Sie erwartet die ID des freizugebenden Netzes als Request-Parameter ("netIdToRelease").
     * Nach erfolgreicher Freigabe wird die {@code bergendePerson} des Netzes auf {@code null}
     * gesetzt, und das Netz ist wieder für andere Bergungsversuche verfügbar.
     * Der Status des Netzes bleibt unverändert (typischerweise "Gemeldet").
     * Feedback über Erfolg oder Misserfolg wird dem Benutzer über {@link FacesMessage} angezeigt.
     *
     * @return Ein JSF-Outcome-String. Bei Erfolg wird zur aktualisierten Ansicht
     * {@code assigned_nets.xhtml} navigiert. Bei Fehlern wird {@code null} zurückgegeben,
     * um auf der aktuellen Seite zu bleiben.
     */
    @Transactional
    public String releaseRecovery() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String netIdParam = params.get("netIdToRelease");
        Long netId = null;

        if (netIdParam != null && !netIdParam.isEmpty()) {
            try {
                netId = Long.parseLong(netIdParam);
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Netz-ID für Freigabe empfangen: " + netIdParam);
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ungültige ID", "Die übermittelte Netz-ID ist nicht korrekt."));
                return null;
            }
        }

        if (netId == null) {
            System.err.println("Keine Netz-ID für Freigabe empfangen.");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fehlende ID", "Es wurde keine Netz-ID für die Freigabe übermittelt."));
            return null;
        }

        try {
            GhostNet net = em.find(GhostNet.class, netId);

            if (net == null) {
                System.err.println("Netz mit ID " + netId + " für Freigabe nicht gefunden.");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nicht gefunden", "Das Netz mit der ID " + netId + " konnte nicht gefunden werden."));
                return null;
            }

            if (net.getBergendePerson() == null) {
                System.out.println("Netz mit ID " + netId + " ist bereits keiner Person zugewiesen.");
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bereits freigegeben", "Das Netz war nicht zugewiesen."));
                loadAssignedNets();
                return "assigned_nets?faces-redirect=true";
            }

            net.setBergendePerson(null);
            em.merge(net);
            System.out.println("Bergung für Netz ID " + netId + " erfolgreich freigegeben.");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Erfolg", "Die Bergung für Netz ID " + netId + " wurde erfolgreich freigegeben."));
            loadAssignedNets();
            return "assigned_nets?faces-redirect=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Freigeben der Bergung für Netz ID " + netId + ": " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Systemfehler", "Ein unerwarteter Fehler ist beim Freigeben der Bergung aufgetreten."));
            return null;
        }
    }

    public List<GhostNet> getAssignedNets() {
        return assignedNets != null ? assignedNets : Collections.emptyList();
    }
}