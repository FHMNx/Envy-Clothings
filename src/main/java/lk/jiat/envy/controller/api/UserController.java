package lk.jiat.envy.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.service.UserService;
import lk.jiat.envy.util.AppUtil;

@Path("/users")
public class UserController {

    @Path("/reset-password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(String jsonData) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().resetPassword(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/forgot-password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(String jsonData) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().forgotPassword(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    @IsUser
    @Path("/logout")
    @POST
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("user") == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = (User) httpSession.getAttribute("user");
        httpSession.invalidate();

        Cookie cookie = new Cookie("remember_me", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        new UserService().invalidateRememberMeToken(user.getId());
        return Response.ok().build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(String jsonData) {
        UserDTO userDto = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().addNewUser(userDto);
        return Response.ok().entity(responseJson).build();
    }


    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(String jsonData, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().userLogin(userDTO, request, response);
        return Response.ok().entity(responseJson).build();
    }


    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllCustomers(@QueryParam("page") @DefaultValue("1") int page,
                                     @QueryParam("size") @DefaultValue("10") int size,
                                     @Context HttpServletRequest request) {
        String responseJson = new UserService().getAllCustomers(request, page, size);
        return Response.ok().entity(responseJson).build();
    }
}