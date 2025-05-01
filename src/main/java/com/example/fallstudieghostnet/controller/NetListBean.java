package com.example.fallstudieghostnet.controller;

import com.example.fallstudieghostnet.model.GhostNet;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.faces.context.FacesContext;
import java.util.Collections;
import java.util.List;
import jakarta.transaction.Transactional;
import java.util.Map;
import com.example.fallstudieghostnet.model.Person;
/**
 * Backing Bean für die JSF-Seite list_nets.xhtml.
 * Lädt und stellt die Liste der Geisternetze bereit, die geborgen werden müssen (Status 'Gemeldet').
 */
@Named
@RequestScoped
public class NetListBean {

    /**
     * Der EntityManager, der von CDI/Container injected wird.
     * Wird für alle Datenbankoperationen verwendet.
     */
    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    /**
     * Die Liste der GhostNet-Objekte, die den Status 'Gemeldet' haben.
     * Wird von der loadNets()-Methode befüllt.
     */
    private List<GhostNet> netsToRecover;

    /**
     * Initialisierungsmethode, die nach der Bean-Erstellung aufgerufen wird (@PostConstruct).
     * Führt die Datenbankabfrage aus, um die relevanten Netze zu laden.
     */
    @PostConstruct
    public void loadNets() {
        System.out.println("Lade Netze zum Bergen...");
        try {

            String jpql = "SELECT gn FROM GhostNet gn " +
                    "LEFT JOIN FETCH gn.meldendePerson " +
                    "LEFT JOIN FETCH gn.bergendePerson " +
                    "WHERE gn.status = :status ORDER BY gn.id DESC";

            TypedQuery<GhostNet> query = em.createQuery(jpql, GhostNet.class);
            query.setParameter("status", "Gemeldet");
            this.netsToRecover = query.getResultList();
            System.out.println("Anzahl gefundener Netze: " + (this.netsToRecover != null ? this.netsToRecover.size() : 0));

        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Netze: " + e.getMessage());
            e.printStackTrace();
            this.netsToRecover = Collections.emptyList();
        }
    }
    /**
     * Action-Methode, um die Bergung eines Netzes einer Person zuzuweisen.
     * Wird vom "Bergung übernehmen"-Button aufgerufen.
     *
     * @return Outcome-String für Navigation (zurück zur Liste).
     */
    @Transactional
    public String assignRecovery() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String netIdParam = params.get("netIdToAssign");
        Long netId = null;

        if (netIdParam != null && !netIdParam.isEmpty()) {
            try {
                netId = Long.parseLong(netIdParam);
                System.out.println("Versuche Netz mit ID " + netId + " zuzuweisen.");
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Netz-ID empfangen: " + netIdParam);
                return null;
            }
        }

        if (netId == null) {
            System.err.println("Keine Netz-ID für Zuweisung empfangen.");
            return null;
        }

        try {
            GhostNet net = em.find(GhostNet.class, netId);

            if (net == null) {
                System.err.println("Netz mit ID " + netId + " nicht gefunden.");
                return null;
            }

            if (net.getBergendePerson() != null) {
                System.out.println("Netz mit ID " + netId + " ist bereits zugewiesen an: " + net.getBergendePerson().getName());

                return null;
            }

            Person currentUser = em.find(Person.class, 1L);
            if (currentUser == null) {
                System.err.println("Test-Benutzer (Person ID 1) nicht gefunden! Zuweisung nicht möglich.");
                return null;
            }
            System.out.println("Weise Netz der Person zu: " + currentUser.getName());
            net.setBergendePerson(currentUser);
            System.out.println("Netz mit ID " + netId + " erfolgreich Person ID " + currentUser.getId() + " zugewiesen.");

            return "list_nets?faces-redirect=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Zuweisen der Bergung für Netz ID " + netId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Action-Methode, um ein Netz als geborgen zu markieren.
     * Wird vom "Als geborgen melden"-Button aufgerufen.
     *
     * @return Outcome-String für Navigation (zurück zur Liste).
     */
    @Transactional
    public String markAsRecovered() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String netIdParam = params.get("netIdToRecover");
        Long netId = null;

        if (netIdParam != null && !netIdParam.isEmpty()) {
            try {
                netId = Long.parseLong(netIdParam);
                System.out.println("Versuche Netz mit ID " + netId + " als geborgen zu markieren.");
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Netz-ID empfangen: " + netIdParam);
                return null;
            }
        }

        if (netId == null) {
            System.err.println("Keine Netz-ID für Bergungsmeldung empfangen.");

            return null;
        }

        try {

            GhostNet net = em.find(GhostNet.class, netId);

            if (net == null) {
                System.err.println("Netz mit ID " + netId + " nicht gefunden.");

                return null;
            }

            net.setStatus("Geborgen");
            System.out.println("Status für Netz mit ID " + netId + " auf 'Geborgen' gesetzt.");

            return "list_nets?faces-redirect=true";

        } catch (Exception e) {
            System.err.println("Fehler beim Markieren als geborgen für Netz ID " + netId + ": " + e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    public List<GhostNet> getNetsToRecover() {
        return netsToRecover != null ? netsToRecover : Collections.emptyList();
    }
}