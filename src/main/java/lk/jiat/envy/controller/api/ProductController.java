package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.service.ProductService;
import lk.jiat.envy.util.AppUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/products")
public class ProductController {

    @Path("/save-product")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveProduct(@FormDataParam("product") String productJson, @Context HttpServletRequest request) {
        ProductDTO productDTO = AppUtil.GSON.fromJson(productJson, ProductDTO.class);
        String responseJson = new ProductService().addNewProduct(productDTO, request);
        return Response.ok().entity(responseJson).build();
    }
}