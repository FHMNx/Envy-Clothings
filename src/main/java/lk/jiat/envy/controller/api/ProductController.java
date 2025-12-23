package lk.jiat.envy.controller.api;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.entity.Product;
import lk.jiat.envy.service.FileUploadService;
import lk.jiat.envy.service.ProductService;
import lk.jiat.envy.util.AppUtil;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.InputStream;

@Path("/products")
public class ProductController {

    @Path("/single-product")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadSingleProduct(@QueryParam("productId") int id) {
        String responseJson = new ProductService().getSingleProduct(id);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllUserProducts(@Context HttpServletRequest request) {
        String responseJson = new ProductService().getAllProducts(request);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{productId}/upload-images")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProductImages(@PathParam("productId") int productId,
                                        @FormDataParam("images[]") FormDataBodyPart formDataBodyPart,
                                        @Context ServletContext context) {

        JsonObject json = new JsonObject();
        if (formDataBodyPart == null ||
                formDataBodyPart.getParent().getBodyParts().size() != 3) {

            json.addProperty("status", false);
            json.addProperty("message", "please select 3 product images");

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(AppUtil.GSON.toJson(json))
                    .build();
        }

        FileUploadService fileUploadService = new FileUploadService(context);
        ProductService productService = new ProductService();
        Product product = productService.getProductById(productId);

        if (product == null) {
            json.addProperty("status", false);
            json.addProperty("message", "Product not found");

            return Response.status(Response.Status.NOT_FOUND).entity(AppUtil.GSON.toJson(json)).build();
        }

        formDataBodyPart.getParent().getBodyParts().forEach(bodyPart -> {
            InputStream inputStream = bodyPart.getEntityAs(InputStream.class);
            ContentDisposition contentDisposition = bodyPart.getContentDisposition();

            FileUploadService.FileItem fileItem = fileUploadService.uploadFile("product/" + productId, inputStream, contentDisposition);

            product.getImages().add(fileItem.getFullUrl());
        });

        String responseJson = productService.updateProductTable(product);
        return Response.ok().entity(responseJson).build();
    }


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