package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.service.UserService;

@Path("/profiles")
public class ProfileController {

    @IsUser
    @Path("/updateProfile")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(String jsonData, @Context HttpServletRequest request) {
        return Response.ok().entity("").build();
    }

    @IsUser
    @Path("/userProfile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserProfile(@Context HttpServletRequest request) {
        String responseJson = new UserService().userProfile(request);
        return Response.ok().entity(responseJson).build();
    }

}
