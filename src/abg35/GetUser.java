package abg35;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetPortfolio
 */
@WebServlet("/GetUser")
public class GetUser extends HttpServlet {
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int userId = Integer.parseInt(request.getParameter("id"));
		// get the selected portfolio Id
		int portfolioId = 0;
		if(request.getParameter("index") == null)
			portfolioId = getFirstPortfolio(userId);
		else
			portfolioId = Integer.parseInt(request.getParameter("index"));
	    
	    request.setAttribute("user", getUser(userId, portfolioId));
	    request.setAttribute("selectedPortfolioIndex", portfolioId);
   	 	request.getRequestDispatcher("portfolio.jsp").forward(request, response);
	}
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private UserModel getUser(int userId, int portfolioId) {
		
		List<StockModel> stocks = getStocks(portfolioId);
		List<PortfolioModel> portfolios = getPortfolio(userId, stocks);
		String userName = getUserName(userId);
		
		return new UserModel(userId, userName, portfolios);
	}
	
	private int getFirstPortfolio(int userId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		int portfolioId = 0;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT P_ID as id FROM portfolio WHERE USER_ID = " + userId + " AND ISACTIVE = 1 ORDER BY P_ID LIMIT 1");
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 portfolioId = (Integer)map.get("id");
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portfolioId;
	}
	
	private float getExpectedReturn(String company) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		float er = 0;
		try {
			Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery("SELECT EXPECTED_RETURN FROM company_details WHERE COMPANY = '" + company +"'");
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 er = (float)map.get("EXPECTED_RETURN");
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return er;
	}
	
	private String getCompanyType(String company) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		String type = "";
		try {
			Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery("SELECT CTYPE FROM company_details WHERE COMPANY = '" + company +"'");
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 type = String.valueOf(map.get("CTYPE"));
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return type;
	}
	
	private String getUserName(int userId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		String userName = null;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT USERNAME FROM users WHERE USER_ID = " + userId);
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 userName = String.valueOf(map.get("USERNAME"));
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userName;
	}
	
	private List<StockModel> getStocks(int portfolioId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		List<StockModel> stocks = new ArrayList<StockModel>();
		List<String> stockSymbols = new ArrayList<String>();
		try {
			 Statement stmt=con.createStatement();
			 ResultSet rs=stmt.executeQuery("SELECT COMPANY, PRICE, SHARES FROM stock_details WHERE P_ID = " + portfolioId);
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 String company = String.valueOf(map.get("COMPANY"));
				 double price = (double)map.get("PRICE");
				 int shares = (Integer)map.get("SHARES");
				 String type = getCompanyType(company);
				 float er = getExpectedReturn(company);
				 double value = getStockValue(getStockTransactionId(company, portfolioId));
				 
				 stockSymbols.add(company);
				 StockModel stock = new StockModel(company, type, price, shares, value, er);
				 stock.setStockPrice(price); //comment me
				 stocks.add(stock);
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Double> currentPriceList = GetStockValue.returnPrices(stockSymbols);
		for(int i = 0; i < currentPriceList.size(); i++) {
			continue;
			//if(currentPriceList.get(i) < 0.001) continue;
			//stocks.get(i).setStockPrice(Math.round(currentPriceList.get(i) * 100.0) / 100.0);
		}
		
		return stocks;
	}
	
	private int getStockTransactionId(String company, int pId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		int sId = 0;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT S_ID FROM stock_details WHERE P_ID = " + pId + " AND COMPANY ='" + company + "'" );
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 sId = (Integer)map.get("S_ID");
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sId;
	}
	
	private double getStockValue(int sId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		double value = 0;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT ACTION, PRICE, SHARES FROM stock_transactions WHERE S_ID = " + sId );
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 String action = String.valueOf(map.get("ACTION"));
				 double price = (double)map.get("PRICE");
				 int shares = (Integer)map.get("SHARES");
				 if(action.equals("BUY"))
					 value += shares * price;
				 else
					 value -= shares * price;
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;		
	}
	
	private List<Double> getCurrentStockPrices(List<String> symbols){
		List<Double> priceList = new ArrayList<Double>();
		
		for(int i = 0; i < symbols.size(); i++) {
			priceList.add(123.45);
		}
		
		return priceList;
	}
	
	private List<PortfolioModel> getPortfolio(int userId, List<StockModel> stocks) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		List<PortfolioModel> portfolios = new ArrayList<PortfolioModel>();
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT P_ID as id, PNAME as name, BALANCE FROM portfolio WHERE ISACTIVE = 1 AND USER_ID =" + userId );
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 int portfolioId = (Integer)map.get("id");
				 String portfolioName = String.valueOf(map.get("name"));
				 double portfolioBalance = (double)map.get("BALANCE");
				 double portfolioValue = getPortfolioValue(portfolioId);
				 double er = getPortfolioExpectedReturn(portfolioValue, stocks);
				 portfolios.add(new PortfolioModel(portfolioId, portfolioName, portfolioValue, portfolioBalance, stocks, er));
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return portfolios;
	}
	
	public double getPortfolioExpectedReturn(double pValue, List<StockModel> stocks) {
		double er = pValue;
		for(StockModel stock : stocks) {
			er += stock.getStockPrice() * stock.getShares() * stock.getExpectedReturn() /100;
		}
		return er;
	}
	
	public double getPortfolioValue(int portfolioId) {
		Connection con=DatabaseConnection.getConnection();
		List<Map<Object, Object>> result=null;
		double portfolioValue = 0;
		try {
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT AMOUNT,ACTION FROM portfolio_transactions "
					+ "WHERE P_ID = " + portfolioId);
			 result= DatabaseConnection.resultSetToArrayList(rs);
			 for(Map<Object,Object> map:result)
			 {
				 String action = String.valueOf(map.get("ACTION"));
				 double amount = (double)map.get("AMOUNT");
				 portfolioValue = action.equals("ADD") ? portfolioValue + amount : portfolioValue - amount;
			 }
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return portfolioValue;
	}
}
