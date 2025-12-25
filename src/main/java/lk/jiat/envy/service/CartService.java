//package lk.jiat.envy.service;
//
//import com.google.gson.JsonObject;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lk.jiat.envy.entity.Cart;
//import lk.jiat.envy.entity.Stock;
//import lk.jiat.envy.entity.User;
//import lk.jiat.envy.util.HibernateUtil;
//import lk.jiat.envy.validation.Validator;
//import org.hibernate.Session;
//
//import java.util.List;
//
//public class CartService {
//
//    public String addToCart(String sId, String qty, HttpServletRequest request) {
//        JsonObject responseObject = new JsonObject();
//        boolean status = false;
//        String message = "";
//
//        if (sId == null || sId.isBlank()) {
//            message = "Product ID not found!";
//        } else if (!sId.matches(Validator.IS_INTEGER)) {
//            message = "Invalid product Id!";
//        } else if (qty == null || qty.isBlank()) {
//            message = "Product quantity not found!";
//        } else if (!qty.matches(Validator.IS_INTEGER)) {
//            message = "Invalid quantity value!";
//        } else {
//            int stockId = Integer.parseInt(sId);
//            int requestQty = Integer.parseInt(qty);
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            Stock stock = hibernateSession.find(Stock.class, stockId);
//            if (stock == null) {
//                message = "Product not found!";
//            } else {
//
//                HttpSession httpSession = request.getSession();
//                User user = (User) httpSession.getAttribute("user");
//                List<Cart> sessionCart = getSessionAttribute(httpSession);
//                if (user == null) {
//                    // User not logged in
//                    if (sessionCart == null) {
//                        // first time
//                        // no session cart -> create new session cart for user
//                        return guestUserFirstTime(stock, requestQty, httpSession);
//                    } else {
//                        // second time
//                        // session cart exists -> add new cart item to list
//                        return guestUserSecondTime(stock, requestQty, httpSession);
//                    }
//                } else {
//                    // User already logged
//                    return loggedUserCart(stock, requestQty, httpSession, hibernateSession);
//                }
//            }
//            hibernateSession.close();
//        }
//
//        responseObject.addProperty("status", status);
//        responseObject.addProperty("message", message);
//        return AppUtil.GSON.toJson(responseObject);
//    }
//
//}
