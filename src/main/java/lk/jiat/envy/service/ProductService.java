package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.dto.StockDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ProductService {

    public String getAllProducts(@Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "session expired. please login as an admin!";
        } else {
            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin Not Found!. please register as a admin";
            } else {
                if (!admin.getStatus().getName().equals(String.valueOf(Status.Type.VERIFIED))) {
                    message = "Admin Status Not Found!. please register as a admin";
                } else {
                    Set<Product> productSet = admin.getProducts();
                    if (productSet.isEmpty()) {
                        message = "Product Not Found!";
                    } else {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd");
                        List<ProductDTO> productDTOList = new ArrayList<>();
                        for (Product p : productSet) {
                            ProductDTO productDTO = new ProductDTO();
                            productDTO.setProductId(p.getId());
                            productDTO.setProductName(p.getTitle());

                            List<Stock> stocks = hibernateSession.createQuery("FROM Stock s WHERE s.product=:product ORDER BY s.id DESC", Stock.class)
                                    .setParameter("product", p)
                                    .getResultList();

                            List<StockDTO> stockDTOList = new ArrayList<>();
                            for (Stock s : stocks) {
                                StockDTO stockDTO = new StockDTO();
                                stockDTO.setStockId(s.getId());
                                stockDTO.setProductId(s.getProduct().getId());
                                stockDTO.setQuantity(s.getQuantity());
                                stockDTO.setPrice(s.getPrice());
                                stockDTO.setCreatedAt(formatter.format(s.getCreatedAt()));
                                stockDTOList.add(stockDTO);
                            }
                            productDTO.setStockDTOList(stockDTOList);
                            productDTOList.add(productDTO);
                            stockDTOList.sort(Comparator.comparing(StockDTO::getStockId).reversed());
                            productDTOList.sort(Comparator.comparing(ProductDTO::getProductId).reversed());
                        }

                        responseObject.add("products", AppUtil.GSON.toJsonTree(productDTOList));
                        status = true;
                        message = "product loading successful!";
                    }
                }
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);

    }

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
            message = "please select a  brand";
        } else if (productDTO.getModelId() <= 0) {
            message = "please select a  model";
        } else if (productDTO.getColorId() <= 0) {
            message = "please select a  color";
        } else if (productDTO.getSizeId() == 0) {
            message = "please select a  size";
        } else if (productDTO.getCategoryId() == 0) {
            message = "please select a category";
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
                                    Category category = hibernateSession.find(Category.class, productDTO.getCategoryId());
                                    if (category == null) {
                                        message = "category not found";
                                    } else {

                                        Product product = new Product();
                                        product.setTitle(productDTO.getProductName());
                                        product.setDescription(productDTO.getDescription());
                                        product.setCreatedAt(LocalDateTime.now());
                                        product.setUpdatedAt(LocalDateTime.now());
                                        product.setImages(new ArrayList<>());

                                        product.setColor(color);
                                        product.setSize(size);
                                        product.setCategory(category);
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
                }

                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

}
