package lk.jiat.envy.middleware;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;

import java.io.IOException;

@WebFilter("/*")
public class RememberMeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("remember_me".equals(cookie.getName())) {

                        String token = cookie.getValue();

                        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                        User user = hibernateSession.createQuery("FROM User u WHERE u.rememberToken = :token AND u.rememberTokenExpiry > CURRENT_TIMESTAMP", User.class)
                                .setParameter("token", token)
                                .getSingleResultOrNull();

                        if (user != null) {
                            request.getSession(true).setAttribute("user", user);
                        }

                        hibernateSession.close();
                    }
                }
            }
        }

        chain.doFilter(req, res);
    }
}
