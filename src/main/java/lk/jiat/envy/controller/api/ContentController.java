package lk.jiat.envy.controller.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.service.CityService;
import lk.jiat.envy.service.ProductService;

@Path("/data")
public class ContentController {

    @Path("/cities")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadCities() {
        String loadAllCities = new CityService().loadAllCities();
        return Response.ok().entity(loadAllCities).build();
    }

    @Path("/brands")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadBrands() {
        String responseJson = new ProductService().loadBrandDetails();
        return Response.ok().entity(responseJson).build();
    }

    @Path("/{brandId}/models")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadModels(@PathParam("brandId") int id) {
        String responseJson = new ProductService().loadModelDetails(id);
        return Response.ok().entity(responseJson).build();
    }

}
