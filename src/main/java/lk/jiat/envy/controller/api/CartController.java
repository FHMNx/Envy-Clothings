//package lk.jiat.envy.controller.api;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.QueryParam;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//@Path("/carts")
//public class CartController {
//
//    @Path("/add-to-cart")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response addToCart(@QueryParam("stockId") String stockId,
//                              @QueryParam("qty") String qty,
//                              @Context HttpServletRequest request) {
//
//        return Response.ok().entity("").build();
//
//    }
//}
