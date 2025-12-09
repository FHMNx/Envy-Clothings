package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.AdminDTO;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.Status;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.Session;

public class AdminService {

    public String adminLoginString(UserDTO userDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        AdminDTO adminDTO = new AdminDTO();

        if (adminDTO.getEmail() == null) {
            message = "email is required";
        } else if (adminDTO.getEmail().isBlank()) {
            message = "email address cannot be blank";
        } else if (!adminDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "email is not valid";
        } else if (adminDTO.getPassword() == null) {
            message = "password is required";
        } else if (adminDTO.getPassword().isBlank()) {
            message = "password cannot be blank";
        } else if (!adminDTO.getEmail().equals(userDTO.getEmail()) && !adminDTO.getPassword().equals(userDTO.getPassword())) {
            message = "invalid login credentials";
        } else {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Admin singleAdmin = hibernateSession.createNamedQuery("admin.getByEmail", Admin.class)
                    .setParameter("email", adminDTO.getEmail())
                    .getSingleResultOrNull();

            if (singleAdmin == null) {
                message = "Account not found";
            } else {
                if (!singleAdmin.getPassword().equals(adminDTO.getPassword())) {
                    message = "something went wrong, please check your login credentials";
                } else {
                    Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                            .setParameter("name", String.valueOf(Status.Type.VERIFIED))
                            .getSingleResult();

                    if (!singleAdmin.getStatus().equals(verifiedStatus)) {
                        message = "Account is not verified, please verified first";
                    } else {
                        HttpSession httpSession = request.getSession();
                        httpSession.setAttribute("admin", singleAdmin);
                        status = true;
                        message = "login successfully";
                    }
                }
            }

            hibernateSession.close();
        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

}
