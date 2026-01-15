package lk.jiat.envy.mail;

import io.rocketbase.mail.model.HtmlTextEmail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lk.jiat.envy.util.Env;

public class VerificationMailTemplate extends Mailable {
    private final String to;
    private final String verificationCode;

    public VerificationMailTemplate(String to, String verificationCode) {
        this.to = to;
        this.verificationCode = verificationCode;
    }

    @Override
    public void build(Message message) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject("Email verification code: " + Env.get("app.name"));

        String appUrl = Env.get("app.url");
        String verifyUrl = appUrl + "/verify-account.html?email=" + to + "&verificationCode=" + verificationCode;

        HtmlTextEmail htmlTextEmail = getEmailTemplateBuilder()

                .header()
                .logo("https://cdn.venngage.com/template/thumbnail/small/16e72e35-d54b-4d3d-b574-972050954cba.webp").logoWidth(550)
                .logoHeight(190)
                .and()

                .text("Welcome To," + Env.get("app.name")).h1().center().and()
                .hr().and()
                .text("Verify Your Email Address").h3().center().and()

                .text("We're excited to have you onboard. Please verify your email to activate your account.").center().and()

                .text(verificationCode).h1().center().and()
                .button("Verify Account", verifyUrl).blue().center().and()

                .text("If the button does not work, click below link:").center().and()

                .html("Having trouble with the <strong>Verify Account</strong> button? No worries - you can <a href=" + verifyUrl + ">click here</a> to verify your account and start shopping at " + Env.get("app.name") + ".",
                        " Click this link to verify your account and start exploring all the amazing products at" + Env.get("app.name")).and()

                .html("If you have any questions, feel free to <a href=\"mailto:{{support_email}}\">email our customer success team</a>. (We're lightning quick at replying.) We also offer <a href=\"{{live_chat_url}}\">live chat</a> during business hours.",
                        "If you have any questions, feel free to email our customer success team").and()

                .text("Cheers,\n" +
                        "Envy Clothings Team").and()
                .text("© " + Env.get("app.name") + " - All Rights Reserved").center().and()
                .text("This is an automated email. Please do not reply.").center().and()
                .footerText("Envy Clothings\n" +
                        "Colombo Rd.\n" +
                        "Kandy 1234").and()
                .footerImage("https://www.envypost.co.uk/wp-content/uploads/2022/03/shareimage.jpg").width(100).linkUrl("https://fhmnx.github.io/Portfolio/").and()
                .build();

        message.setContent(htmlTextEmail.getHtml(), "text/html; charset=utf-8");
    }
}
