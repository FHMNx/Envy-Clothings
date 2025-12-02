package lk.jiat.envy.controller.api;

import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.dto.UserDTO;
import lk.jiat.envy.service.UserService;

@Path("/verify-account")
public class VerificationController {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUserAccount(String jsonData) {
        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().verifyUserAccount(userDTO);
        return Response.ok().entity(responseJson).build();
    }
}
