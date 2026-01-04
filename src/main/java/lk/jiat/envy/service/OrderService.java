package lk.jiat.envy.service;

import lk.jiat.envy.dto.CheckoutRequestDTO;
import lk.jiat.envy.entity.*;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {

    public Order createOrder(User user, CheckoutRequestDTO dto, PaymentType paymentType, DeliveryType deliveryType, Status status,
                             Address billingAddress, Session hibernateSession) {

        Order order = new Order();
        order.setUser(user);
        order.setStatus(status);
        order.setPaymentType(paymentType);
        order.setDelivery_type(deliveryType);
        order.setNote(dto.getNote());
        order.setUpdatedAt(LocalDateTime.now());


        if (dto.isCurrentAddress()) {
            order.setDeliveryLineOne(billingAddress.getLineOne());
            order.setDeliveryLineTwo(billingAddress.getLineTwo());
            order.setPostalCode(billingAddress.getPostalCode());
            order.setMobile(billingAddress.getMobile());
            order.setCity(billingAddress.getCity());
        } else {
            order.setDeliveryLineOne(dto.getLineOne());
            order.setDeliveryLineTwo(dto.getLineTwo());
            order.setPostalCode(dto.getPostalCode());
            order.setMobile(dto.getMobile());
            order.setCity(hibernateSession.find(City.class, dto.getCityId()));
        }

        order.setNote(dto.getNote());
        hibernateSession.persist(order);

        List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user = :user", Cart.class)
                .setParameter("user", user)
                .getResultList();

        for (Cart cart : cartList) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setStock(cart.getStock());
            item.setQuantity(cart.getQuantity());

            hibernateSession.persist(item);
            hibernateSession.remove(cart);
        }

        return order;
    }

}
