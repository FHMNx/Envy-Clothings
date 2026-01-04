package lk.jiat.envy.controller.api;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.dto.CheckoutRequestDTO;
import lk.jiat.envy.service.CheckoutService;
import lk.jiat.envy.util.AppUtil;

@Path("/checkouts")
public class CheckoutController {

    private final CheckoutService checkoutService = new CheckoutService();

    @IsUser
    @Path("/user-checkout")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout(String requestData, @Context HttpServletRequest request) {
        CheckoutRequestDTO checkoutRequestDTO = AppUtil.GSON.fromJson(requestData, CheckoutRequestDTO.class);
        String responseJson = checkoutService.processCheckout(checkoutRequestDTO, request);
        return Response.ok().entity(responseJson).build();
    }


    @IsUser
    @Path("/user-checkout-data")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserCheckoutData(@Context HttpServletRequest request) {
        String responseObject = checkoutService.getCheckoutData(request);
        return Response.ok().entity(responseObject).build();
    }
}
