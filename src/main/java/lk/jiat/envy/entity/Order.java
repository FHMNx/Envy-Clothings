package lk.jiat.envy.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "delivery_line_one", length = 50)
    private String deliveryLineOne;

    @Column(name = "delivery_line_two", length = 50)
    private String deliveryLineTwo;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(length = 10, nullable = false)
    private String mobile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String note;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_types_id")
    private DeliveryType delivery_type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paymentType_id")
    private PaymentType paymentType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> order_items = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DeliveryType getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(DeliveryType delivery_type) {
        this.delivery_type = delivery_type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrder_items() {
        return order_items;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getDeliveryLineOne() {
        return deliveryLineOne;
    }

    public void setDeliveryLineOne(String deliveryLineOne) {
        this.deliveryLineOne = deliveryLineOne;
    }

    public String getDeliveryLineTwo() {
        return deliveryLineTwo;
    }

    public void setDeliveryLineTwo(String deliveryLineTwo) {
        this.deliveryLineTwo = deliveryLineTwo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
