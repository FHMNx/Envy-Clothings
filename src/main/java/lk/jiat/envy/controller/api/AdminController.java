package lk.jiat.envy.controller.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsAdmin;
import lk.jiat.envy.dto.AdminDTO;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.service.AdminService;
import lk.jiat.envy.service.UserService;
import lk.jiat.envy.util.AppUtil;

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

}