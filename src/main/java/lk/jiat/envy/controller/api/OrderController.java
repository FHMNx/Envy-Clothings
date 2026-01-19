package lk.jiat.envy.controller.api;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.entity.Order;
import lk.jiat.envy.service.OrderService;

@Path("/orders")
public class OrderController {

    private final OrderService orderService = new OrderService();

    @Path("/verify-order")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyOrder(@QueryParam("orderId") String orderId) {
        String responseJson = orderService.verifyOrderDetails(orderId);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllOrders(@QueryParam("page") @DefaultValue("1") int page,
                                  @QueryParam("size") @DefaultValue("10") int size,
                                  @Context HttpServletRequest request) {
        String responseJson = orderService.getAllOrders(request,page,size);
        return Response.ok().entity(responseJson).build();

    }
}

