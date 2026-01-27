package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.AdminDTO;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.mail.AdminForgotPasswordMailTemplate;
import lk.jiat.envy.mail.ForgotPasswordMailTemplate;
import lk.jiat.envy.provider.MailServiceProvider;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.util.PasswordUtil;
import lk.jiat.envy.util.TokenUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public String changePassword(AdminDTO adminDTO, @Context HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (adminDTO.getPassword() == null || adminDTO.getPassword().isBlank()) {
            message = "Current password is required";
        } else if (adminDTO.getNewPassword() == null || adminDTO.getNewPassword().isBlank()) {
            message = "New password is required";
        } else if (!adminDTO.getNewPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "New password is not strong enough";
        } else if (!adminDTO.getNewPassword().equals(adminDTO.getConfirmPassword())) {
            message = "New password and confirm password do not match";
        } else {

            HttpSession httpSession = request.getSession(false);

            if (httpSession == null || httpSession.getAttribute("admin") == null) {
                message = "Please login again";
            } else {

                Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");

                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = hibernateSession.beginTransaction();

                try {
                    Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

                    if (!PasswordUtil.verify(adminDTO.getPassword(), admin.getPassword())) {
                        message = "Current password is incorrect";
                    } else {

                        String hashedPassword = PasswordUtil.hash(adminDTO.getNewPassword());
                        admin.setPassword(hashedPassword);

                        hibernateSession.merge(admin);
                        transaction.commit();

                        status = true;
                        message = "Password updated successfully";
                    }

                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Password update failed";
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String forgotAdminPassword(AdminDTO adminDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (adminDTO.getEmail() == null || adminDTO.getEmail().isBlank()) {
            message = "Email address is required";
        } else {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Admin admin = hibernateSession.createNamedQuery("admin.getByEmail", Admin.class)
                    .setParameter("email", adminDTO.getEmail())
                    .getSingleResultOrNull();

            if (admin == null) {
                message = "No account found with this email";
            } else {
                String token = TokenUtil.generateToken();
                LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

                Transaction transaction = hibernateSession.beginTransaction();
                admin.setPasswordResetToken(token);
                admin.setPasswordResetExpiry(expiry);
                hibernateSession.update(admin);
                transaction.commit();

                AdminForgotPasswordMailTemplate forgotPasswordMailTemplate = new AdminForgotPasswordMailTemplate(admin.getEmail(), token);
                MailServiceProvider.getInstance().sendMail(forgotPasswordMailTemplate);

                status = true;
                message = "Password reset email sent";

            }
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String resetPassword(AdminDTO adminDTO) {
        JsonObject responseObject = new JsonObject();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Admin admin = hibernateSession.createQuery("FROM Admin a WHERE a.passwordResetToken = :token AND a.passwordResetExpiry > CURRENT_TIMESTAMP", Admin.class)
                .setParameter("token", adminDTO.getToken())
                .getSingleResultOrNull();

        if (admin == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Invalid or expired reset token");
        } else {
            Transaction transaction = hibernateSession.beginTransaction();
            String hashedPassword = PasswordUtil.hash(adminDTO.getNewPassword());
            admin.setPassword(hashedPassword);

            admin.setPasswordResetToken(null);
            admin.setPasswordResetExpiry(null);
            hibernateSession.update(admin);
            transaction.commit();

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Password reset successfully");
        }

        hibernateSession.close();
        return AppUtil.GSON.toJson(responseObject);
    }

}
