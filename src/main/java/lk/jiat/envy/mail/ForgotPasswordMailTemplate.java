package lk.jiat.envy.mail;

import io.rocketbase.mail.model.HtmlTextEmail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lk.jiat.envy.util.Env;

public class ForgotPasswordMailTemplate extends Mailable {

    private final String to;
    private final String resetToken;

    public ForgotPasswordMailTemplate(String to, String resetToken) {
        this.to = to;
        this.resetToken = resetToken;
    }

    @Override
    public void build(Message message) throws MessagingException {

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Reset your password - " + Env.get("app.name"));

        String resetUrl = Env.get("app.url") + "/reset-password.html?email=" + to + "&token=" + resetToken;

        HtmlTextEmail email = getEmailTemplateBuilder()

                .header()
                .logo("https://cdn.venngage.com/template/thumbnail/small/16e72e35-d54b-4d3d-b574-972050954cba.webp")
                .logoWidth(570)
                .logoHeight(200)
                .and()

                .text("Password Reset Request").h1().center().and().hr().and()

                .text("We received a request to reset your password for your " + Env.get("app.name") + " account.")
                .center().and()

                .text("Click the button below to set a new password. This link will expire in 10 minute for security reasons.")
                .center().and()

                .button("Reset Password", resetUrl).blue().center().and()

                .text("If the button does not work, click the link below:")
                .center().and()

                .html("You can also reset your password by <a href=\"" + resetUrl + "\">clicking here</a>.",
                        "Reset your password using the provided link.").and()

                .text("If you did NOT request a password reset, please ignore this email. Your account is still safe.")
                .center().and()

                .text("Regards,\n" + Env.get("app.name") + " Team").and()

                .text("© " + Env.get("app.name") + " - All Rights Reserved")
                .center().and()

                .text("This is an automated email. Please do not reply.").center().and()

                .footerText(Env.get("app.name") + "\n" + "Colombo Rd.\n" + "Kandy 1234").and()

                .footerImage("https://www.envypost.co.uk/wp-content/uploads/2022/03/shareimage.jpg")
                .width(100)
                .linkUrl("https://fhmnx.github.io/Portfolio/")
                .and()

                .build();

        message.setContent(email.getHtml(), "text/html; charset=utf-8");
    }
}
