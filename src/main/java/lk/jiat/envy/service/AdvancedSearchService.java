package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.dto.ProductDTO;
import lk.jiat.envy.entity.*;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            productDTO.setProductId(stock.getProduct().getId());
            productDTO.setStockId(stock.getId());
            productDTO.setProductName(stock.getProduct().getTitle());
            productDTO.setPrice(stock.getPrice());
            productDTO.setImages(stock.getProduct().getImages());
            productDTOList.add(productDTO);
        }

        hibernateSession.close();


        responseObject.add("brandList", AppUtil.GSON.toJsonTree(brandList));
        responseObject.add("categoryList", AppUtil.GSON.toJsonTree(categories));
        responseObject.add("colorList", AppUtil.GSON.toJsonTree(colors));
        responseObject.add("sizeList", AppUtil.GSON.toJsonTree(sizes));
        responseObject.add("productList", AppUtil.GSON.toJsonTree(productDTOList));
        responseObject.addProperty("minPrice", minPrice);
        responseObject.addProperty("maxPrice", maxPrice);
        responseObject.addProperty("maxResult", AppUtil.MAX_RESULT_VALUE);


        status = true;


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public String getAdvancedSearchData(JsonObject requestObject) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();

        StringBuilder hql = new StringBuilder("SELECT st FROM Stock st" +
                " JOIN st.product p" +
                " LEFT JOIN p.model m" +
                " LEFT JOIN m.brand b" +
                " LEFT JOIN p.category c" +
                " LEFT JOIN p.color cl" +
                " LEFT JOIN p.size sz" +
                " WHERE 1=1");

        Map<String, Object> params = new HashMap<>();
        if (requestObject.has("brandName") && !requestObject.get("brandName").isJsonNull()) {
            hql.append(" AND b.name = :brandName ");
            params.put("brandName", requestObject.get("brandName").getAsString());
        }

        if (requestObject.has("categoryName") && !requestObject.get("categoryName").isJsonNull()) {
            hql.append(" AND c.name = :categoryName ");
            params.put("categoryName", requestObject.get("categoryName").getAsString());
        }

        if (requestObject.has("colorName") && !requestObject.get("colorName").isJsonNull()) {
            hql.append(" AND cl.name = :colorName ");
            params.put("colorName", requestObject.get("colorName").getAsString());
        }

        if (requestObject.has("sizeName") && !requestObject.get("sizeName").isJsonNull()) {
            hql.append(" AND sz.name = :sizeName ");
            params.put("sizeName", requestObject.get("sizeName").getAsString());
        }

        if (requestObject.has("priceStart") && requestObject.has("priceEnd")) {
            hql.append(" AND st.price BETWEEN :priceStart AND :priceEnd ");
            params.put("priceStart", requestObject.get("priceStart").getAsDouble());
            params.put("priceEnd", requestObject.get("priceEnd").getAsDouble());
        }

        hql.append(" AND st.status.name =:inStockStatus ");
        params.put("inStockStatus", String.valueOf(Status.Type.PENDING));


        //SORTING
        if (requestObject.has("sortProduct")) {
            String sortName = requestObject.get("sortProduct").getAsString();
            switch (sortName) {
                case "latest":
                    hql.append(" ORDER BY st.createdAt DESC");
                    break;

                case "oldest":
                    hql.append(" ORDER BY st.createdAt ASC");
                    break;

                case "name":
                    hql.append(" ORDER BY p.title ASC");
                    break;

                case "price":
                    hql.append(" ORDER BY st.price ASC");
                    break;
            }
        }

        Query<Stock> query = hibernateSession.createQuery(hql.toString(), Stock.class);
        params.forEach(query::setParameter);

        if (requestObject.has("firstResult")) {
            int firstResult = requestObject.get("firstResult").getAsInt();
            query.setFirstResult(firstResult);
            query.setMaxResults(AppUtil.MAX_RESULT_VALUE);
        }

        List<ProductDTO> productDTOList = generateProductDTO(hibernateSession, query);
        responseObject.add("productList", AppUtil.GSON.toJsonTree(productDTOList));

        String countHql = hql.toString().replace("SELECT st", "SELECT COUNT(st)");
        Query<Long> countQuery = hibernateSession.createQuery(countHql, Long.class);
        params.forEach(countQuery::setParameter);
        Long productCount = countQuery.getSingleResult();
        responseObject.addProperty("allProductCount", productCount);

        hibernateSession.close();
        status = true;
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    private List<ProductDTO> generateProductDTO(Session hibernateSession, Query<Stock> stockQuery) {
        List<Stock> stockList = stockQuery.getResultList();

        List<ProductDTO> productDTOList = new ArrayList<>();
        for (Stock stock : stockList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(stock.getProduct().getId());
            productDTO.setStockId(stock.getId());
            productDTO.setProductName(stock.getProduct().getTitle());
            productDTO.setPrice(stock.getPrice());
            productDTO.setImages(stock.getProduct().getImages());
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }

}
