package lk.jiat.envy.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.Status;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class UserService {
    private static final Gson GSON = new Gson();

    public String userLogin(UserDTO userDTO , @Context HttpServletRequest request) {
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
                    }else{
                       HttpSession httpSession = request.getSession();
                       httpSession.setAttribute("user", singleUser);
                       status = true;
                       message = "login successfully";
                    }
                }
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return GSON.toJson(responseObject);
    }

    public String verifyUserAccount(String code) {
        JsonObject responseObject = new JsonObject();

        boolean status = false;
        String message = "";

        if (code == null) {
            message = "verification code is required";
        } else if (code.isBlank()) {
            message = "verification code cannot be empty";
        } else if (!code.matches(Validator.VERIFICATION_CODE_VALIDATION)) {
            message = "please provide a valid verification code. verification code must have 6 digits";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            hibernateSession.createQuery("FROM User u WHERE u.verificationCode=:verificationCode", User.class)
                    .setParameter("verificationCode", Integer.parseInt(code))
                    .getSingleResultOrNull();
            hibernateSession.close();
        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return GSON.toJson(responseObject);
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

                    status = true;
                    responseObject.addProperty("uId", user.getId());
                    message = "User has been registered successfully. verification code has been sent to your mail. " +
                            "please verify it for activate your account";


                    /// VERIFICATION MAIL SENDING ALGORITHM
                    /// VERIFICATION MAIL SENDING ALGORITHM

                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Account creation failed..";
                }

            }
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return GSON.toJson(responseObject);
    }

}
