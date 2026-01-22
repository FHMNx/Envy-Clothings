package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.jiat.envy.dto.CheckoutRequestDTO;
import lk.jiat.envy.dto.OrderDTO;
import lk.jiat.envy.dto.OrderDetailsDTO;
import lk.jiat.envy.dto.OrderItemDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.mail.InvoiceMailTemplate;
import lk.jiat.envy.provider.MailServiceProvider;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.Env;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

                hibernateSession.createQuery("DELETE FROM Cart c WHERE c.user = :user")
                        .setParameter("user", order.getUser())
                        .executeUpdate();

                transaction.commit();

                // INVOICE MAIL SENDING
                List<OrderItemDTO> itemDTOList = order.getOrder_items().stream()
                        .map(orderItem -> {

                            OrderItemDTO dto = new OrderItemDTO();

                            Stock stock = orderItem.getStock();
                            Product product = stock.getProduct();

                            dto.setProductName(product.getTitle());
                            dto.setQuantity(orderItem.getQuantity());
                            dto.setPrice(stock.getPrice());

                            List<String> images = product.getImages();
                            dto.setImageUrl((images != null && !images.isEmpty()) ? images.get(0) : null);

                            return dto;
                        })
                        .toList();


                double subTotal = itemDTOList.stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum();

                double total = subTotal;

                String invoiceId = "ORD-" + order.getId();

                InvoiceMailTemplate invoiceMail = new InvoiceMailTemplate(
                        order.getUser().getEmail(),
                        order.getUser().getFirstName(),
                        invoiceId,
                        itemDTOList,
                        subTotal,
                        total
                );

                MailServiceProvider.getInstance().sendMail(invoiceMail);

                // INVOICE MAIL SENDING

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

    public String getAllOrders(HttpServletRequest request, int page, int size) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        List<OrderDTO> orderDTOList = new ArrayList<>();

        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "Session expired. Please login as an admin!";
        } else {

            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin not found! Please register as an admin.";
            } else if (!admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                message = "Admin status not verified!";
            } else {

                //PAGINATION
                int offset = (page - 1) * size;
                Long totalOrders = hibernateSession.createQuery("SELECT COUNT(DISTINCT o.id) " +
                                "FROM Order o JOIN o.order_items oi JOIN oi.stock s " +
                                "JOIN s.product p WHERE p.admin = :admin", Long.class)
                        .setParameter("admin", admin)
                        .uniqueResult();

                List<Order> orderList = hibernateSession.createQuery("SELECT DISTINCT o FROM Order o " +
                                "JOIN o.order_items oi JOIN oi.stock s JOIN s.product p " +
                                "WHERE p.admin = :admin ORDER BY o.id DESC", Order.class)
                        .setParameter("admin", admin)
                        .setFirstResult(offset)
                        .setMaxResults(size)
                        .getResultList();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd");

                for (Order o : orderList) {
                    OrderDTO orderDTO = new OrderDTO();
                    orderDTO.setOrderId(o.getId());
                    orderDTO.setCustomerName(o.getUser().getFirstName() + " " + o.getUser().getLastName());
                    orderDTO.setPaymentTypeId(o.getPaymentType().getId());
                    orderDTO.setCreatedAt(formatter.format(o.getCreatedAt()));
                    orderDTO.setStatusId(o.getStatus().getId());

                    double total = 0;
                    for (OrderItem item : o.getOrder_items()) {
                        total += item.getStock().getPrice() * item.getQuantity();
                    }
                    orderDTO.setTotal(total);

                    orderDTOList.add(orderDTO);
                }

                status = true;
                message = orderDTOList.isEmpty() ? "No orders found" : "Orders loaded successfully";

                responseObject.addProperty("currentPage", page);
                responseObject.addProperty("pageSize", size);
                responseObject.addProperty("totalOrders", totalOrders);
                responseObject.addProperty("totalPages", (int) Math.ceil((double) totalOrders / size));
            }

            hibernateSession.close();
        }

        responseObject.add("orders", AppUtil.GSON.toJsonTree(orderDTOList));
        responseObject.addProperty("message", message);
        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String deleteOrder(HttpServletRequest request, int orderId) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "Session expired. Please login again!";
        } else {

            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin not found!";
            } else {

                Order order = hibernateSession.find(Order.class, orderId);

                if (order == null) {
                    message = "order not found!";
                } else if (!admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                    message = "Admin not verified!";
                } else {

                    Transaction transaction = hibernateSession.beginTransaction();

                    for (OrderItem orderItem : order.getOrder_items()) {
                        hibernateSession.remove(orderItem);
                    }

                    hibernateSession.remove(order);

                    transaction.commit();

                    status = true;
                    message = "order deleted successfully!";
                }
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }

    public String loadOrderInfo(int orderId, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "Session expired. Please login as an admin!";
        } else {

            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin not found! Please register as an admin.";
            } else if (!admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                message = "Admin status not verified!";
            } else {

                Order order = hibernateSession.createQuery("SELECT o FROM Order o " + "JOIN FETCH o.order_items oi " +
                                "JOIN FETCH oi.stock s " + "JOIN FETCH s.product p " + "WHERE o.id = :orderId", Order.class)
                        .setParameter("orderId", orderId)
                        .uniqueResult();

                if (order == null) {
                    message = "Order not found!";
                } else {

                    OrderDetailsDTO dto = new OrderDetailsDTO();

                    dto.setOrderId(order.getId());
                    dto.setCustomerName(order.getUser().getFirstName() + " " + order.getUser().getLastName());
                    dto.setMobile(order.getMobile());
                    dto.setPostalCode(order.getPostalCode());
                    dto.setAddressLine1(order.getDeliveryLineOne());
                    dto.setAddressLine2(order.getDeliveryLineTwo());
                    dto.setNote(order.getNote());
                    dto.setStatusId(order.getStatus().getId());

                    List<OrderItemDTO> itemDTOs = new ArrayList<>();

                    for (OrderItem oi : order.getOrder_items()) {
                        OrderItemDTO itemDTO = new OrderItemDTO();
                        itemDTO.setProductName(oi.getStock().getProduct().getTitle());
                        itemDTO.setQuantity(oi.getQuantity());
                        itemDTO.setPrice(oi.getStock().getPrice());
                        itemDTOs.add(itemDTO);
                    }

                    dto.setItems(itemDTOs);

                    status = true;
                    message = "Order loaded successfully";
                    responseObject.add("order", AppUtil.GSON.toJsonTree(dto));

                }

            }

            hibernateSession.close();
        }

        responseObject.addProperty("message", message);
        responseObject.addProperty("status", status);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String updateOrderStatus(OrderDTO orderDTO, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            message = "Session expired. Please login as admin!";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                Order order = hibernateSession.find(Order.class, orderDTO.getOrderId());

                if (order == null) {
                    message = "Order not found!";
                } else {

                    Status newStatus = hibernateSession.find(Status.class, orderDTO.getStatusId());
                    if (newStatus == null) {
                        message = "Invalid status";
                    } else {
                        order.setStatus(newStatus);
                        hibernateSession.update(order);
                        transaction.commit();
                        status = true;
                        message = "Order status updated successfully!";
                    }
                }

            } catch (HibernateException e) {
                if (transaction != null) transaction.rollback();
                message = "Failed to update order status: " + e.getMessage();
            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }


}
