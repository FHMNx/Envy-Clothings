package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.entity.Address;
import lk.jiat.envy.entity.City;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ProfileService {

    public String userProfile(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        User user = (User) httpSession.getAttribute("user");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Address> addressList = hibernateSession.createQuery("FROM Address a WHERE a.user=:user", Address.class)
                .setParameter("user", user)
                .getResultList();

        Address primaryAddress = null;
        for (Address address : addressList) {
            if (address.isPrimary()) {
                primaryAddress = address;
                break;
            }
        }

        if (primaryAddress != null) {
            userDTO.setLineOne(primaryAddress.getLineOne());
            userDTO.setLineTwo(primaryAddress.getLineTwo());
            userDTO.setPostalCode(primaryAddress.getPostalCode());
            userDTO.setMobile(primaryAddress.getMobile());
            userDTO.setIsPrimary(primaryAddress.isPrimary());
            userDTO.setCityId(primaryAddress.getCity().getId());
            userDTO.setCityName(primaryAddress.getCity().getName());
        }

        responseObject.add("user", AppUtil.GSON.toJsonTree(userDTO));

        hibernateSession.close();
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String updateProfile(UserDTO userDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (userDTO.getFirstName() == null) {
            message = "First Name is required";
        } else if (userDTO.getFirstName().isBlank()) {
            message = "First Name cannot be empty";
        } else if (userDTO.getLastName() == null) {
            message = "Last Name is required";
        } else if (userDTO.getLastName().isBlank()) {
            message = "Last Name cannot be empty";
        } else if (userDTO.getMobile() == null || userDTO.getMobile().isBlank()) {
            message = "Mobile number cannot be empty";
        } else if (!userDTO.getMobile().matches(Validator.MOBILE_VALIDATION)) {
            message = "Mobile number is not valid";
        } else if (userDTO.getLineOne() == null) {
            message = "Address Line One is required";
        } else if (userDTO.getLineOne().isBlank()) {
            message = "Address Line One cannot be empty";
        } else if (userDTO.getPostalCode() != null && !userDTO.getPostalCode().isBlank() && !userDTO.getPostalCode().matches(Validator.POSTAL_CODE_VALIDATION)) {
            message = "provide a valid Postal Code";
        } else if (userDTO.getCityId() == 0) {
            message = "Please select a city";
        } else if (userDTO.getPassword() == null) {
            message = "Password is required";
        } else if (userDTO.getPassword().isBlank()) {
            message = "Password cannot be empty";
        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "password is not valid";
        } else if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isBlank() && !userDTO.getNewPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "provide a valid new password";
        } else if (userDTO.getConfirmPassword() != null && !userDTO.getConfirmPassword().isBlank() && !userDTO.getConfirmPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "provide a valid confirm password";
        } else if (userDTO.getNewPassword() != null && userDTO.getConfirmPassword() != null &&
                !userDTO.getConfirmPassword().equals(userDTO.getNewPassword())) {
            message = "New password and confirm password do not match";
        } else {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null) {
                message = "please login first";
            } else if (httpSession.getAttribute("user") == null) {
                message = "Please login first";
            } else {
                User sessionUser = (User) httpSession.getAttribute("user");
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                User dbUser = hibernateSession.createNamedQuery("user.getByEmail", User.class)
                        .setParameter("email", sessionUser.getEmail())
                        .getSingleResult();
                dbUser.setFirstName(userDTO.getFirstName());
                dbUser.setLastName(userDTO.getLastName());
                dbUser.setPassword(userDTO.getConfirmPassword().isBlank() ? userDTO.getConfirmPassword() : userDTO.getPassword());

                List<Address> addressList = hibernateSession.createQuery("FROM Address a WHERE a.user=:user", Address.class)
                        .setParameter("user", dbUser)
                        .getResultList();

                Address currentAddress = null;
                for (Address address : addressList) {
                    if (address.getLineOne().equals(userDTO.getLineOne()) &&
                            address.getLineTwo().equals(userDTO.getLineTwo() != null ? userDTO.getLineTwo() : "") &&
                            address.getPostalCode().equals(userDTO.getPostalCode() != null ? userDTO.getPostalCode() : "") &&
                            address.getCity().getId() == userDTO.getCityId()) {

                        currentAddress = address;
                        break;
                    }
                }

                if (currentAddress == null) {
                    currentAddress = new Address();
                }

                currentAddress.setLineOne(userDTO.getLineOne());
                currentAddress.setLineTwo(userDTO.getLineTwo());
                currentAddress.setPostalCode(userDTO.getPostalCode());
                currentAddress.setMobile(userDTO.getMobile());
                currentAddress.setUser(dbUser);

                City city = hibernateSession.find(City.class, userDTO.getCityId());
                currentAddress.setCity(city);

                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.merge(dbUser);
                    hibernateSession.merge(currentAddress);
                    transaction.commit();
                    status = true;
                    message = "profile details updated successful";
                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "profile details updated failed";
                }

                hibernateSession.close();
            }
        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);

    }

}
