package abg35;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Servlet implementation class CashOperation
 */
@WebServlet("/CashOperation")
public class CashOperation extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONParser parser=new JSONParser();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		JSONObject resp=new JSONObject();
		try {
			JSONObject json=(JSONObject) parser.parse(getBody(request));
			int portfolioid=Integer.parseInt(String.valueOf(json.get("pid")));
			double cash = Double.parseDouble(String.valueOf(json.get("amount")));
			String action=String.valueOf(json.get("action"));
		    
		    if (action.equals("D")) {
		    	String result = DepositCash(portfolioid,cash);
		    	if(!result.equals("Success")) {
		    		resp.put("error", result);
		    	}
	    	}else if (action.equals("W")) {
	    		String result = WithDrawCash(portfolioid,cash);
				if(!result.equals("Success")) {
		    		resp.put("error", result);
		    	}
	    	}else {
	    		resp.put("error", "Entered action code is not correct.");
	    	}
		}catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.put("error", "Something went wrong during transaction.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.put("error", "Something went parsing the json request.");
		}
		resp.put("resp", "Done.");
		out.print(resp);
		out.flush();
	}
	
	public static double getPortfolioBalance(int pid){
		double portfoliobalance = 0;
		Connection con=DatabaseConnection.getConnection();
		try{  
    		//here sonoo is database name, root is username and password  
    		Statement stmt=con.createStatement();  
    		ResultSet rs=stmt.executeQuery("select * from portfolio where p_id LIKE '%" + pid +"%'"); 
    		while(rs.next())  
    		portfoliobalance += rs.getDouble(4);
    		}
		catch(Exception e){ 
			
		}  
		return portfoliobalance;

    }
	public static double getInvestedCash(int pid) throws ClassNotFoundException, SQLException{
		double DepositedCash = 0;
		Connection con=DatabaseConnection.getConnection();
	    PreparedStatement statement =  con.prepareStatement("select sum(amount) from portfolio_transactions where p_id LIKE '%" + pid +"%'"+ " AND action LIKE '%A%'");
	     ResultSet result = statement.executeQuery();
 		while(result.next())  {
 			DepositedCash = result.getDouble(1);
 		}
 		con.close();
		return DepositedCash;
}
	
	public static double getWithDrawnCash(int pid) throws ClassNotFoundException, SQLException{
		double WithDrawnCash = 0;
		Connection con=DatabaseConnection.getConnection();
	    PreparedStatement statement =  con.prepareStatement("select sum(amount) from portfolio_transactions where p_id LIKE '%" + pid +"%'"+ " AND action LIKE '%W%'");
	     ResultSet result = statement.executeQuery();
 		while(result.next())  {
 			WithDrawnCash = result.getDouble(1);
 		}
 		con.close();
		return WithDrawnCash;
}
	public static double getTransactionBalance(int pid) throws ClassNotFoundException, SQLException{
		double positive = getInvestedCash(pid);
		double negative = getWithDrawnCash(pid);
		
		return positive - negative;
	}
	public static String DepositCash(int pid, double amount) throws SQLException, ClassNotFoundException {
		if(AllowedToDeposit(pid,amount)){
			Connection con=DatabaseConnection.getConnection();	
			int update = 0;
			PreparedStatement statement =  con.prepareStatement("SELECT MAX(T_ID) AS maxtid FROM portfolio_transactions");
		    ResultSet result = statement.executeQuery();
	 		while(result.next())  {
	 			update = result.getInt(1) + 1;
	 		}
	 		statement.close();
			Statement stmt=con.createStatement();
			
			System.out.println(update);
			double newbalance =getPortfolioBalance(pid) + amount;
			stmt.executeUpdate("INSERT INTO portfolio_transactions (T_ID,P_ID,ACTION,AMOUNT,TDATE) VALUES ('"+update+"','"+pid+"','ADD','"+amount+"',CURRENT_TIMESTAMP)");
			String sql = "UPDATE portfolio " +"SET balance ="+ newbalance+" WHERE p_id="+ pid+"";
			stmt.executeUpdate(sql);
			stmt.close();
			con.close();
		}
		else{
			return "Deposit amount exceeds the cash limitation.";
		}
		return "Success";
	}
	public static String WithDrawCash(int pid, double amount) throws SQLException, ClassNotFoundException {
		if(AllowedToWithdraw(pid,amount)){
			Connection con=DatabaseConnection.getConnection();
			
			int update =0;
			PreparedStatement statement =  con.prepareStatement("SELECT MAX(T_ID) AS maxtid FROM portfolio_transactions");
		     ResultSet result = statement.executeQuery();
	 		while(result.next())  {
	 			update = result.getInt(1) + 1;
	 		}
	 		statement.close();
			Statement stmt=con.createStatement();
			
			double newbalance =getPortfolioBalance(pid) - amount;
			stmt.executeUpdate("INSERT INTO portfolio_transactions (T_ID,P_ID,ACTION,AMOUNT,TDATE) VALUES ('"+update+"','"+pid+"','W','"+amount+"',CURRENT_TIMESTAMP)");
			String sql = "UPDATE portfolio " +"SET balance ="+ newbalance+" WHERE p_id="+ pid+"";
		    stmt.executeUpdate(sql);
		    stmt.close();
		    con.close();
		}
		else{
			return "Withdrawed amount is grater than the balance.";
		}
		return "Success";	
	}
	
	public static boolean AllowedToDeposit(int pid,double amount) throws ClassNotFoundException, SQLException{
		double PortfolioBalance = getPortfolioBalance(pid) + amount;
		double TransactionBalance = getTransactionBalance(pid) + amount;
		if((PortfolioBalance/TransactionBalance)>0.1){
			double threshold = (TransactionBalance - amount) / 10;
			System.out.printf("\nAllowed amount to deposit is %f\n",threshold+amount-PortfolioBalance );
		}
		return ((PortfolioBalance/TransactionBalance)<0.1);
	}
	
	public static boolean AllowedToWithdraw(int pid,double amount) throws ClassNotFoundException, SQLException{
		
		double PortfolioBalance = getPortfolioBalance(pid) - amount;
		
		return (PortfolioBalance > 0);
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
