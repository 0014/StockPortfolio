package abg35;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/ModifyPortfolio")
public class ModifyPortfolio extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONParser parser=new JSONParser();
		JSONObject json=new JSONObject();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp=new JSONObject();
		try {
			json = (JSONObject) parser.parse(getBody(request));
			String userId=String.valueOf(json.get("userId"));
			String pId=String.valueOf(json.get("pId"));
			String pName=String.valueOf(json.get("pName"));
			Connection conn=DatabaseConnection.getConnection();
			Statement stmt=conn.createStatement();
			int rs=stmt.executeUpdate("UPDATE portfolio SET PNAME ='"+pName+"' WHERE USER_ID='"+userId+"' and P_ID='"+pId+"' ");
			if(rs!=0)
			{
				resp.put("resp", "Portfolio updated successfully");
			}
			else
			{
				resp.put("resp", "Some unexpected error");
			}
			
			out.print(resp);
			out.flush();
			conn.close();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getBody(HttpServletRequest request)
	{
		StringBuilder buffer = new StringBuilder();
	    try {
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
			    buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    String data = buffer.toString();
		return data;
	}
}
