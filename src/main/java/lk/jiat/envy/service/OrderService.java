package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.CheckoutRequestDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
        order.setCreatedAt(LocalDateTime.now());
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


    public Order createPendingOrder(Session hibernateSession, User user, CheckoutRequestDTO requestDTO, PaymentType paymentType,
                                    DeliveryType deliveryType, Status status, Address billingAddress, City city) {

        Order order = new Order();
        order.setUser(user);
        order.setPaymentType(paymentType);
        order.setDelivery_type(deliveryType);
        order.setStatus(status);
        order.setNote(requestDTO.getNote());
        order.setUpdatedAt(LocalDateTime.now());

        if (requestDTO.isCurrentAddress()) {
            order.setDeliveryLineOne(billingAddress.getLineOne());
            order.setDeliveryLineTwo(billingAddress.getLineTwo());
            order.setPostalCode(billingAddress.getPostalCode());
            order.setMobile(billingAddress.getMobile());
            order.setCity(billingAddress.getCity());
        } else {
            order.setDeliveryLineOne(requestDTO.getLineOne());
            order.setDeliveryLineTwo(requestDTO.getLineTwo());
            order.setPostalCode(requestDTO.getPostalCode());
            order.setMobile(requestDTO.getMobile());
            order.setCity(city);
        }

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
        }

        hibernateSession.flush();

        return order;
    }

    public void completeOrder(String tempOrderId) {

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {

                Order order = hibernateSession.createQuery("FROM Order o WHERE o.tempOrderId = :tempId", Order.class).
                        setParameter("tempId", tempOrderId)
                        .uniqueResult();

                if (order == null) {
                    throw new RuntimeException("Order not found for tempOrderId: " + tempOrderId);
                }

                Status inStockStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", Status.Type.IN_STOCK.name())
                        .getSingleResult();

                Status outOfStockStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", Status.Type.OUT_OF_STOCK.name())
                        .getSingleResult();

                for (OrderItem orderItem : order.getOrder_items()) {
                    Stock stock = orderItem.getStock();

                    int updateQty = stock.getQuantity() - orderItem.getQuantity();
                    stock.setQuantity(updateQty);

                    if (updateQty <= 0) {
                        stock.setStatus(outOfStockStatus);
                    } else {
                        stock.setStatus(inStockStatus);
                    }

                    hibernateSession.merge(stock);
                }

                Status paidStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", Status.Type.PAID.name())
                        .getSingleResult();

                order.setStatus(paidStatus);
                hibernateSession.merge(order);

                hibernateSession.createQuery("DELETE FROM Cart c WHERE c.user = :user").
                        setParameter("user", order.getUser())
                        .executeUpdate();

                transaction.commit();
                System.out.println("Order marked as PAID: " + tempOrderId);

            } catch (HibernateException e) {
                transaction.rollback();
                throw new RuntimeException();
            }
        }
    }

    public void failOrder(String tempOrderId) {
        System.out.println("Payment failed for temp order: " + tempOrderId);

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                // Find the pending order by tempOrderId
                Order order = hibernateSession.createQuery("FROM Order o WHERE o.tempOrderId = :tempId", Order.class)
                        .setParameter("tempId", tempOrderId)
                        .uniqueResult();

                if (order != null) {

                    Status failedStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                            .setParameter("name", Status.Type.PAYMENT_FAILED.name())
                            .getSingleResult();

                    order.setStatus(failedStatus);
                    hibernateSession.merge(order);

                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } catch (HibernateException e) {
                transaction.rollback();
                throw new RuntimeException("Failed to complete Order: " + tempOrderId, e);
            }
        }
    }

    public String verifyOrderDetails(String tempOrderId) {

        JsonObject responseJson = new JsonObject();

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            Order order = hibernateSession.createQuery("FROM Order o WHERE o.tempOrderId = :tempId", Order.class)
                    .setParameter("tempId", tempOrderId)
                    .uniqueResult();

            if (order == null) {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Order not found");
                return AppUtil.GSON.toJson(responseJson);
            }

            if (order.getStatus().getName().equals(Status.Type.PAID.name())) {
                responseJson.addProperty("status", true);
                responseJson.addProperty("orderId", order.getId());
            } else {
                responseJson.addProperty("status", false);
                responseJson.addProperty("message", "Payment pending");
            }

            return AppUtil.GSON.toJson(responseJson);
        }
    }

}
