package com.example.fallstudieghostnet.controller;

import com.example.fallstudieghostnet.model.GhostNet;
import com.example.fallstudieghostnet.model.Person;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

import jakarta.inject.Named;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;

/**
 * Backing Bean für die JSF-Seite report_net.xhtml.
 * Verantwortlich für die Entgegennahme der Formulardaten zur Meldung eines neuen Geisternetzes
 * und die Initiierung des Speicherprozesses.
 */
@Named
@RequestScoped
public class ReportNetBean {

    @PersistenceContext(unitName = "ghostnetPU")
    private EntityManager em;

    /**
     * Das neue GhostNet-Objekt, das über das Formular befüllt wird.
     */
    private GhostNet newNet;

    /**
     * Das Person-Objekt für die meldende Person, das über das Formular befüllt wird.
     */
    private Person reporter;

    /**
     * Initialisierungsmethode, die nach der Bean-Erstellung aufgerufen wird.
     * Erstellt leere Objekte für das Formular-Binding.
     */
    @PostConstruct
    public void init() {
        newNet = new GhostNet();
        reporter = new Person();
        // Standard-Status setzen
        newNet.setStatus("Gemeldet");
    }

    /**
     * Action-Methode, die aufgerufen wird, wenn der "Netz melden"-Button geklickt wird.
     * Validiert die Eingaben (rudimentär) und speichert das neue Netz und ggf.
     * die meldende Person in der Datenbank via JPA.
     * Läuft innerhalb einer Transaktion.
     *
     * @return Der Outcome-String für die JSF-Navigation (Weiterleitung zur Startseite).
     */
    @Transactional // Sorgt dafür, dass die Methode in einer JTA-Transaktion läuft
    public String saveNet() {
        System.out.println("Versuche Netz zu speichern...");
        System.out.println("Netzdaten: " + newNet);
        System.out.println("Melderdaten: " + reporter);

        try {
            Person meldendePerson = null;
            if (reporter != null && reporter.getName() != null && !reporter.getName().trim().isEmpty()) {
                em.persist(reporter);
                meldendePerson = reporter;
                System.out.println("Meldende Person gespeichert/verwendet: ID " + meldendePerson.getId());
            } else {
                System.out.println("Anonyme Meldung.");
            }


            newNet.setMeldendePerson(meldendePerson);


            newNet.setStatus("Gemeldet");


            em.persist(newNet);
            System.out.println("Geisternetz gespeichert: ID " + newNet.getId());

            return "index?faces-redirect=true";

        } catch (Exception e) {

            System.err.println("Fehler beim Speichern des Netzes: " + e.getMessage());
            e.printStackTrace();


            return null;
        }
    }

    public GhostNet getNewNet() {
        return newNet;
    }

    public void setNewNet(GhostNet newNet) {
        this.newNet = newNet;
    }

    public Person getReporter() {
        return reporter;
    }

    public void setReporter(Person reporter) {
        this.reporter = reporter;
    }
}