package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.entity.Brand;
import lk.jiat.envy.entity.Category;
import lk.jiat.envy.entity.Color;
import lk.jiat.envy.entity.Size;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;

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

        hibernateSession.close();

        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }
}
