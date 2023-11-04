package lk.ijse.dep11.web;

import lk.ijse.dep11.web.to.Student;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet({"/students", ""})
@MultipartConfig(location = "/tmp")
public class StudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("connectionPool");
        try (Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT S.*, P.path FROM student AS S LEFT OUTER JOIN picture AS P ON S.id = P.student_id");
            List<Student> studentList = new ArrayList<>();
            while (rst.next()){
                String id = String.format("S%03d",rst.getInt("id"));
                String name = rst.getString("name");
                String address = rst.getString("address");
                String path = rst.getString("path");
                studentList.add(new Student(id, name, address, path));
            }
            req.setAttribute("studentList", studentList);
            getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        Part picture = req.getPart("picture");
        BasicDataSource pool = (BasicDataSource) getServletContext().getAttribute("connectionPool");
        try{
            Connection connection = pool.getConnection();
            connection.setAutoCommit(false);
            try{
                PreparedStatement stmStudent = connection
                        .prepareStatement("INSERT INTO student (name, address) VALUES (?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                stmStudent.setString(1, name);
                stmStudent.setString(2, address);
                stmStudent.executeUpdate();
                if (picture.getSize() > 0){

                }
                connection.commit();
            }catch (Throwable t){
                connection.rollback();
                t.printStackTrace();
            }finally{
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        resp.sendRedirect("/app");

    }
}
