package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.jiat.envy.dto.CartDTO;
import lk.jiat.envy.dto.WishListDTO;
import lk.jiat.envy.entity.Cart;
import lk.jiat.envy.entity.Stock;
import lk.jiat.envy.entity.User;
import lk.jiat.envy.entity.WishList;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import lk.jiat.envy.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WishListService {

    private JsonObject toggleGuestWishList(HttpSession session, int stockId) {

        Set<Integer> wishList = (Set<Integer>) session.getAttribute("sessionWishList");

        if (wishList == null) {
            wishList = new HashSet<>();
        }
        JsonObject responseObject = new JsonObject();

        if (wishList.contains(stockId)) {
            wishList.remove(stockId);
            responseObject.addProperty("message", "Removed from wishlist");
        } else {
            wishList.add(stockId);
            responseObject.addProperty("message", "Added to wishlist");
        }

        session.setAttribute("sessionWishList", wishList);

        responseObject.addProperty("status", true);
        return responseObject;
    }

    private JsonObject toggleUserWishList(User user, int stockId) {

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();

        User dbUser = hibernateSession.find(User.class, user.getId());
        Stock stock = hibernateSession.find(Stock.class, stockId);

        JsonObject responseObject = new JsonObject();

        List<WishList> existing = hibernateSession.createQuery("FROM WishList w WHERE w.user = :user AND w.stock = :stock", WishList.class)
                .setParameter("user", dbUser)
                .setParameter("stock", stock)
                .getResultList();

        if (existing.isEmpty()) {
            // ADD
            WishList wl = new WishList();
            wl.setUser(dbUser);
            wl.setStock(stock);
            hibernateSession.persist(wl);
            responseObject.addProperty("message", "Added to wishlist");
        } else {
            //remove
            for (WishList wl : existing) {
                hibernateSession.remove(wl);
            }
            responseObject.addProperty("message", "Removed from wishlist");
        }

        transaction.commit();
        hibernateSession.close();

        responseObject.addProperty("status", true);
        return responseObject;
    }

    public String toggleWishList(HttpServletRequest request, int stockId) {

        HttpSession httpSession = request.getSession();
        User user = (User) httpSession.getAttribute("user");

        JsonObject response;

        if (user == null) {
            response = toggleGuestWishList(httpSession, stockId);
        } else {
            response = toggleUserWishList(user, stockId);
        }

        return AppUtil.GSON.toJson(response);
    }

    public String getWishListIds(HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        JsonObject response = new JsonObject();

        if (user == null) {
            Set<Integer> ids = (Set<Integer>) session.getAttribute("sessionWishList");
            response.add("ids", AppUtil.GSON.toJsonTree(ids == null ? Set.of() : ids));
        } else {
            Session hSession = HibernateUtil.getSessionFactory().openSession();
            User dbUser = hSession.find(User.class, user.getId());

            var ids = hSession.createQuery("SELECT w.stock.id FROM WishList w WHERE w.user=:user", Integer.class)
                    .setParameter("user", dbUser)
                    .getResultList();

            hSession.close();
            response.add("ids", AppUtil.GSON.toJsonTree(ids));
        }

        response.addProperty("status", true);
        return AppUtil.GSON.toJson(response);
    }

    public String getAllUserWishList(HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        List<WishListDTO> wishListDTOList = new ArrayList<>();

        HttpSession httpSession = request.getSession();
        User sessionUser = (User) httpSession.getAttribute("user");

        if (sessionUser == null) {
            // Guest user
            Set<Integer> sessionWishList = (Set<Integer>) httpSession.getAttribute("sessionWishList");

            if (sessionWishList != null && !sessionWishList.isEmpty()) {

                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                for (Integer stockId : sessionWishList) {
                    Stock stock = hibernateSession.find(Stock.class, stockId);
                    if (stock != null) {
                        WishListDTO dto = new WishListDTO();
                        dto.setStockId(stock.getId());
                        dto.setProductName(stock.getProduct().getTitle());
                        dto.setImage(stock.getProduct().getImages().get(0));
                        dto.setPrice(stock.getPrice());

                        wishListDTOList.add(dto);
                    }
                }

                hibernateSession.close();

                if (!wishListDTOList.isEmpty()) {
                    status = true;
                    message = "WishList items loaded successfully";
                } else {
                    message = "Your wishList is empty";
                }
            } else {
                message = "Your wishList is empty!";
            }

        } else {
            // db user
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            List<WishList> wishListList = hibernateSession.createQuery("FROM WishList w WHERE w.user.id = :id", WishList.class)
                    .setParameter("id", sessionUser.getId())
                    .getResultList();

            if (wishListList != null && !wishListList.isEmpty()) {
                wishListDTOList = generateWishListDTOs(wishListList);
                status = true;
                message = "WishList items loaded successfully.";
            } else {
                message = "Your wishList is empty!";
            }

            hibernateSession.close();
        }

        responseObject.add("wishListItems", AppUtil.GSON.toJsonTree(wishListDTOList));
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }

    public List<WishListDTO> generateWishListDTOs(List<WishList> wishList) {
        List<WishListDTO> wishListDTOList = new ArrayList<>();
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        for (WishList wl : wishList) {
            Stock stock = hibernateSession.find(Stock.class, wl.getStock().getId());

            WishListDTO wishListDTO = new WishListDTO();
            wishListDTO.setWishListId(wl.getId());
            wishListDTO.setStockId(stock.getId());
            wishListDTO.setProductName(stock.getProduct().getTitle());
            wishListDTO.setImage(stock.getProduct().getImages().get(0));
            wishListDTO.setPrice(stock.getPrice());

            wishListDTOList.add(wishListDTO);
        }
        hibernateSession.close();
        return wishListDTOList;
    }

    public String deleteWishListItem(String wishListIdOrStockId, HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (wishListIdOrStockId == null || wishListIdOrStockId.isBlank()) {
            message = "Invalid ID!";
        } else if (!wishListIdOrStockId.matches(Validator.IS_INTEGER)) {
            message = "Invalid ID!";
        } else {
            int id = Integer.parseInt(wishListIdOrStockId);
            HttpSession httpSession = request.getSession();
            User sessionUser = (User) httpSession.getAttribute("user");

            if (sessionUser == null) {
                // SESSION USER → remove by stockId
                Set<Integer> sessionWishList = (Set<Integer>) httpSession.getAttribute("sessionWishList");
                if (sessionWishList != null && !sessionWishList.isEmpty()) {
                    boolean removed = sessionWishList.remove(id); // remove the stockId
                    if (removed) {
                        httpSession.setAttribute("sessionWishList", sessionWishList);
                        status = true;
                        message = "WishList item deleted";
                    } else {
                        message = "Item not found in your wishlist!";
                    }
                } else {
                    message = "Your wishlist is empty!";
                }
            } else {
                // DB USER → remove by wishListId
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                WishList existingList = hibernateSession.createQuery(
                                "FROM WishList w WHERE w.id=:wishListId AND w.user.id=:userId", WishList.class)
                        .setParameter("wishListId", id)
                        .setParameter("userId", sessionUser.getId())
                        .getSingleResultOrNull();
                if (existingList == null) {
                    message = "WishList item not found!";
                } else {
                    Transaction transaction = hibernateSession.beginTransaction();
                    try {
                        hibernateSession.remove(existingList);
                        transaction.commit();
                        status = true;
                        message = "WishList item deleted";
                    } catch (HibernateException e) {
                        transaction.rollback();
                        message = "Failed to delete WishList item: " + e.getMessage();
                    }
                }
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

}
