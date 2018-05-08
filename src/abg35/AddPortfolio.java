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
 * Servlet implementation class AddPortfolio
 */
@WebServlet("/AddPortfolio")
public class AddPortfolio extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddPortfolio() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONParser parser=new JSONParser();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp=new JSONObject();
		
		try {
			JSONObject json=(JSONObject) parser.parse(getBody(request));
			String userId=String.valueOf(json.get("userId"));
			String portFolioName=String.valueOf(json.get("portfolioName"));
			String date=String.valueOf(json.get("DOT"));
			String money=String.valueOf(json.get("moneyUSD"));
			JSONArray jsonArr=new JSONArray(String.valueOf(json.get("companies")));
			double totalValue = 0; double dow30Value = 0;
			for(int i=0;i<jsonArr.length();i++)
			{
				 org.json.JSONObject jsonObj=jsonArr.getJSONObject(i);
			     String sym=jsonObj.getString("sym");
			     String[] companyInfo = getValueForCompany(sym);
			     String value = companyInfo[0]; String type = companyInfo[1];
			     String shares=String.valueOf(jsonObj.get("shares"));
		     	 if(type.equals("DOW30"))
			     	dow30Value += (Double.parseDouble(value)*Double.parseDouble(shares));
			     totalValue+=(Double.parseDouble(value)*Double.parseDouble(shares));
			}
			double balance=Double.parseDouble(money)-totalValue;
			if(currentAvailableBalance(Double.parseDouble(money),balance))
			{
				int pid=insertPortfolio(userId,portFolioName,date,balance);
				if(pid>0){
					System.out.println("total: " + totalValue);
					System.out.println("Dow30 value: " + dow30Value);
					if(dow30Value > totalValue * 68 / 100 && dow30Value < totalValue * 72 / 100){
						if(insertPortfolioTransactions(pid,Double.parseDouble(money),date)>0){
							if(insertStockDetails(jsonArr,pid,date)>0){
								resp.put("resp", "Portfolio Created Successfully");
								resp.put("pid", pid);
							}else{
								resp.put("resp", "Some unexpected error");
							}
						}else{
							resp.put("resp", "Some unexpected error");
						}
					}else{
						resp.put("resp", "Money spent on DOW30 stocks must be between 68% - 72%");
					}
				}else{
					resp.put("resp", "Some unexpected error");
				}
			}else{
				resp.put("resp", "Available Balance cannot be less than 10% of the original balance");
			}
			out.print(resp);
			out.flush();

		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int insertStockDetails(JSONArray jsonArr, int pid, String date) {
		Connection con=DatabaseConnection.getConnection();
	     int update=0;
	     int done=0;
		 try {
			 for(int i=0;i<jsonArr.length();i++)
			 {
				org.json.JSONObject jsonObj=jsonArr.getJSONObject(i);
				String sym=jsonObj.getString("sym");
				String[] companyInfo = getValueForCompany(sym);
				String value = companyInfo[0]; String type = companyInfo[1];
				String shares=String.valueOf(jsonObj.get("shares"));
				Statement stmt=con.createStatement();
				stmt.executeUpdate("INSERT INTO stock_details (P_ID,COMPANY,PRICE,SHARES) VALUES ('"+pid+"','"+sym+"','"+value+"','"+shares+"')");
				ResultSet rs = stmt.getGeneratedKeys();
				while(rs.next())
				{
					update=rs.getInt(1);
				}
				done=stmt.executeUpdate("INSERT INTO stock_transactions (S_ID,ACTION,SHARES,TDATE,PRICE,CURRENCY) VALUES ('"+update+"','BUY','"+shares+"','"+date+"','"+value+"','USD')");
			    stmt.close(); 
			}
			con.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return done;

	}

	public int insertPortfolioTransactions(int pid, double d, String date) {
		Connection con=DatabaseConnection.getConnection();
		int update=0;
		try {
			Statement stmt=con.createStatement();
			update=stmt.executeUpdate("INSERT INTO portfolio_transactions (P_ID,ACTION,AMOUNT,TDATE) VALUES ('"+pid+"','ADD','"+d+"','"+date+"')");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return update;
	}

	public int insertPortfolio(String userId, String portFolioName, String date, double balance) {
		Connection con=DatabaseConnection.getConnection();
		int pid = 0;
		try {
			Statement stmt=con.createStatement();
			stmt.executeUpdate("INSERT INTO portfolio (USER_ID,PNAME,BALANCE,DATECREATED) VALUES ('"+userId+"','"+portFolioName+"','"+balance+"','"+date+"')");
			ResultSet rs = stmt.getGeneratedKeys();
			while(rs.next())
			{
				pid=rs.getInt(1);
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pid;
	}

	public boolean currentAvailableBalance(double money, double balance) {
		if(balance>=(0.1*money) || balance < 0)
		{
			return false;
		}
		else
		{
			return true;
		}
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