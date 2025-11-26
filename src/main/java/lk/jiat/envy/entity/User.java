package lk.jiat.envy.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NamedQuery(name = "user.getByEmail" , query = "FROM User u WHERE u.email=:email")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", length = 45, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 45, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 15, nullable = false)
    private String password;

    @Column(name = "verification_code", nullable = false, length = 15)
    private String verificationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Order> orders = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Cart> carts = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Set<Cart> getCarts() {
        return carts;
    }
}
