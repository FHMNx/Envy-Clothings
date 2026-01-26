package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.AdminDTO;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.Status;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.util.PasswordUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class AdminService {

    public String adminLogin(AdminDTO adminDTO, @Context HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (adminDTO.getEmail() == null || adminDTO.getEmail().isBlank()) {
            message = "Email is required";
        } else if (!adminDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Invalid email format";
        } else if (adminDTO.getPassword() == null || adminDTO.getPassword().isBlank()) {
            message = "Password is required";
        } else {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                Admin singleAdmin = hibernateSession.createNamedQuery("admin.getByEmail", Admin.class)
                        .setParameter("email", adminDTO.getEmail())
                        .uniqueResult();

                if (singleAdmin == null) {
                    message = "Account not found";
                } else if (!PasswordUtil.verify(adminDTO.getPassword(), singleAdmin.getPassword())) {
                    message = "Invalid credentials";
                } else if (singleAdmin.getStatus().getId() != 1) {
                    message = "Your account is inactive. Please contact system administrator.";

                } else {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("admin", singleAdmin);

                    singleAdmin.setLastLoginAt(LocalDateTime.now());
                    hibernateSession.merge(singleAdmin);

                    status = true;
                    message = "Login successful";
                }
                transaction.commit();

            } catch (HibernateException e) {
                transaction.rollback();
                throw new RuntimeException("Admin login failed", e);
            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

}
