package lk.jiat.envy.controller.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.service.OrderService;
import lk.jiat.envy.util.Env;
import lk.jiat.envy.util.PayHereUtil;

import java.net.URI;
import java.util.Map;

@Path("/payments")
public class PaymentController {

    @Path("/return")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response paymentReturn(@QueryParam("orderId") String orderId) {
        System.out.println("payHere return triggered");
        return Response.seeOther(URI.create(Env.get("app.url") + "/payment-processing.html")).build();
    }


    @Path("/cancel")
    @GET
    public Response paymentCancel() {
        System.out.println("payHere cancel triggered");
        return Response.ok().build();
    }

    @Path("/notify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response paymentNotify(MultivaluedMap<String, String> form) {

        if (!PayHereUtil.validateNotify(form)) {
            return Response.status(400).build();
        }

        String tempOrderId = form.getFirst("order_id");
        int statusCode = Integer.parseInt(form.getFirst("status_code"));

        OrderService orderService = new OrderService();

        if (statusCode == PayHereUtil.PAYMENT_SUCCESS) {
            orderService.completeOrder(tempOrderId);
        } else {
            orderService.failOrder(tempOrderId);
        }

        return Response.ok().build();
    }


}
