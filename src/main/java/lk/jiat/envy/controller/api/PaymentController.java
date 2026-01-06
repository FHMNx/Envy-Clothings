package lk.jiat.envy.controller.api;


import com.google.gson.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.util.PayHereUtil;

@Path("/payments")
public class PaymentController {

    @Path("/return")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response paymentSuccess(@QueryParam("orderId") String orderId) {
        return Response.ok().entity(orderId).build();
    }


    @Path("/cancel")
    @GET
    public Response paymentCancel() {
        System.out.println("payment canceled");
        return Response.ok().build();
    }

    @Path("/notify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response paymentNotify(MultivaluedMap<String, String> form) {
        String orderId = form.getFirst("order_id");
        String statusCode = form.getFirst("status_code");
        System.out.println("orderId: " + orderId);
        System.out.println("statusCode: " + statusCode);

        if (!PayHereUtil.validateNotify(form)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("INVALID SIGNATURE").build();
        }

        if(Integer.parseInt(statusCode) == PayHereUtil.PAYMENT_SUCCESS) {
            //SUCCESS
        }else{
            //FAIL
        }

            return Response.ok().build();

    }

//
//    @POST
//    @Path("/confirm")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response confirmPayment(@QueryParam("orderId") String orderId) {
//        JsonObject response = new JsonObject();
//        try {
//            boolean success = true;
//            if (success) {
//                // Mark order as confirmed / paid
//                // e.g., update Order status to "PAID" in DB
//                response.addProperty("status", true);
//                response.addProperty("message", "Order confirmed");
//            } else {
//                response.addProperty("status", false);
//                response.addProperty("message", "Payment validation failed");
//            }
//        } catch (Exception e) {
//            response.addProperty("status", false);
//            response.addProperty("message", "Something went wrong");
//        }
//
//        return Response.ok().entity(response.toString()).build();
//    }

}
