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
 * Servlet implementation class BuySellStocks
 */
@WebServlet("/BuySellStocks")
public class BuySellStocks extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		JSONParser parser = new JSONParser();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp = new JSONObject();
		try {
			JSONObject json = (JSONObject) parser.parse(getBody(request));
			double balance = Double.parseDouble(String.valueOf(json.get("remainingBalance")));
			String portfolioId=String.valueOf(json.get("portfolioId"));
			String dateTime=String.valueOf(json.get("dateTime"));
			JSONArray jsonArr=new JSONArray(String.valueOf(json.get("companies")));
			for(int i=0; i<jsonArr.length(); i++)
			{
				 org.json.JSONObject jsonObj=jsonArr.getJSONObject(i);
				 String symbol=jsonObj.getString("symbol");
				 int numberOfShares=Integer.parseInt(jsonObj.getString("numberOfShares"));
				 double totalValue=Double.parseDouble(jsonObj.getString("totalValue"));
				 int[] stockInfo = getAvailableShares(portfolioId, symbol);
				 int stockId = stockInfo[0]; int availableShares = stockInfo[1];
				 String action = "";
				 if(numberOfShares > 0) {
					 action = "BUY";
					 if(availableShares != 0) {
						 buyStocks(portfolioId, symbol, availableShares, numberOfShares, totalValue/numberOfShares, "add");
					 }else {
						 buyStocks(portfolioId, symbol, availableShares, numberOfShares, totalValue/numberOfShares, "new");
					 }
				 }else {
					action = "SELL";
					numberOfShares *= -1; totalValue *= -1;
					if(availableShares == numberOfShares) {
						sellStocks(portfolioId, symbol, availableShares, numberOfShares, "delete");
					}else {
						sellStocks(portfolioId, symbol, availableShares, numberOfShares, "update");
					}
				 }
				if(!insertStockTransaction(stockId, numberOfShares, dateTime, totalValue / numberOfShares, action)) {
					resp.put("error", "Something went wrong inserting the transaction record.");

					out.print(resp);
					out.flush();
					return;
				}
			}
			if(!updateBalance(portfolioId, balance)){
				resp.put("error", "Something went wrong updating the balance.");

				out.print(resp);
				out.flush();
				return;
			}
			resp.put("resp", "Done.");
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
	
	public boolean updateBalance(String portfolioId, double balance) {
		Connection con=DatabaseConnection.getConnection();
		try {
			Statement stmt=con.createStatement();
			stmt.executeUpdate("UPDATE portfolio SET BALANCE = " + balance
						+ " WHERE P_ID=" + portfolioId);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insertStockTransaction(int stockId, int numberOfShares, String date, double stockUnitePrice, String action) {
		Connection con=DatabaseConnection.getConnection();
		try {
			Statement stmt=con.createStatement();
			stmt.executeUpdate("INSERT INTO stock_transactions (S_ID,ACTION,SHARES,TDATE,PRICE,CURRENCY) VALUES ('"+stockId+"','"+ action + "','"+numberOfShares+"','"+date+"','"+stockUnitePrice+"','USD')");
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean buyStocks(String portfolioId, String symbol, int availableShares, int numberOfShares, double unitPrice, String action) {
		Connection con=DatabaseConnection.getConnection();
		try {
			Statement stmt=con.createStatement();
			if(action == "new") {
				stmt.executeUpdate("INSERT INTO stock_details (P_ID,COMPANY,PRICE,SHARES) VALUES ('"+portfolioId+"','"+symbol+"','"+unitPrice+"','"+numberOfShares+"')");
			}else if(action == "add") {
				stmt.executeUpdate("UPDATE stock_details SET SHARES = " + (availableShares + numberOfShares)
						+ " WHERE COMPANY='" + symbol + "' AND P_ID=" + portfolioId);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sellStocks(String portfolioId, String symbol, int availableShares, int numberOfShares, String action) {
		Connection con=DatabaseConnection.getConnection();
		try {
			Statement stmt=con.createStatement();
			if(action == "delete") {
				stmt.executeUpdate("DELETE FROM stock_details "
						+ "WHERE COMPANY='" + symbol + "' AND P_ID=" + portfolioId);
			}else if(action == "update") {
				stmt.executeUpdate("UPDATE stock_details SET SHARES = " + (availableShares - numberOfShares)
						+ " WHERE COMPANY='" + symbol + "' AND P_ID=" + portfolioId);
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int[] getAvailableShares(String portfolioId, String symbol){
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		int[] stockInfo = new int[] {0,0};
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT SHARES, S_ID FROM stock_details "
					+ "WHERE COMPANY='"+symbol+"' AND P_ID=" + portfolioId);
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 stockInfo[0]=Integer.parseInt(String.valueOf(map.get("S_ID")));
				 stockInfo[1]=Integer.parseInt(String.valueOf(map.get("SHARES")));
			 }
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stockInfo;
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
