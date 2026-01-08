package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.CheckoutRequestDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
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

    public void completeOrder(String orderId) {
        int oId = Integer.parseInt(orderId.replaceAll(Validator.NON_DIGIT_PATTERN, ""));

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            try {

                Order order = hibernateSession.find(Order.class, oId);
                if (order == null) {
                    throw new RuntimeException("Order Not Found for Order ID: " + oId);
                }

                //update stock quantity
                List<OrderItem> orderItems = order.getOrder_items();
                if (orderItems != null && !orderItems.isEmpty()) {
                    for (OrderItem orderItem : orderItems) {
                        Stock stock = orderItem.getStock();
                        int updatedQty = stock.getQuantity() - orderItem.getQuantity();
                        if (updatedQty < 0) {
                            throw new RuntimeException("Insufficient stock for product: " + stock.getProduct().getTitle());
                        }
                        stock.setQuantity(updatedQty);
                        hibernateSession.merge(stock);
                    }
                }

                //update order status
                Status completedStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", String.valueOf(Status.Type.PAID))
                        .getSingleResult();
                order.setStatus(completedStatus);
                hibernateSession.merge(order);

                //remove cart items
                List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user = :user", Cart.class)
                        .setParameter("user", order.getUser())
                        .getResultList();

                for (Cart cart : cartList) {
                    hibernateSession.remove(cart);
                }
                transaction.commit();

            } catch (HibernateException e) {
                transaction.rollback();
                throw new RuntimeException("Failed to complete Order: " + orderId, e);
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
                    // Fetch PAYMENT_FAILED status
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

    public int getPendingOrderIdByTempId(String tempOrderId) {

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {

            Order order = hibernateSession.createQuery("FROM Order o WHERE o.tempOrderId = :tempId", Order.class)
                    .setParameter("tempId", tempOrderId)
                    .uniqueResult();
            if (order == null) {
                throw new RuntimeException("Pending order not found");
            }
            return order.getId();
        }
    }


}
