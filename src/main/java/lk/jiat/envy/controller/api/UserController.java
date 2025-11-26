package lk.jiat.envy.controller.api;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.service.UserService;

@Path("/users")
public class UserController {

    private final Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(String jsonData) {
        UserDTO userDto = gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().addNewUser(userDto);
        return Response.ok().entity(responseJson).build();
    }
}