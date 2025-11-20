package lk.jiat.envy.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Storage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 45, nullable = false)
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return value;
    }

    public void setName(String name) {
        this.value = name;
    }
}
