package lk.jiat.envy.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lk.jiat.envy.entity.User;

public class UserService {
    private static final Gson GSON = new Gson();

    public String addNewUser(User user) {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        String message = "";
        if (user.getFirstName() == null) {
            message = "First Name is required";
        } else if (user.getFirstName().isBlank()) {
            message = "First Name cannot be empty";
        } else if (user.getLastName() == null) {
            message = "Last Name is required";
        } else if (user.getLastName().isBlank()) {
            message = "Last Name cannot be empty";
        } else if (user.getEmail() == null) {
            message = "Email is required";
        } else if (user.getEmail().isBlank()) {
            message = "Email cannot be empty";
        } else if (user.getEmail().matches("")) {
            message = "please enter a valid email address";
        } else if (user.getPassword() == null) {
            message = "Password is required";
        } else if (user.getPassword().isBlank()) {
            message = "Password cannot be empty";
        } else if (user.getPassword().matches("")) {
            message = "please enter a valid password. \n" +
                    " The password must contain at least 6 characters with one Capital letter , One Simple letter , One Digit" +
                    " and One Special Character";
        } else {

        }

        responseObject.addProperty("message", message);
        return GSON.toJson(responseObject);
    }
}
