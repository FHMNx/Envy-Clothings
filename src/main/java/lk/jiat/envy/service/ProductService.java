package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.dto.SearchResponseDTO;
import lk.jiat.envy.dto.StockDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ProductService {

    public String getBasicSearchData(String title) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (!title.isBlank()) {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            Status inStockStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                    .setParameter("name", String.valueOf(Status.Type.PENDING))
                    .getSingleResult();

            List<Stock> stockList = hibernateSession.createQuery("FROM Stock s WHERE s.product.title LIKE :title AND s.status=:status", Stock.class)
                    .setParameter("title", "%" + title + "%")
                    .setParameter("status", inStockStatus)
                    .getResultList();

            if (stockList.isEmpty()) {
                message = "Product not found";
            } else {

                List<SearchResponseDTO> searchResponseDTOList = new ArrayList<>();
                for (Stock stock : stockList) {
                    SearchResponseDTO dto = new SearchResponseDTO();
                    dto.setStockId(stock.getId());
                    dto.setTitle(stock.getProduct().getTitle());
                    dto.setPrice(stock.getPrice());
                    dto.setImage(stock.getProduct().getImages().get(0));

                    searchResponseDTOList.add(dto);
                }
                responseObject.add("basicSearchData", AppUtil.GSON.toJsonTree(searchResponseDTOList));
                status = true;
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String updateProduct(ProductDTO productDTO, @Context HttpServletRequest request) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (productDTO.getProductId() <= 0) {
            message = "invalid product id";
        } else if (productDTO.getProductName() == null || productDTO.getProductName().isEmpty()) {
            message = "product name is required";
        } else if (productDTO.getBrandId() <= 0) {
            message = "please select a brand";
        } else if (productDTO.getModelId() <= 0) {
            message = "please select a model";
        } else if (productDTO.getColorId() <= 0) {
            message = "please select a color";
        } else if (productDTO.getSizeId() <= 0) {
            message = "please select a size";
        } else if (productDTO.getCategoryId() <= 0) {
            message = "please select a category";
        } else if (productDTO.getPrice() <= 0) {
            message = "product price must be greater than 0";
        } else if (productDTO.getQuantity() <= 0) {
            message = "quantity must be greater than 0";
        } else if (productDTO.getDescription() == null || productDTO.getDescription().isEmpty()) {
            message = "product description is required";
        } else {

            HttpSession httpSession = request.getSession(false);
            if (httpSession == null || httpSession.getAttribute("admin") == null) {
                message = "session expired, please login again";
            } else {

                Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");
                Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = hibernateSession.beginTransaction();

                try {

                    Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());
                    if (admin == null || !admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                        message = "unauthorized admin";
                    } else {

                        Product product = hibernateSession.find(Product.class, productDTO.getProductId());
                        if (product == null) {
                            message = "product not found";
                        } else {

                            Model model = hibernateSession.find(Model.class, productDTO.getModelId());
                            Color color = hibernateSession.find(Color.class, productDTO.getColorId());
                            Size size = hibernateSession.find(Size.class, productDTO.getSizeId());
                            Category category = hibernateSession.find(Category.class, productDTO.getCategoryId());

                            if (model == null || color == null || size == null || category == null) {
                                message = "invalid product references";
                            } else {

                                Status inStockStatus = hibernateSession.find(Status.class, 15);

                                product.setTitle(productDTO.getProductName());
                                product.setDescription(productDTO.getDescription());
                                product.setUpdatedAt(LocalDateTime.now());
                                product.setModel(model);
                                product.setColor(color);
                                product.setSize(size);
                                product.setCategory(category);

                                Stock stock = product.getStocks().iterator().next();
                                stock.setPrice(productDTO.getPrice());
                                stock.setQuantity(productDTO.getQuantity());
                                stock.setStatus(inStockStatus);
                                stock.setUpdatedAt(LocalDateTime.now());

                                transaction.commit();
                                status = true;
                                responseObject.addProperty("productId", product.getId());
                            }
                        }
                    }

                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "product update failed";
                } finally {
                    hibernateSession.close();
                }
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String updateProductImages(int productId, List<FormDataBodyPart> images, List<Integer> indexes, HttpServletRequest request, ServletContext context) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            message = "Session expired. Please login again.";
        } else {

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            try {
                Product product = hibernateSession.find(Product.class, productId);
                if (product == null) {
                    message = "Product not found";
                } else {

                    List<String> existingImages = product.getImages();
                    FileUploadService uploadService = new FileUploadService(context);

//                    System.out.println("Received images: " + images.size());
//                    System.out.println("Received indexes: " + indexes);


                    for (int x = 0; x < images.size(); x++) {
                        FormDataBodyPart part = images.get(x);
                        int index = indexes.get(x);

                        InputStream is = part.getEntityAs(InputStream.class);
                        ContentDisposition cd = part.getContentDisposition();

                        FileUploadService.FileItem file = uploadService.uploadFile("product/" + productId, is, cd);
                        while (existingImages.size() <= index) {
                            existingImages.add("");
                        }
                        existingImages.set(index, file.getRelativePath());
                    }
//                    System.out.println("Updated images: " + existingImages);

                    product.setUpdatedAt(LocalDateTime.now());
                    hibernateSession.merge(product);

                    transaction.commit();
                    status = true;
                    message = "Product images updated successfully";
                }
            } catch (Exception e) {
                transaction.rollback();
                message = "Image update failed";
            } finally {
                hibernateSession.close();
            }
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String getSelectedProduct(int productId) {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Product product = hibernateSession.find(Product.class, productId);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setProductName(product.getTitle());
        productDTO.setBrandId(product.getModel().getBrand().getId());
        productDTO.setModelId(product.getModel().getId());
        productDTO.setColorId(product.getColor().getId());
        productDTO.setSizeId(product.getSize().getId());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setDescription(product.getDescription());

        List<StockDTO> stockDTOList = new ArrayList<>();
        for (Stock stock : product.getStocks()) {
            StockDTO stockDTO = new StockDTO();
            stockDTO.setProductId(stock.getProduct().getId());
            stockDTO.setStockId(stock.getId());
            stockDTO.setQuantity(stock.getQuantity());
            stockDTO.setPrice(stock.getPrice());

            stockDTOList.add(stockDTO);
        }

        productDTO.setStockDTOList(stockDTOList);
        productDTO.setImages(product.getImages());

        responseObject.add("editProduct", AppUtil.GSON.toJsonTree(productDTO));
        hibernateSession.close();

        return AppUtil.GSON.toJson(responseObject);
    }

    public String getSingleProduct(int productId) {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        Product product = hibernateSession.find(Product.class, productId);

        if (product == null) {
            hibernateSession.close();
            responseObject.addProperty("status", false);
            responseObject.addProperty("message", "product not found");
            return AppUtil.GSON.toJson(responseObject);
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(productId);
        productDTO.setProductName(product.getTitle());
        productDTO.setBrandName(product.getModel().getBrand().getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setColorName(product.getColor().getName());
        productDTO.setColorId(product.getColor().getId());
        productDTO.setSizeId(product.getSize().getId());
        productDTO.setSizeName(product.getSize().getName());

        List<StockDTO> stockDTOList = new ArrayList<>();
        for (Stock stock : product.getStocks()) {
            StockDTO stockDTO = new StockDTO();
            stockDTO.setProductId(stock.getProduct().getId());
            stockDTO.setStockId(stock.getId());
            stockDTO.setQuantity(stock.getQuantity());
            stockDTO.setPrice(stock.getPrice());
            stockDTO.setStatus(stock.getStatus().getName());
            stockDTOList.add(stockDTO);
        }

        productDTO.setStockDTOList(stockDTOList);
        productDTO.setImages(product.getImages());

        responseObject.addProperty("status", true);
        responseObject.add("singleProduct", AppUtil.GSON.toJsonTree(productDTO));
        hibernateSession.close();
        return AppUtil.GSON.toJson(responseObject);
    }

    public String getAllProducts(HttpServletRequest request, int page, int size) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);
        List<ProductDTO> productDTOList = new ArrayList<>();

        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "Session expired. Please login as an admin!";
        } else {
            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin not found! Please register as an admin.";
            } else if (!admin.getStatus().getName().equals(Status.Type.VERIFIED.name())) {
                message = "Admin status not verified!";
            } else {

                //PAGINATION
                int offset = (page - 1) * size;
                Long totalProducts = hibernateSession.createQuery("SELECT COUNT(p.id) FROM Product p WHERE p.admin = :admin", Long.class)
                        .setParameter("admin", admin)
                        .uniqueResult();

                List<Product> productList = hibernateSession.createQuery("FROM Product p WHERE p.admin = :admin ORDER BY p.id DESC", Product.class)
                        .setParameter("admin", admin)
                        .setFirstResult(offset)
                        .setMaxResults(size)
                        .getResultList();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM dd");

                for (Product p : productList) {
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
                        stockDTO.setStatusId(s.getStatus().getId());
                        stockDTO.setCreatedAt(formatter.format(s.getCreatedAt()));
                        stockDTOList.add(stockDTO);
                    }
                    productDTO.setStockDTOList(stockDTOList);
                    productDTOList.add(productDTO);
                }

                status = true;
                message = productDTOList.isEmpty() ? "No products found!" : "Product loading successful!";

                responseObject.addProperty("currentPage", page);
                responseObject.addProperty("pageSize", size);
                responseObject.addProperty("totalProducts", totalProducts);
                responseObject.addProperty("totalPages", (int) Math.ceil((double) totalProducts / size));
            }

            hibernateSession.close();
        }

        responseObject.add("products", AppUtil.GSON.toJsonTree(productDTOList));
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
            message = "Product images uploading successful";

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

                                        Status inStockStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                                                .setParameter("name", String.valueOf(Status.Type.IN_STOCK))
                                                .getSingleResult();

                                        Discount defaultDiscount = hibernateSession.createNamedQuery("Discount.findDefault", Discount.class)
                                                .getSingleResult();

                                        stock.setStatus(inStockStatus);
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

    public String deleteProduct(HttpServletRequest request, int productId) {

        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("admin") == null) {
            message = "Session expired. Please login again!";
        } else {

            Admin sessionAdmin = (Admin) httpSession.getAttribute("admin");
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

            Admin admin = hibernateSession.find(Admin.class, sessionAdmin.getId());

            if (admin == null) {
                message = "Admin not found!";
            } else {

                Product product = hibernateSession.find(Product.class, productId);

                if (product == null) {
                    message = "Product not found!";
                } else if (product.getAdmin().getId() != (admin.getId())) {
                    message = "Unauthorized action!";
                } else {

                    Transaction transaction = hibernateSession.beginTransaction();

                    for (Stock stock : product.getStocks()) {
                        hibernateSession.remove(stock);
                    }

                    hibernateSession.remove(product);

                    transaction.commit();

                    status = true;
                    message = "Product deleted successfully!";
                }
            }

            hibernateSession.close();
        }

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);

        return AppUtil.GSON.toJson(responseObject);
    }

}
