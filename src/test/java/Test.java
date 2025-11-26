import lk.jiat.envy.entity.Status;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Test {
    public static void main(String[] args) {
//       String s =  AppUtil.generateCode();
//        System.out.println(s);

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Status.Type[] values = Status.Type.values();
            Transaction transaction = s.beginTransaction();
            for (Status.Type t : values) {
                Status status = new Status();
                status.setName(t.name());
                s.persist(status);
            }
            transaction.commit();
        }
    }
}
