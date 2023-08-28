package servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet(urlPatterns = "/Item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/posapi", "root", "1234");
            String option = req.getParameter("option");

            switch (option){
                case "getAll":
                    PreparedStatement pstm = connection.prepareStatement("select * from Item");
                    ResultSet rst = pstm.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

                    while (rst.next()) {
                        JsonObjectBuilder itemObject = Json.createObjectBuilder();
                        itemObject.add("code", rst.getString(1));
                        itemObject.add("description", rst.getString(2));
                        itemObject.add("qty", rst.getInt(3));
                        itemObject.add("unitPrice", rst.getDouble(4));
                        arrayBuilder.add(itemObject.build());
                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(arrayBuilder.build());

                    break;

                case "search":
                    String code1 = req.getParameter("ItemCode");
                    PreparedStatement pstm2 = connection.prepareStatement("select * from item where ItemCode=?");
                    pstm2.setObject(1, code1);
                    ResultSet rst2 = pstm2.executeQuery();
                    resp.addHeader("Access-Control-Allow-Origin", "*");

                    JsonObjectBuilder objectBuilder1 = Json.createObjectBuilder();
                    if (rst2.next()) {
                        String ids = rst2.getString(1);
                        String description = rst2.getString(2);
                        String unitPrice = rst2.getString(3);
                        String qty = rst2.getString(4);


                        objectBuilder1.add("code", ids);
                        objectBuilder1.add("description", description);
                        objectBuilder1.add("unitPrice", unitPrice);
                        objectBuilder1.add("qty", qty);


                    }
                    resp.setContentType("application/json");
                    resp.getWriter().print(objectBuilder1.build());

                    break;
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("error", e.getMessage());
            resp.setContentType("application/json");
            resp.setStatus(400);
            resp.getWriter().print(objectBuilder.build());
        }
    }


}
