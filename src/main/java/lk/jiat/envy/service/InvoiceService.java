package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.InvoiceDTO;
import lk.jiat.envy.dto.InvoiceItemDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.Session;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceService {

    public String getInvoiceData(String orderId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        int oId = Integer.parseInt(orderId.replaceAll(Validator.NON_DIGIT_PATTERN, ""));

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Order order = hibernateSession.find(Order.class, oId);

        if (order == null) {
            message = "incorrect order details. please check credentials";
        } else {
            if (order.getStatus().getName().equals(String.valueOf(Status.Type.PAID))) {
                InvoiceDTO invoiceDTO = new InvoiceDTO();
                invoiceDTO.setInvoiceNo("000" + order.getId());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                invoiceDTO.setInvoiceDate(formatter.format(order.getCreatedAt()));

                User user = order.getUser();
                invoiceDTO.setBuyerName(user.getFirstName() + " " + user.getLastName());
                invoiceDTO.setAddress(order.getDeliveryLineOne() + (!order.getDeliveryLineTwo().isBlank() ? order.getDeliveryLineTwo() : ""));
                invoiceDTO.setCityName(order.getCity().getName());
                invoiceDTO.setCountryName("Sri Lanka");
                invoiceDTO.setEmail(user.getEmail());

                DeliveryType withinCity =  hibernateSession.createNamedQuery("DeliveryType.findByName", DeliveryType.class)
                        .setParameter("name", String.valueOf(DeliveryType.Value.WITHIN_CITY))
                        .getSingleResult();

                DeliveryType outOfCity =  hibernateSession.createNamedQuery("DeliveryType.findByName", DeliveryType.class)
                        .setParameter("name", String.valueOf(DeliveryType.Value.OUT_OF_CITY))
                        .getSingleResult();

                double shippingCost = 0;

                List<InvoiceItemDTO> itemDTOS = new ArrayList<>();
                for (OrderItem orderItem : order.getOrder_items()) {
                    InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
                    invoiceItemDTO.setItemName(orderItem.getStock().getProduct().getTitle());
                    invoiceItemDTO.setItemQty(orderItem.getQuantity());
                    invoiceItemDTO.setItemPrice(orderItem.getStock().getPrice());
                    itemDTOS.add(invoiceItemDTO);

                    User admin = order.getUser();
                    Address adminAddress = hibernateSession.createQuery("FROM Address a WHERE a.user=:user", Address.class)
                            .setParameter("user", admin)
                            .getSingleResult();

                    if(order.getCity().getName().equals(adminAddress.getCity().getName())){
                        shippingCost +=withinCity.getPrice();
                    }else{
                        shippingCost +=outOfCity.getPrice();
                    }

                }
                invoiceDTO.setShippingCost(shippingCost);
                invoiceDTO.setInvoiceItemDTOList(itemDTOS);
                invoiceDTO.setInvoiceStatus(order.getStatus().getName());

                status = true;
                responseObject.add("invoiceData", AppUtil.GSON.toJsonTree(invoiceDTO));

            }
        }

        hibernateSession.close();

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }
}
