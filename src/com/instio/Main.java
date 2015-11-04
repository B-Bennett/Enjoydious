package com.instio;

import org.eclipse.jetty.server.Request;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

public class Main {

    public static User selectUser(Connection conn, String username) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            user = new User();
            user.id = results.getInt("id");
            user.password = results.getString("password");
        }
        return user;
    }

    public static void insertConcession(Connection conn, int id, String name, String type, int amount) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO concession VALUES (NULL, ?, ?, ?, ?)");
        stmt.setInt(1, id);
        stmt.setString(2, name);
        stmt.setString(3, type);
        stmt.setInt(4, amount);
        stmt.execute();
    }

    public static Concession selectConcession(Connection conn, int id) throws SQLException {
        Concession concession = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM concessions INNER JOIN users ON concessions.user_id = users.id WHERE concessions.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            concession = new Concession();
            concession.id = results.getInt("concessions.id");
            concession.name = results.getString("concessions.name");
            concession.type = results.getString("users.name");
            concession.username = results.getString("users.name");
            concession.amount = results.getInt("concession.amount");
        }
        return concession;
    }

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS concession (id IDENTITY, name VARCHAR, type VARCHAR)");

    }

    public static void insertUser(Connection conn, String username, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static ArrayList<Concession> selectConcessions(Connection conn, int id) throws SQLException {
        ArrayList<Concession> concessions = new ArrayList();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM concessions INNER JOIN users ON concessions.user_id = users.id WHERE concessions.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            Concession concession = new Concession();
            concession.id = results.getInt("concessions.id");
            concession.name = results.getString("concessions.name");
            concession.type = results.getString("users.name");
            concession.username = results.getString("users.name");
            concession.amount = results.getInt("concession.amount");
        }
        return concessions;
    }

    public static ArrayList<Concession> main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        //public static void main(String[] args) {
        //ArrayList<Concession> concessions = new ArrayList();

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");//read username
                    String password = request.queryParams("password");

                    ArrayList<Concession> concessions = selectConcessions(conn, -1);
                    HashMap m = new HashMap();
                    m.put("username", username);
                    m.put("password", password);
                    m.put("concessions", concessions);
                    if (username == null) { //if user is not logged in
                        return new ModelAndView(m, "login.html");
                    }
                    return new ModelAndView(m, "logout.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/login",
                ((request, response) -> {
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
                    //concession.id = concessions.size() + 1;
                    concession.id = Integer.valueOf("concessiontype");
                    concession.name = request.queryParams("foodname");
                    concession.type = request.queryParams("concessiontype");
                    //concession.username = username;
                    concession.amount = Integer.valueOf("number");
                    //concessions.add(concession);
                    insertConcession(conn, concession.id, concession.name, concession.type, concession.amount);
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
                "/remove-concession",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    try {
                        int idNum = Integer.valueOf(id);
                        concessions.remove(idNum - 1);
                        for (int i = 0; i < concessions.size(); i++) {
                            concessions.get(i).id = i + 1; //changes the number when you delete a beer
                        }
                    } catch (Exception e) {

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
                "/edit-concession",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    String id = request.queryParams("id");

                    HashMap m = new HashMap();
                    m.put("username", username);
                    m.put("id", id);


                    return new ModelAndView(m, "edit.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-concession",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    String name = request.queryParams("foodname");
                    String type = request.queryParams("concessiontype");
                    try {
                        int idNum = Integer.valueOf(id);

                        //Concession concession = concessions.get(idNum - 1);
                        //concession.name = name;
                        //concession.type = type;
                    } catch (Exception e) {

                    }
                    response.redirect("/");

                    return "";
                })
        );

    }//public void main
}//public class
