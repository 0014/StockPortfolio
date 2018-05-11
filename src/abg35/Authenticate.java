package abg35;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetUser
 */
@WebServlet("/Authenticate")
public class Authenticate extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 response.setContentType("text/html");
	     
	     String userName = request.getParameter("userName");
	     String password = request.getParameter("password");
	     int userId = getUser(userName, password);
	     if(userId != 0) {
	    	 request.getRequestDispatcher("GetUser?id=" + userId).forward(request, response);
	     }else {
	    	 request.setAttribute("error", "User Name/Password pair does not match.");
	    	 request.getRequestDispatcher("login.jsp").forward(request, response);
	     }
	}
 
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private int getUser(String userName, String password) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		int userId = 0;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT USER_ID as id FROM users WHERE USERNAME = '" + userName  + "' and PASSWORD = '"+ password +"'");
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 userId=(Integer)map.get("id");
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userId;
	}
}
