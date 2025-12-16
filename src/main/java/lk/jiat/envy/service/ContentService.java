package lk.jiat.envy.service;

import com.google.gson.JsonObject;
import lk.jiat.envy.entity.Brand;
import lk.jiat.envy.entity.Color;
import lk.jiat.envy.entity.Model;
import lk.jiat.envy.entity.Size;
import lk.jiat.envy.util.AppUtil;
import lk.jiat.envy.util.HibernateUtil;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class ContentService {

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

        hibernateSession.close();

        return AppUtil.GSON.toJson(responseObject);
    }

}
