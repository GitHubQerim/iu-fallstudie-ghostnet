package com.example.fallstudieghostnet.controller;

import com.example.fallstudieghostnet.model.GhostNet;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

/**
 * Backing Bean für die JSF-Seite assigned_nets.xhtml.
 * Lädt und stellt die Liste der Geisternetze bereit, die bereits einer Person
 * zur Bergung zugewiesen wurden.
 */
@Named
@RequestScoped
public class AssignedNetListBean {

    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    /**
     * Die Liste der GhostNet-Objekte, die einer Person zur Bergung zugewiesen sind.
     */
    private List<GhostNet> assignedNets;

    /**
     * Lädt die zugewiesenen Netze aus der Datenbank nach Bean-Erstellung.
     */
    @PostConstruct
    public void loadAssignedNets() {
        System.out.println("Lade zugewiesene Netze...");
        try {
            String jpql = "SELECT gn FROM GhostNet gn JOIN FETCH gn.bergendePerson WHERE gn.bergendePerson IS NOT NULL ORDER BY gn.id DESC";

            TypedQuery<GhostNet> query = em.createQuery(jpql, GhostNet.class);
            this.assignedNets = query.getResultList();

            System.out.println("Anzahl gefundener zugewiesener Netze: " + (this.assignedNets != null ? this.assignedNets.size() : 0));

        } catch (Exception e) {
            System.err.println("Fehler beim Laden der zugewiesenen Netze: " + e.getMessage());
            e.printStackTrace();
            this.assignedNets = Collections.emptyList();
        }
    }

    /**
     * Getter für die Liste der zugewiesenen Netze.
     * Wird von der JSF-Seite aufgerufen.
     *
     * @return Liste der zugewiesenen GhostNet-Objekte.
     */
    public List<GhostNet> getAssignedNets() {
        return assignedNets != null ? assignedNets : Collections.emptyList();
    }
}