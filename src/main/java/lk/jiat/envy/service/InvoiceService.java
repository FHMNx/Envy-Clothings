package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.util.AppUtil;

public class InvoiceService {

    public String getInvoiceData(String orderId) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }
}
