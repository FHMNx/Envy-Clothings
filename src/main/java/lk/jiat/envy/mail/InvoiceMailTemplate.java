package lk.jiat.envy.mail;

import io.rocketbase.mail.model.HtmlTextEmail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lk.jiat.envy.dto.OrderItemDTO;
import lk.jiat.envy.util.Env;

import java.util.List;

public class InvoiceMailTemplate extends Mailable {

    private final String to;
    private final String customerName;
    private final String invoiceId;
    private final List<OrderItemDTO> items;
    private final double subTotal;
    private final double total;

    public InvoiceMailTemplate(
            String to,
            String customerName,
            String invoiceId,
            List<OrderItemDTO> items,
            double subTotal,
            double total
    ) {
        this.to = to;
        this.customerName = customerName;
        this.invoiceId = invoiceId;
        this.items = items;
        this.subTotal = subTotal;
        this.total = total;
    }

    @Override
    public void build(Message message) throws MessagingException {

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Your Envy Clothing Order Confirmation. Order No 000" + invoiceId);

        StringBuilder itemsHtml = new StringBuilder();

        for (OrderItemDTO item : items) {

            double itemTotal = item.getPrice() * item.getQuantity();
            String image = item.getImageUrl() != null ? item.getImageUrl() : "https://via.placeholder.com/80x100?text=Item";

            itemsHtml.append(
                    "<div style='display:flex;align-items:center;padding:12px 0;border-bottom:1px solid #e5e5e5;'>"
                            + "<img src='" + image + "' style='width:70px;border-radius:6px;margin-right:14px;'>"
                            + "<div style='flex:1;'>"
                            + "<strong>" + item.getProductName() + "</strong><br>"
                            + "<span style='font-size:13px;color:#666;'>Quantity: " + item.getQuantity() + "</span>"
                            + "</div>"
                            + "<div style='font-weight:600;'>Rs. "
                            + String.format("%,.2f", itemTotal)
                            + "</div>"
                            + "</div>"
            );
        }

        String totalsHtml =
                "<div style='margin-top:16px;text-align:right;'>"
                        + "<p>Subtotal: <strong>Rs. " + String.format("%,.2f", subTotal) + "</strong></p>"
                        + "<h2>Total: Rs. " + String.format("%,.2f", total) + "</h2>"
                        + "</div>";

        HtmlTextEmail email = getEmailTemplateBuilder()


                .header()
                .logo("https://cdn.venngage.com/template/thumbnail/small/16e72e35-d54b-4d3d-b574-972050954cba.webp")
                .logoWidth(550)
                .logoHeight(190)
                .and()


                .text("Your Order No 000" + invoiceId + " is Confirmed.").h1().center().and()


                .text("Hi " + customerName + ",").and()
                .text("Your order has been received and is now being processed. Estimated delivery " +
                        "within 1-3 working days Kandy & suburbs." +
                        " 3-5 working days outstation. Please find the order information " +
                        "below for your reference.")
                .and()

                .text("ORDER DETAILS").h3().and()
                .html(itemsHtml.toString(), "order items").and()

                .html(totalsHtml, "order totals").and()


                .text("This is an automated email. Please do not reply.").center().and()
                .text("Regards,<br>" + Env.get("app.name") + " Team").and()
                .text("© " + Env.get("app.name") + " - All Rights Reserved").center().and()

                .build();

        message.setContent(email.getHtml(), "text/html; charset=utf-8");
    }
}
