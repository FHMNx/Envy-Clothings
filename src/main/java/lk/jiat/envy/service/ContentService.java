package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.dto.StockDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class ContentService {

    public String loadNewProductArrival() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Product> productList = hibernateSession.createQuery("FROM Product p ORDER BY p.createdAt DESC", Product.class)
                .setMaxResults(12)
                .getResultList();

        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Product product : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getId());
            productDTO.setProductName(product.getTitle());
            productDTO.setColorId(product.getColor().getId());
            productDTO.setColorName(product.getColor().getName());
            productDTO.setImages(product.getImages());

            List<StockDTO> stockDTOList = new ArrayList<>();
            for (Stock stock : product.getStocks()) {
                StockDTO stockDTO = new StockDTO();
                stockDTO.setProductId(product.getId());
                stockDTO.setStockId(stock.getId());
                stockDTO.setQuantity(stock.getQuantity());
                stockDTO.setPrice(stock.getPrice());
                stockDTOList.add(stockDTO);
            }

            productDTO.setStockDTOList(stockDTOList);
            productDTOList.add(productDTO);

        }

        hibernateSession.close();

        responseObject.add("newArrivals" , AppUtil.GSON.toJsonTree(productDTOList));
        responseObject.addProperty("status", true);

        return AppUtil.GSON.toJson(responseObject);
    }

    public String loadBrandDetails() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Brand> brandList = hibernateSession.createQuery("FROM Brand b", Brand.class).getResultList();
        responseObject.add("brands", AppUtil.GSON.toJsonTree(ContentService.brands(brandList)));
        hibernateSession.close();

        return AppUtil.GSON.toJson(responseObject);
    }

    public String loadModelDetails(int id) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        if (id <= 0) {
            message = "please select a brand";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Brand brand = hibernateSession.find(Brand.class, id);

            if (brand == null) {
                message = "please provide a correct brand";
            } else {
                List<Model> modelList = hibernateSession.createQuery("FROM Model m WHERE m.brand = : brand", Model.class)
                        .setParameter("brand", brand)
                        .getResultList();

                if (modelList.isEmpty()) {
                    message = "no moels found";
                } else {
                    responseObject.add("models", AppUtil.GSON.toJsonTree(ContentService.models(modelList)));
                    status = true;
                    message = "models data loading successful";
                }
            }
            hibernateSession.close();
        }


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private static List<JsonObject> brands(List<Brand> brandList) {
        List<JsonObject> brandJson = new ArrayList<>();
        for (Brand b : brandList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", b.getId());
            object.addProperty("name", b.getName());
            brandJson.add(object);
        }
        return brandJson;
    }

    private static List<JsonObject> models(List<Model> modelList) {
        List<JsonObject> modelJson = new ArrayList<>();
        for (Model m : modelList) {
            JsonObject object = new JsonObject();
            object.addProperty("id", m.getId());
            object.addProperty("name", m.getName());
            modelJson.add(object);
        }
        return modelJson;
    }

    public String loadProductSpecifications() {
        JsonObject responseObject = new JsonObject();

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        List<Color> colorList = hibernateSession.createQuery("FROM Color c", Color.class).getResultList();
        responseObject.add("color", AppUtil.GSON.toJsonTree(colorList));

        List<Size> sizeList = hibernateSession.createQuery("FROM Size s", Size.class).getResultList();
        responseObject.add("size", AppUtil.GSON.toJsonTree(sizeList));

        List<Category> categoryList = hibernateSession.createQuery("FROM Category c", Category.class).getResultList();
        responseObject.add("category", AppUtil.GSON.toJsonTree(categoryList));

        hibernateSession.close();

        return AppUtil.GSON.toJson(responseObject);
    }

}
