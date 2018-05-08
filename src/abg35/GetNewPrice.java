package abg35;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 * Servlet implementation class GetNewPrice
 */
@WebServlet("/GetNewPrice")
public class GetNewPrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetNewPrice() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONParser parser=new JSONParser();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp=new JSONObject();
		
		try {
			JSONObject json=(JSONObject) parser.parse(getBody(request));
			String symbol=String.valueOf(json.get("symbol"));
			
			String[] companyInfo = getValueForCompany(symbol);
			resp.put("unitPrice", companyInfo[0]);
			resp.put("stockType", companyInfo[1]);
			
			out.print(resp);
			out.flush();

		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public String[] getValueForCompany(String sym) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		String[] response = new String[2];
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT YEAR_OPENING_PRICE AS price, CTYPE as type FROM company_details WHERE COMPANY='"+sym+"'");
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 response[0] = String.valueOf(map.get("price"));
				 response[1] = String.valueOf(map.get("type"));
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
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
