package lk.jiat.envy.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@NamedQuery(name = "Status.findByName" , query = "FROM Status s WHERE s.name=:name")
public class Status implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 15, nullable = false, unique = true)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static enum Type {
        ACTIVE,
        PENDING,
        INACTIVE,
        BLOCKED,
        DELIVERED,
        PACKING,
        APPROVED,
        REJECTED,
        CANCELED,
        VERIFIED,
        RECEIVED,
        COMPLETED,
        PAID,
        PAYMENT_FAILED,
        IN_STOCK,
        OUT_OF_STOCK
    }
}
