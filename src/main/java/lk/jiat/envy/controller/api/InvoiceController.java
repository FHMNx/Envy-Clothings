package lk.jiat.envy.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.jiat.envy.annotation.IsUser;
import lk.jiat.envy.service.InvoiceService;

@Path("/invoices")
@IsUser
public class InvoiceController {

    private final InvoiceService invoiceService = new InvoiceService();

    @Path("/user-invoice")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadInvoiceData(@QueryParam("orderId") String orderId, @Context HttpServletRequest request) {
        String responseJson = invoiceService.getInvoiceData(orderId, request);
        return Response.ok().entity(responseJson).build();
    }
}
