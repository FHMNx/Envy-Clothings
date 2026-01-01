package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchService {

    public String getAllProductData() {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        List<Brand> brands = hibernateSession.createQuery("FROM Brand b", Brand.class).getResultList();
        List<JsonObject> brandList = ContentService.brands(brands);

        List<Category> categories = hibernateSession.createQuery("FROM Category c", Category.class).getResultList();

        List<Color> colors = hibernateSession.createQuery("FROM Color c", Color.class).getResultList();

        List<Size> sizes = hibernateSession.createQuery("FROM Size s", Size.class).getResultList();

        Double minPrice = hibernateSession.createQuery("SELECT MIN(s.price) FROM Stock s", Double.class).uniqueResult();
        Double maxPrice = hibernateSession.createQuery("SELECT MAX(s.price) FROM Stock s", Double.class).uniqueResult();

        Status inStockStatus = hibernateSession.createNamedQuery("Status.findByName", Status.class)
                .setParameter("name", String.valueOf(Status.Type.PENDING))
                .getSingleResult();

        Query<Stock> stockQuery = hibernateSession.createQuery("FROM Stock s WHERE s.status = :status ORDER BY s.id ASC", Stock.class)
                .setParameter("status", inStockStatus);

        responseObject.addProperty("allProductCount", stockQuery.getResultList().size());


        List<Stock> stockList = stockQuery.getResultList();

        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Stock stock : stockList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(stock.getId());
            productDTO.setProductName(stock.getProduct().getTitle());
            productDTO.setPrice(stock.getPrice());
            productDTO.setImages(stock.getProduct().getImages());
            productDTOList.add(productDTO);
        }

        hibernateSession.close();


        responseObject.add("brandList" , AppUtil.GSON.toJsonTree(brandList));
        responseObject.add("categoryList" , AppUtil.GSON.toJsonTree(categories));
        responseObject.add("colorList" , AppUtil.GSON.toJsonTree(colors));
        responseObject.add("sizeList" , AppUtil.GSON.toJsonTree(sizes));
        responseObject.add("productList" , AppUtil.GSON.toJsonTree(productDTOList));
        responseObject.addProperty("minPrice", minPrice);
        responseObject.addProperty("maxPrice", maxPrice);
        responseObject.addProperty("maxResult", AppUtil.MAX_RESULT_VALUE);


        status = true;


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }
}
