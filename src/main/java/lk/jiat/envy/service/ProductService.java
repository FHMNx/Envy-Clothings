package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class ProductService {

    public String updateProductTable(Product product) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = hibernateSession.beginTransaction();
        try {
            hibernateSession.merge(product);
            transaction.commit();
            status = true;
            message = "Product images uploading successfull";

        } catch (HibernateException e) {
            transaction.rollback();
            message = "Product images uploading failed";
        } finally {
            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }

    public Product getProductById(int id) {
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        Product product = hibernateSession.find(Product.class, id);
        hibernateSession.close();

        return product;
    }

    public String addNewProduct(ProductDTO productDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (productDTO.getProductName() == null) {
            message = "product name is required";
        } else if (productDTO.getProductName().isEmpty()) {
            message = "product name is empty";
        } else if (productDTO.getBrandId() <= 0) {
            message = "invalid brand.select a correct brand";
        } else if (productDTO.getModelId() <= 0) {
            message = "invalid model.select a correct model";
        } else if (productDTO.getColorId() <= 0) {
            message = "invalid color.select a correct color";
        } else if (productDTO.getSizeId() == 0) {
            message = "invalid size.select a correct size";
        } else if (productDTO.getPrice() <= 0) {
            message = "product price can not be less than 0";
        } else if (productDTO.getQuantity() <= 0) {
            message = "quantity can not be less than or equal to 0";
        } else if (productDTO.getDescription() == null) {
            message = "product description is required";
        } else if (productDTO.getDescription().isEmpty()) {
            message = "product description is empty";
        } else {

            HttpSession httpSession = request.getSession(false);
            if (httpSession == null) {
                message = "session expired, please login again";
            } else if (httpSession.getAttribute("admin") == null) {
                message = "please logged in as admin";
            } else {

                Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

                Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

                if (admin == null) {
                    message = "admin account not found";
                } else {
                    if (!admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                        message = "your admin account is not verified";
                    } else {
                        Model model = hibernateSession.find(Model.class, productDTO.getModelId());
                        if (model == null) {
                            message = "model not found";
                        } else {
                            Color color = hibernateSession.find(Color.class, productDTO.getColorId());
                            if (color == null) {
                                message = "color not found";
                            } else {
                                Size size = hibernateSession.find(Size.class, productDTO.getSizeId());
                                if (size == null) {
                                    message = "size not found";
                                } else {
                                    Product product = new Product();
                                    product.setTitle(productDTO.getProductName());
                                    product.setDescription(productDTO.getDescription());
                                    product.setCreatedAt(LocalDateTime.now());
                                    product.setUpdatedAt(LocalDateTime.now());
                                    product.setColor(color);
                                    product.setSize(size);
                                    product.setModel(model);
                                    product.setAdmin(admin);

                                    Stock stock = new Stock();
                                    stock.setProduct(product);
                                    stock.setPrice(productDTO.getPrice());
                                    stock.setQuantity(productDTO.getQuantity());
                                    stock.setCreatedAt(LocalDateTime.now());
                                    stock.setUpdatedAt(LocalDateTime.now());

                                    Status pendingStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                                            .setParameter("name", String.valueOf(Status.Type.PENDING))
                                            .getSingleResult();

                                    Discount defaultDiscount = hibernateSession.createNamedQuery("Discount.findDefault", Discount.class)
                                            .getSingleResult();

                                    stock.setStatus(pendingStatus);
                                    stock.setDiscount(defaultDiscount);

                                    Transaction transaction = hibernateSession.beginTransaction();
                                    try {
                                        hibernateSession.persist(product);
                                        hibernateSession.persist(stock);
                                        transaction.commit();
                                        status = true;

                                        responseObject.addProperty("productId", product.getId());
                                    } catch (HibernateException e) {
                                        transaction.rollback();
                                    }
                                }
                            }
                        }
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
