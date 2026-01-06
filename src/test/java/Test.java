import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lk.jiat.envy.entity.Address;
import lk.jiat.envy.entity.Status;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.mail.Mailable;
import lk.jiat.envy.mail.VerificationMailTemplate;
import lk.jiat.envy.provider.MailServiceProvider;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.util.PayHereUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class Test {
    public static void main(String[] args) {

        System.out.println(PayHereUtil.generateHash("#0001", 1500));

//        MailServiceProvider.getInstance().start();
//
//        Mailable testMail = new Mailable() {
//            @Override
//            public void build(Message message) throws MessagingException {
//                message.setRecipient(Message.RecipientType.TO, new InternetAddress("recipient@gmail.com"));
//                message.setSubject("Test Email from Java Viva");
//                message.setText("Hello! This is a test email.");
//            }
//        };
//
//        MailServiceProvider.getInstance().sendMail(testMail);

//        SessionFactory session = HibernateUtil.getSessionFactory();

//        VerificationMailTemplate verificationMailTemplate = new VerificationMailTemplate("fhmnx35888@gmail.com", "123456");
//        MailServiceProvider.getInstance().sendMail(verificationMailTemplate);

//       String s =  AppUtil.generateCode();
//        System.out.println(s);

//        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
//
//            User user = s.createQuery("FROM User u WHERE u.id=:id", User.class)
//                    .setParameter("id", 3)
//                    .getSingleResult();
//
//
//
//        }
    }
}
