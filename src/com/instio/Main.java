package com.instio;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ArrayList<Concession> concessions = new ArrayList();

        Spark.get (
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");//read username
                    String password = request.queryParams("password");
                    if (username == null) { //if user is not logged in
                        return new ModelAndView(new HashMap(), "login.html");
                    }
                    HashMap m = new HashMap();
                    m.put("username", username);
                    m.put("concessions", concessions);
                    return new ModelAndView(m, "logout.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/choose-food",
                ((request, response) -> {
                    Concession concession = new Concession();
                    concession.id = concessions.size() + 1;
                    concession.name = request.queryParams("foodname");
                    concession.type = request.queryParams("concessiontype");
                    concessions.add(concession);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/choose-drink",
                ((request, response) -> {
                    Concession concession = new Concession();
                    concession.id = concessions.size() + 1;
                    concession.name = request.queryParams("drinkname");
                    concession.type = request.queryParams("drinktype");
                    concessions.add(concession);
                    response.redirect("/");
                    return "";
                })
        );

    }//public void main
}//public class
