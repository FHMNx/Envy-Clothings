package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.service.WishListService;

@Path("/wishList")
public class wishListController {

    private final WishListService wishListService = new WishListService();

    @Path("/toggle")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleWishList(@Context HttpServletRequest request, @QueryParam("stockId") int stockId) {
        String responseJson = wishListService.toggleWishList(request, stockId);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWishListIds(@Context HttpServletRequest request) {
        String json = wishListService.getWishListIds(request);
        return Response.ok(json).build();
    }

    @Path("load-wishList")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadWishList(@Context HttpServletRequest request) {
        String responseJson = wishListService.getAllUserWishList(request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/remove-wishList/{wishListId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeWishList(@PathParam("wishListId") String wishListId, @Context HttpServletRequest request) {
        String responseJson = wishListService.deleteWishListItem(wishListId, request);
        return Response.ok().entity(responseJson).build();
    }

}
