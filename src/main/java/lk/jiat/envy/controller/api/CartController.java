package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.service.CartService;

@Path("/carts")
public class CartController {

    @Path("/add-to-cart")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(@QueryParam("stockId") String stockId,
                              @QueryParam("qty") String qty,
                              @Context HttpServletRequest request) {
        String responseJson = new CartService().addToCart(stockId, qty, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/load-carts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllCarts(@Context HttpServletRequest request) {
        String responseJson = new CartService().getAllUserCarts(request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/remove-cart/{cartId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeCartItem(@PathParam("cartId") String cartId, @Context HttpServletRequest request) {
        String responseJson = new CartService().deleteCartItem(cartId, request);
        return Response.ok().entity(responseJson).build();
    }
}
