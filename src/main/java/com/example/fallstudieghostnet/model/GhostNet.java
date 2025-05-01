package com.example.fallstudieghostnet.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Repräsentiert ein gemeldetes Geisternetz in der Datenbank.
 * Diese Klasse ist eine JPA-Entität und wird auf die Tabelle 'ghost_net' gemappt.
 */
@Entity
@Table(name = "ghost_net")
public class GhostNet {

    /**
     * Eindeutige ID des Geisternetzes (Primärschlüssel).
     * Wird von der Datenbank automatisch generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Standort des Netzes (z.B. als GPS-Koordinaten-String oder Beschreibung).
     * Maximal 255 Zeichen.
     */
    @Column(name = "location", length = 255)
    private String standort;

    /**
     * Geschätzte Größe des Netzes (z.B. "klein", "mittel", "groß" oder "5x10m").
     * Maximal 100 Zeichen.
     */
    @Column(name = "size_estimate", length = 100)
    private String groesse;

    /**
     * Aktueller Status des Netzes (z.B. "Gemeldet", "Bergung bevorstehend", "Geborgen", "Verschollen").
     * Maximal 50 Zeichen.
     */
    @Column(name = "status", length = 50, nullable = false)
    private String status;

    /**
     * Die Person, die dieses Netz gemeldet hat.
     * Kann null sein, falls die Meldung anonym erfolgte.
     * Verweist auf die Spalte 'meldende_person_id' in dieser Tabelle.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meldende_person_id")
    private Person meldendePerson;

    /**
     * Die Person, die aktuell für die Bergung dieses Netzes eingetragen ist.
     * Kann null sein, falls noch keine Person zugewiesen ist.
     * Verweist auf die Spalte 'bergende_person_id' in dieser Tabelle.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bergende_person_id")
    private Person bergendePerson;

    /**
     * Standardkonstruktor (parameterlos).
     * Wird von JPA benötigt.
     */
    public GhostNet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public String getGroesse() {
        return groesse;
    }

    public void setGroesse(String groesse) {
        this.groesse = groesse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Person getMeldendePerson() {
        return meldendePerson;
    }

    public void setMeldendePerson(Person meldendePerson) {
        this.meldendePerson = meldendePerson;
    }

    public Person getBergendePerson() {
        return bergendePerson;
    }

    public void setBergendePerson(Person bergendePerson) {
        this.bergendePerson = bergendePerson;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GhostNet ghostNet = (GhostNet) o;
        return id != null && id.equals(ghostNet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode();
    }

    @Override
    public String toString() {
        return "GhostNet{" +
                "id=" + id +
                ", standort='" + standort + '\'' +
                ", groesse='" + groesse + '\'' +
                ", status='" + status + '\'' +
                ", meldendePersonId=" + (meldendePerson != null ? meldendePerson.getId() : "null") +
                ", bergendePersonId=" + (bergendePerson != null ? bergendePerson.getId() : "null") +
                '}';
    }
}