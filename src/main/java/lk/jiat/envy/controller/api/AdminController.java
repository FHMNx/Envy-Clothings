package lk.jiat.envy.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsAdmin;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.dto.AdminDTO;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.service.AdminService;
import lk.jiat.envy.service.ProductService;
import lk.jiat.envy.service.UserService;
import lk.jiat.envy.util.AppUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/admin")
public class AdminController {

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adminLogin(String jsonData, @Context HttpServletRequest request) {
        AdminDTO adminDTO = AppUtil.GSON.fromJson(jsonData, AdminDTO.class);
        String responseJson = new AdminService().adminLogin(adminDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @IsAdmin
    @Path("/logout")
    @POST
    public Response logout(@Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("admin") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        session.invalidate();

        return Response.ok().build();
    }

    @IsAdmin
    @Path("/changePassword")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(String jsonData, @Context HttpServletRequest request) {
        AdminDTO adminDTO = AppUtil.GSON.fromJson(jsonData, AdminDTO.class);
        String responseJson = new AdminService().changePassword(adminDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/forgot-password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response forgotPassword(String jsonData) {
        AdminDTO adminDTO = AppUtil.GSON.fromJson(jsonData, AdminDTO.class);
        String responseJson = new AdminService().forgotAdminPassword(adminDTO);
        return Response.ok().entity(responseJson).build();
    }


    @Path("/reset-password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(String jsonData) {
        AdminDTO adminDTO = AppUtil.GSON.fromJson(jsonData, AdminDTO.class);
        String responseJson = new AdminService().resetPassword(adminDTO);
        return Response.ok().entity(responseJson).build();
    }

}