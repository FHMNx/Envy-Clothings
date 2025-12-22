package lk.jiat.envy.controller.api;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

@Path("/auth")
public class AuthController {

    @GET
    @Path("/admin-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAdminStatus(@Context HttpServletRequest request) {

            JsonObject json = new JsonObject();
            boolean isAdmin = false;

            HttpSession httpSession = request.getSession(false);

            if (httpSession != null) {
                if (httpSession.getAttribute("admin") != null) {
                    isAdmin = true;
                }
            }

            json.addProperty("isAdmin", isAdmin);
            return Response.ok(AppUtil.GSON.toJson(json)).build();
        }
}
