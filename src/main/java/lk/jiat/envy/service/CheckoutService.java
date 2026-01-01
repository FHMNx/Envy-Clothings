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
        boolean status = false;
        String message = "";

        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            message = "please login first";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Address billingAddress = hibernateSession.createQuery("FROM Address a WHERE a.user.id =: userId AND a.addressType=:addressType", Address.class)
                    .setParameter("userId", sessionUser.getId())
                    .setParameter("addressType", Address.class)
                    .getSingleResultOrNull();

            if (billingAddress == null) {
                message = "you haven't updated your billing address";
            } else {
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setId(billingAddress.getId());
                addressDTO.setLineOne(billingAddress.getLineOne());
                addressDTO.setLineTwo(billingAddress.getLineTwo());
                addressDTO.setPostalCode(billingAddress.getPostalCode());
                addressDTO.setMobile(billingAddress.getMobile());
                addressDTO.setAddressType(billingAddress.getAddressType());

                UserDTO userDTO = new UserDTO();
                addressDTO.setUserId(userDTO.getId());
                userDTO.setCityId(billingAddress.getId());

                List<Cart> cartList = hibernateSession.createQuery("FROM Cart c WHERE c.user.id=:userId", Cart.class)
                        .setParameter("userId", sessionUser.getId())
                        .getResultList();

                if (cartList.isEmpty()) {
                    message = "you cart is empty. please add some items first";
                } else {
                    List<CartDTO> cartDTOList = new CartService().generateCartDTOs(cartList);
                    List<AdminDTO> adminDTOList = new ArrayList();
                    for (Cart c : cartList) {
                        Admin admin = c.getStock().getProduct().getAdmin();
                        AdminDTO adminDTO = new AdminDTO();
                    }

                    List<DeliveryTypeDTO> deliveryTypeDTOList = new ArrayList();
                    List<DeliveryType> deliveryTypeList = hibernateSession.createQuery("FROM DeliveryType d", DeliveryType.class).getResultList();

                    for (DeliveryType deliveryType: deliveryTypeList) {
                        DeliveryTypeDTO deliveryTypeDTO = new DeliveryTypeDTO();
                        deliveryTypeDTO.setId(deliveryType.getId());
                        deliveryTypeDTO.setName(deliveryType.getName());
                        deliveryTypeDTOList.add(deliveryTypeDTO);
                    }

                    responseObject.add("userBillingAddress", AppUtil.GSON.toJsonTree(addressDTO));
                    responseObject.add("cartList", AppUtil.GSON.toJsonTree(cartDTOList));
                    responseObject.add("adminList", AppUtil.GSON.toJsonTree(adminDTOList));
                    responseObject.add("deliveryTypes", AppUtil.GSON.toJsonTree(deliveryTypeDTOList));

                }


            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }
}
