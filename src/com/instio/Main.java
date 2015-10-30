package com.instio;

import org.eclipse.jetty.server.Request;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

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
                    m.put("password", password);
                    m.put("concessions", concessions);
                    return new ModelAndView(m, "logout.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "login",
                ((request, response) ->  {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");

                    //send down an error code if uname or pass ont entered
                    if (username.isEmpty() || password.isEmpty()) {
                        Spark.halt(403);
                    }

                    User user = new User();
                    if (user == null) {
                        user = new User();
                        user.password = password;
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/choose-food",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    Concession concession = new Concession();
                    concession.id = concessions.size() + 1;
                    concession.name = request.queryParams("foodname");
                    concession.type = request.queryParams("concessiontype");
                    concession.username = username;
                    concessions.add(concession);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/choose-drink",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    Concession concession = new Concession();
                    concession.id = concessions.size() + 1;
                    concession.name = request.queryParams("drinkname");
                    concession.type = request.queryParams("drinktype");
                    concession.username = username;
                    concessions.add(concession);
                    response.redirect("/");
                    return "";
                })
        );
        Spark.post(
                "/Remove-Concession",
                ((request, response) -> {
                    String id = request.queryParams("concessionId");
                    try {
                        int idNum = Integer.valueOf(id);
                        concessions.remove(idNum-1);
                        for (int i = 0; i < concessions.size(); i++) {
                            concessions.get(i).id = i + 1; //changes the number when you delete a beer
                        }
                    }catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );
        /*Spark.post(
                "/edit-order",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    if (username == null) {
                        Spark.halt(403);
                    }

                    String username = request.queryParams("replyId");
                    String text = request.queryParams("text");
                    try {
                        int idNum = Integer.valueOf(idNum);
                        Concession concession = new Concession(concessions.size()username, text);
                        concessions.add(concession);
                    }catch (Exception e) {

                    }
                    response.redirect("/");
                    return "";
                })
        );*/
        Spark.post(
                "/log-out",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
        Spark.get(
                "edit-concession",
                ((request, response) ->  {
                    Session session = request.session();
                    String username = session.attribute("username");

                    HashMap m = new HashMap();
                    m.put("username", username);

                    return new ModelAndView(m, "edit.html");
                }),
                new MustacheTemplateEngine()
        );

    }//public void main
}//public class
