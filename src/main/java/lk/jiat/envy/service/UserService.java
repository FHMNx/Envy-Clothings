package lk.jiat.envy.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {
    private static final Gson GSON = new Gson();

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

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.persist(user);
                    transaction.commit();

                    status = true;
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
