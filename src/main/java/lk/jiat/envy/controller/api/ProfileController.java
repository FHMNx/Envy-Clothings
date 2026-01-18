package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.service.ProfileService;
import lk.jiat.envy.util.AppUtil;

@Path("/profiles")
public class ProfileController {

    private final ProfileService profileService = new ProfileService();

    @IsUser
    @Path("/updateProfile")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(String jsonData, @Context HttpServletRequest request) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = profileService.updateProfile(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/userProfile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserProfile(@Context HttpServletRequest request) {
        String responseJson = profileService.userProfile(request);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/userOrders")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserOrdes(@Context HttpServletRequest request) {
        String responseJson = profileService.loadUserOrders(request);
        return Response.ok().entity(responseJson).build();
    }

}
