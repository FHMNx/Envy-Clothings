package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.Admin;
import lk.jiat.envy.entity.Status;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.mail.ForgotPasswordMailTemplate;
import lk.jiat.envy.mail.VerificationMailTemplate;
import lk.jiat.envy.provider.MailServiceProvider;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.util.TokenUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class UserService {

    public String userLogin(UserDTO userDTO, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO.getEmail() == null) {
            message = "email is required";
        } else if (userDTO.getEmail().isBlank()) {
            message = "email address cannot be empty";
        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "email address is not valid";
        } else if (userDTO.getPassword() == null) {
            message = "Password is required";
        } else if (userDTO.getPassword().isBlank()) {
            message = "Password cannot be empty";
        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "please enter a valid password. \n" +
                    " The password must contain at least 6 characters with one Capital letter , One Simple letter , One Digit" +
                    " and One Special Character";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User singleUser = hibernateSession.createNamedQuery("user.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getSingleResultOrNull();

            if (singleUser == null) {
                message = "account not found, please register first";
            } else {
                if (!singleUser.getPassword().equals(userDTO.getPassword())) {
                    message = "something went wrong, please check your login credentials";
                } else {
                    Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                            .setParameter("name", String.valueOf(Status.Type.VERIFIED))
                            .getSingleResult();

                    if (!singleUser.getStatus().equals(verifiedStatus)) {
                        message = "your account is not verified, please verified first";
                    } else {
                        HttpSession httpSession = request.getSession();
                        httpSession.setAttribute("user", singleUser);

                        Admin admin = hibernateSession.createQuery("FROM Admin a WHERE a.user=:user", Admin.class)
                                .setParameter("user", singleUser)
                                .getSingleResultOrNull();

                        if (admin != null && admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                            httpSession.setAttribute("admin", admin);
                        }

                        if (userDTO.isRememberMe()) {
                            String token = TokenUtil.generateToken();
                            LocalDateTime expiry = LocalDateTime.now().plusDays(30);

                            Transaction transaction = null;
                            try {
                                transaction = hibernateSession.beginTransaction();
                                singleUser.setRememberToken(token);
                                singleUser.setRememberTokenExpiry(expiry);
                                hibernateSession.update(singleUser);
                                transaction.commit();
                            } catch (Exception e) {
                                if (transaction != null) transaction.rollback();
                                throw e;
                            }

                            Cookie rememberCookie = new Cookie("remember_me", token);
                            rememberCookie.setMaxAge(30 * 24 * 60 * 60);
                            rememberCookie.setHttpOnly(true);
                            rememberCookie.setPath("/");

                            response.addCookie(rememberCookie);
                        }

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

    public String verifyUserAccount(UserDTO userDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO.getEmail() == null) {
            message = "email is required";
        } else if (userDTO.getEmail().isBlank()) {
            message = "email address cannot be empty";
        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "email address is not valid";
        } else if (userDTO.getVerificationCode() == null) {
            message = "verification code is required";
        } else if (userDTO.getVerificationCode().isBlank()) {
            message = "verification code cannot be empty";
        } else if (!userDTO.getVerificationCode().matches(Validator.VERIFICATION_CODE_VALIDATION)) {
            message = "please provide a valid verification code. verification code must have 6 digits";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User user = hibernateSession.createQuery("FROM User u WHERE u.email=:email AND u.verificationCode=:verificationCode", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .setParameter("verificationCode", userDTO.getVerificationCode())
                    .getSingleResultOrNull();

            if (user == null) {
                message = "account not found, please register first";
            } else {
                Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", String.valueOf(Status.Type.VERIFIED))
                        .getSingleResult();

                if (user.getStatus().equals(verifiedStatus)) {
                    message = "your account is already verified";
                } else {
                    user.setStatus(verifiedStatus);
                    user.setVerificationCode("");
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        hibernateSession.merge(user);
                        transaction.commit();
                        status = true;
                        message = "Account verification completed";
                    } catch (HibernateException e) {
                        transaction.rollback();
                        message = "account verification failed";
                    }
                }
            }
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String addNewUser(UserDTO userDto) {
        JsonObject responseObject = new JsonObject();

        boolean status = false;
        String message;

        if (userDto.getFirstName() == null) {
            message = "First Name is required";
        } else if (userDto.getFirstName().isBlank()) {
            message = "First Name cannot be empty";
        } else if (userDto.getLastName() == null) {
            message = "Last Name is required";
        } else if (userDto.getLastName().isBlank()) {
            message = "Last Name cannot be empty";
        } else if (userDto.getEmail() == null) {
            message = "Email is required";
        } else if (userDto.getEmail().isBlank()) {
            message = "Email cannot be empty";
        } else if (!userDto.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "please enter a valid email address";
        } else if (userDto.getPassword() == null) {
            message = "Password is required";
        } else if (userDto.getPassword().isBlank()) {
            message = "Password cannot be empty";
        } else if (!userDto.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "please enter a valid password. \n" +
                    " The password must contain at least 6 characters with one Capital letter , One Simple letter , One Digit" +
                    " and One Special Character";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User singleUser = hibernateSession.createNamedQuery("user.getByEmail", User.class)
                    .setParameter("email", userDto.getEmail())
                    .getSingleResultOrNull();

            if (singleUser != null) {
                message = "User with this email already exists";
            } else {
                User user = new User();
                user.setFirstName(userDto.getFirstName());
                user.setLastName(userDto.getLastName());
                user.setEmail(userDto.getEmail());
                user.setPassword(userDto.getPassword());

                String verificationCode = AppUtil.generateCode();
                user.setVerificationCode(verificationCode);

                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());

                Status pendingStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                        .setParameter("name", String.valueOf(Status.Type.PENDING))
                        .getSingleResult();
                user.setStatus(pendingStatus);

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(user);
                    transaction.commit();

                    /// VERIFICATION MAIL SENDING ALGORITHM
                    VerificationMailTemplate verificationMailTemplate = new VerificationMailTemplate(user.getEmail(), verificationCode);
                    MailServiceProvider.getInstance().sendMail(verificationMailTemplate);
                    /// VERIFICATION MAIL SENDING ALGORITHM

                    status = true;
                    message = "User has been registered successfully. verification code has been sent to your mail. " +
                            "please verify it for activate your account";

                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Account creation failed..";
                }

            }
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String forgotPassword(UserDTO userDto) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            message = "Email address is required";
        } else {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User user = hibernateSession.createNamedQuery("user.getByEmail", User.class)
                    .setParameter("email", userDto.getEmail())
                    .getSingleResultOrNull();

            if(user == null){
                message = "No account found with this email";
            }else{
                String token = TokenUtil.generateToken();
                LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

                Transaction transaction = hibernateSession.beginTransaction();
                user.setPasswordResetToken(token);
                user.setPasswordResetExpiry(expiry);
                hibernateSession.update(user);
                transaction.commit();

                ForgotPasswordMailTemplate forgotPasswordMailTemplate = new ForgotPasswordMailTemplate(user.getEmail(), token);
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

    public String resetPassword(UserDTO userDTO) {
        JsonObject responseObject = new JsonObject();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        User user = hibernateSession.createQuery("FROM User u WHERE u.passwordResetToken = :token AND u.passwordResetExpiry > CURRENT_TIMESTAMP", User.class)
                .setParameter("token", userDTO.getToken())
                .getSingleResultOrNull();

        if (user == null) {
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "Invalid or expired reset token");
        } else {
            Transaction transaction = hibernateSession.beginTransaction();
            user.setPassword(userDTO.getNewPassword());

            user.setPasswordResetToken(null);
            user.setPasswordResetExpiry(null);
            hibernateSession.update(user);
            transaction.commit();

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Password reset successfully");
        }

        hibernateSession.close();
        return AppUtil.GSON.toJson(responseObject);
    }

    public void invalidateRememberMeToken(int userId){

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();

        try {
                User user = hibernateSession.find(User.class, userId);

                if(user != null){
                    user.setRememberToken(null);
                    user.setRememberTokenExpiry(null);
                    hibernateSession.merge(user);
                }

                transaction.commit();
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        }finally {
            hibernateSession.close();
        }
    }

}
