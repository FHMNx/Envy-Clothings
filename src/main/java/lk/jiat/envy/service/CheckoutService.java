package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import lk.jiat.envy.dto.*;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class CheckoutService {

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
            AddressDTO billingDTO = buildAddressDTO(billingAddress,sessionUser);


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
