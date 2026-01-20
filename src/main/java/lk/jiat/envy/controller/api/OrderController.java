package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.dto.OrderDTO;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.service.OrderService;
import lk.jiat.envy.service.ProductService;
import lk.jiat.envy.util.AppUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
        String responseJson = orderService.getAllOrders(request, page, size);
        return Response.ok().entity(responseJson).build();

    }

    @Path("/{orderId}/delete")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrder(@PathParam("orderId") int orderId, @Context HttpServletRequest request) {
        String responseJson = orderService.deleteOrder(request, orderId);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{orderId}/OrderInfo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderDetails(@PathParam("orderId") int orderId, @Context HttpServletRequest request) {
        String responseJson = orderService.loadOrderInfo(orderId, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{orderId}/updateOrder")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("orderId") int orderId, String requestBody, @Context HttpServletRequest request) {
        OrderDTO orderDTO = AppUtil.GSON.fromJson(requestBody, OrderDTO.class);
        orderDTO.setOrderId(orderId);
        String responseJson = orderService.updateOrderStatus(orderDTO, request);
        return Response.ok().entity(responseJson).build();
    }

}

