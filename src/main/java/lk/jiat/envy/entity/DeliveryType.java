package lk.jiat.envy.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "delivery_types")
public class DeliveryType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public enum Value {
        WITHIN_KANDY, OUT_OF_KANDY
    }
}
