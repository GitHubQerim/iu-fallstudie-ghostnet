package com.example.fallstudieghostnet.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Repräsentiert eine Person (meldend oder bergend) im System.
 * Diese Klasse ist eine JPA-Entität und wird auf die Tabelle 'person' gemappt.
 */
@Entity
@Table(name = "person")
public class Person {

    /**
     * Eindeutige ID der Person (Primärschlüssel).
     * Wird von der Datenbank automatisch generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name der Person.
     * Darf nicht null sein. Maximal 150 Zeichen.
     */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Telefonnummer der Person.
     * Kann null sein (z.B. bei anonymer Meldung). Maximal 50 Zeichen.
     */
    @Column(nullable = true, length = 50)
    private String telefonnummer;

    /**
     * Liste der Geisternetze, die von dieser Person gemeldet wurden.
     * Die Beziehung wird über das Feld 'meldendePerson' in der GhostNet-Klasse verwaltet.
     */
    @OneToMany(mappedBy = "meldendePerson", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<GhostNet> gemeldeteNetze = new ArrayList<>();

    /**
     * Liste der Geisternetze, für deren Bergung diese Person aktuell eingetragen ist.
     * Die Beziehung wird über das Feld 'bergendePerson' in der GhostNet-Klasse verwaltet.
     */
    @OneToMany(mappedBy = "bergendePerson", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<GhostNet> zuBergendeNetze = new ArrayList<>();

    /**
     * Standardkonstruktor (parameterlos).
     * Wird von JPA benötigt.
     */
    public Person() {
    }

    // Getter und Setter für alle Felder

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public List<GhostNet> getGemeldeteNetze() {
        return gemeldeteNetze;
    }

    public void setGemeldeteNetze(List<GhostNet> gemeldeteNetze) {
        this.gemeldeteNetze = gemeldeteNetze;
    }

    public List<GhostNet> getZuBergendeNetze() {
        return zuBergendeNetze;
    }

    public void setZuBergendeNetze(List<GhostNet> zuBergendeNetze) {
        this.zuBergendeNetze = zuBergendeNetze;
    }

    /**
     * Fügt ein Netz zur Liste der gemeldeten Netze hinzu und setzt die Rückreferenz.
     * @param netz Das gemeldete Netz.
     */
    public void addGemeldetesNetz(GhostNet netz) {
        if (netz != null) {
            this.gemeldeteNetze.add(netz);
            netz.setMeldendePerson(this);
        }
    }

    /**
     * Entfernt ein Netz aus der Liste der gemeldeten Netze und löst die Rückreferenz auf.
     * @param netz Das zu entfernende Netz.
     */
    public void removeGemeldetesNetz(GhostNet netz) {
        if (netz != null) {
            this.gemeldeteNetze.remove(netz);
            if (netz.getMeldendePerson() == this) {
                netz.setMeldendePerson(null);
            }
        }
    }

    /**
     * Fügt ein Netz zur Liste der zu bergenden Netze hinzu und setzt die Rückreferenz.
     * @param netz Das zu bergende Netz.
     */
    public void addZuBergendesNetz(GhostNet netz) {
        if (netz != null) {
            this.zuBergendeNetze.add(netz);
            netz.setBergendePerson(this);
        }
    }

    /**
     * Entfernt ein Netz aus der Liste der zu bergenden Netze und löst die Rückreferenz auf.
     * @param netz Das nicht mehr zu bergende Netz.
     */
    public void removeZuBergendesNetz(GhostNet netz) {
        if (netz != null) {
            this.zuBergendeNetze.remove(netz);
            if (netz.getBergendePerson() == this) {
                netz.setBergendePerson(null);
            }
        }
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id != null && id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode();
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", telefonnummer='" + telefonnummer + '\'' +
                '}';
    }
}