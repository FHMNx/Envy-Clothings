package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.validation.Validator;

public class ProfileService {
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
        } else if (userDTO.getLineOne() == null) {
            message = "Address Line One is required";
        } else if (userDTO.getLineOne().isBlank()) {
            message = "Address Line One cannot be empty";
        } else if (userDTO.getLineTwo() != null && userDTO.getLineTwo().isBlank()) {
            message = "Address Line Two cannot be empty";
        } else if (userDTO.getPostalCode() != null && userDTO.getPostalCode().isBlank()) {
            message = "Postal Code is required";
        } else if (userDTO.getPostalCode() != null && !userDTO.getPostalCode().matches(Validator.POSTAL_CODE_VALIDATION)) {
            message = "provide a valid Postal Code";
        } else if (userDTO.getCityId() == 0) {
            message = "Please select a city";
        } else if (userDTO.getPassword() == null) {
            message = "Password is required";
        } else if (userDTO.getPassword().isBlank()) {
            message = "Password cannot be empty";
        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "please enter a valid password";
        } else if (userDTO.getNewPassword() != null && userDTO.getNewPassword().isBlank()) {
            message = "New password cannot be empty";
        } else if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "password provide a valid new password";
        } else if (userDTO.getConfirmPassword() != null && userDTO.getConfirmPassword().isBlank()) {
            message = "confirm password cannot be empty";
        } else if (userDTO.getConfirmPassword() != null && !userDTO.getConfirmPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "provide a valid confirm password";
        } else if (userDTO.getNewPassword() != null && userDTO.getConfirmPassword() != null) {
            if (!userDTO.getConfirmPassword().equals(userDTO.getNewPassword())) {
                message = "New password and confirm password do not match";
            }
        } else {

        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);

    }
}
