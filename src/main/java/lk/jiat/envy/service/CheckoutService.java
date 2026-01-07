package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;

import lk.jiat.envy.dto.*;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.Env;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.util.PayHereUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutService {

    public String processCheckout(CheckoutRequestDTO requestDTO, HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        hibernateSession.beginTransaction();

        try {
            User sessionUser = (User) request.getSession().getAttribute("user");

            if (sessionUser == null) {
                message = "Session expired. Please login again.";
                hibernateSession.getTransaction().rollback();

            } else {

                User dbUser = hibernateSession.find(User.class, sessionUser.getId());

                Address billingAddress = null;
                City city = null;

                if (requestDTO.isCurrentAddress()) {

                    billingAddress = hibernateSession.createQuery("FROM Address a WHERE a.user=:user AND a.addressType=:type", Address.class)
                            .setParameter("user", dbUser)
                            .setParameter("type", "billing")
                            .getSingleResultOrNull();

                    if (billingAddress == null) {
                        message = "Billing address not found";
                        hibernateSession.getTransaction().rollback();
                    }

                } else {

                    if (requestDTO.getFirstName().isBlank()) {
                        message = "first name is required";
                    } else if (requestDTO.getLastName().isBlank()) {
                        message = "last name is required";
                    } else if (requestDTO.getLineOne().isBlank()) {
                        message = "Address line one is required";
                    } else if (requestDTO.getLineTwo().isBlank()) {
                        message = "Address line two is required";
                    } else if (requestDTO.getCityId() == AppUtil.DEFAULT_SELECTOR_VALUE) {
                        message = "please select a city";
                    } else if (requestDTO.getMobile().isBlank()) {
                        message = "mobile is required";
                    } else if (!requestDTO.getMobile().matches(Validator.MOBILE_VALIDATION)) {
                        message = "mobile is not valid";
                    } else if (requestDTO.getPostalCode().isBlank()) {
                        message = "postal code is required";
                    } else if (!requestDTO.getPostalCode().matches(Validator.POSTAL_CODE_VALIDATION)) {
                        message = "postal code is not valid";
                    }

                    if (!message.isEmpty()) {
                        hibernateSession.getTransaction().rollback();
                    }

                    city = hibernateSession.find(City.class, requestDTO.getCityId());
                    if (city == null) {
                        message = "city not found";
                        hibernateSession.getTransaction().rollback();
                    }
                }

                if (message.isEmpty()) {
                    List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user=:user", Cart.class)
                            .setParameter("user", dbUser)
                            .getResultList();

                    if (cartList.isEmpty()) {
                        message = "Cart is empty";
                        hibernateSession.getTransaction().rollback();

                    } else {

                        Status pendingStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                                .setParameter("name", Status.Type.PENDING.name())
                                .getSingleResult();

                        PaymentType paymentType = hibernateSession.find(PaymentType.class, requestDTO.getPaymentTypeId());

                        City deliveryCity = requestDTO.isCurrentAddress() ? billingAddress.getCity() : city;

                        Admin shopAdmin = hibernateSession.createQuery("FROM Admin", Admin.class)
                                .setMaxResults(1)
                                .uniqueResult();

                        City shopCity = shopAdmin.getCity();

                        int deliveryTypeId = (deliveryCity.getId() == shopCity.getId()) ? 1 : 2;
                        DeliveryType deliveryType = hibernateSession.find(DeliveryType.class, deliveryTypeId);

                        OrderService orderService = new OrderService();

                        // CARD PAYMENT
                        if (paymentType.getId() == 1) {

                            String tempOrderId = "TMP_" + dbUser.getId() + "_" + System.currentTimeMillis();

                            // Create a pending order
                            Order pendingOrder = new OrderService().createPendingOrder(
                                    hibernateSession, dbUser, requestDTO, paymentType, deliveryType, pendingStatus, billingAddress, city
                            );

                            // Set tempOrderId as order reference
                            pendingOrder.setTempOrderId(tempOrderId);
                            hibernateSession.merge(pendingOrder);

                            PayHereDTO paymentDetails = createPaymentDetails(hibernateSession, tempOrderId, dbUser, deliveryType);

                            responseObject.add("paymentDetails", AppUtil.GSON.toJsonTree(paymentDetails));
                            hibernateSession.getTransaction().commit();

                            status = true;
                            message = "Proceed to payment";
                        }

                        // COD PAYMENT
                        else if (paymentType.getId() == 2) {

                            orderService.createOrder(dbUser, requestDTO, paymentType, deliveryType,
                                    pendingStatus, billingAddress, hibernateSession);

                            hibernateSession.getTransaction().commit();

                            status = true;
                            message = "Order placed successfully";
                        }
                    }
                }
            }

        } catch (Exception e) {
            hibernateSession.getTransaction().rollback();
            message = "Something went wrong";
            e.printStackTrace();

        } finally {
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }

    private PayHereDTO createPaymentDetails(Session hibernateSession, String tempOrderId, User user, DeliveryType deliveryType) {

        String order_id = tempOrderId;

        String returnURL = Env.get("app.public.url") + "/api/payments/return";
        String cancelURL = Env.get("app.public.url") + "/api/payments/cancel";
        String notifyURL = Env.get("app.public.url") + "/api/payments/notify";

        StringBuilder items = new StringBuilder();
        double amount = 0.0;

        List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user = :user", Cart.class)
                .setParameter("user", user)
                .getResultList();

        for (Cart cart : cartList) {
            if (!items.isEmpty()) {
                items.append(", ");
            }

            items.append(cart.getStock().getProduct().getTitle())
                    .append(" x ")
                    .append(cart.getQuantity());

            amount += cart.getStock().getPrice() * cart.getQuantity();
        }
        double totalAmount = amount + deliveryType.getPrice();

        String formattedAmount = String.format(Locale.US, "%.2f", totalAmount);
        String hashValue = PayHereUtil.generateHash(order_id, totalAmount);


        Address billingAddress = hibernateSession.createQuery("FROM Address a WHERE a.user = :user AND a.addressType = 'billing'", Address.class)
                .setParameter("user", user)
                .getSingleResultOrNull();

        if (billingAddress == null) {
            throw new RuntimeException("Billing address not found");
        }


        PayHereDTO payHereDTO = new PayHereDTO();
        payHereDTO.setSandBox(true);
        payHereDTO.setMerchant_id(PayHereUtil.getMerchantId());
        payHereDTO.setReturn_url(returnURL);
        payHereDTO.setCancel_url(cancelURL);
        payHereDTO.setNotify_url(notifyURL);
        payHereDTO.setOrder_id(order_id);
        payHereDTO.setItems(items.toString());
        payHereDTO.setAmount(formattedAmount);
        payHereDTO.setCurrency(PayHereUtil.APP_CURRENCY);
        payHereDTO.setHash(hashValue);
        payHereDTO.setFirst_name(user.getFirstName());
        payHereDTO.setLast_name(user.getLastName());
        payHereDTO.setEmail(user.getEmail());
        payHereDTO.setPhone(billingAddress.getMobile());
        payHereDTO.setAddress(billingAddress.getLineOne() + ", " + billingAddress.getLineTwo());
        payHereDTO.setCity(billingAddress.getCity().getName());
        payHereDTO.setCountry(PayHereUtil.APP_COUNTRY);


        return payHereDTO;
    }

    public String getCheckoutData(HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();

        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Please login first");
            return AppUtil.GSON.toJson(responseObject);
        }

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        try {

            Address billingAddress = hibernateSession.createQuery("FROM Address a WHERE a.user.id = :userId AND a.addressType = :type", Address.class)
                    .setParameter("userId", sessionUser.getId())
                    .setParameter("type", "billing")
                    .getSingleResultOrNull();

            if (billingAddress == null) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Please update your billing address");
                return AppUtil.GSON.toJson(responseObject);
            }
            AddressDTO billingDTO = buildAddressDTO(billingAddress, sessionUser);


            Address shippingAddress = hibernateSession.createQuery("FROM Address a WHERE a.user.id = :userId AND a.addressType = :type", Address.class)
                    .setParameter("userId", sessionUser.getId())
                    .setParameter("type", "shipping")
                    .getSingleResultOrNull();

            AddressDTO shippingDTO = shippingAddress != null ? buildAddressDTO(shippingAddress, sessionUser) : null;


            List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id = :userId", Cart.class)
                    .setParameter("userId", sessionUser.getId())
                    .getResultList();

            if (cartList.isEmpty()) {
                responseObject.addProperty("status", false);
                responseObject.addProperty("message", "Your cart is empty");
                return AppUtil.GSON.toJson(responseObject);
            }
            List<CartDTO> cartDTOList = new CartService().generateCartDTOs(cartList);


            Admin shopAdmin = hibernateSession.createQuery("FROM Admin", Admin.class).setMaxResults(1).uniqueResult();
            City shopCity = shopAdmin.getCity();


            List<DeliveryType> deliveryTypeList = hibernateSession.createQuery("FROM DeliveryType", DeliveryType.class).getResultList();
            List<DeliveryTypeDTO> deliveryTypeDTOList = new ArrayList<>();

            for (DeliveryType deliveryType : deliveryTypeList) {
                DeliveryTypeDTO dto = new DeliveryTypeDTO();
                dto.setId(deliveryType.getId());
                dto.setName(deliveryType.getName());
                dto.setPrice(deliveryType.getPrice());
                deliveryTypeDTOList.add(dto);
            }


            responseObject.add("billingAddress", AppUtil.GSON.toJsonTree(billingDTO));
            responseObject.add("shippingAddress", AppUtil.GSON.toJsonTree(shippingDTO));
            responseObject.add("cartList", AppUtil.GSON.toJsonTree(cartDTOList));
            responseObject.add("deliveryTypes", AppUtil.GSON.toJsonTree(deliveryTypeDTOList));

            responseObject.addProperty("shopCityId", shopCity.getId());
            responseObject.addProperty("shopCityName", shopCity.getName());

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Checkout data loaded");

            return AppUtil.GSON.toJson(responseObject);

        } finally {
            hibernateSession.close();
        }
    }

    private AddressDTO buildAddressDTO(Address address, User user) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setLineOne(address.getLineOne());
        dto.setLineTwo(address.getLineTwo());
        dto.setPostalCode(address.getPostalCode());
        dto.setMobile(address.getMobile());
        dto.setAddressType(address.getAddressType());
        dto.setCityId(address.getCity().getId());

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
