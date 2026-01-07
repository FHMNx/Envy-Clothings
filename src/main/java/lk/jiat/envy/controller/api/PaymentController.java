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
    public Response paymentSuccess(@QueryParam("orderId") String orderId) {
        System.out.println("payHere return triggered");
        return Response.seeOther(URI.create(Env.get("app.url") + "/invoice.html?orderId=" + orderId)).build();
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
        System.out.println("payHere notify triggered");

        String orderId = form.getFirst("order_id");
        String statusCode = form.getFirst("status_code");

        System.out.println("orderId: " + orderId);
        System.out.println("statusCode: " + statusCode);

        if (!PayHereUtil.validateNotify(form)) {
            System.out.println("PayHereUtil.ValidateNotify block");
            return Response.status(Response.Status.BAD_REQUEST).entity("INVALID SIGNATURE").build();
        }

        OrderService orderService = new OrderService();

        if (Integer.parseInt(statusCode) == PayHereUtil.PAYMENT_SUCCESS) {
            //success
            int oId = orderService.getPendingOrderIdByTempId(orderId);
            orderService.completeOrder(String.valueOf(oId));

        } else {
            //fail
            System.out.println("Payment failed for: " + orderId);
            orderService.failOrder(orderId);
        }
        return Response.ok().build();
    }



}
