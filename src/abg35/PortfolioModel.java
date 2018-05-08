package abg35;

import java.util.List;

public class PortfolioModel {
	private int _id;
	private String _name;
	private double _portfolioValue;
	private double _balance;
	private double _expectedReturn;
	private List<StockModel> _stocks;
	
	public PortfolioModel(int id, String name, double portfolioValue, double balance, List<StockModel> stocks, double expectedReturn){
		_id = id;
		_name = name;
		_portfolioValue = portfolioValue;
		_balance = balance;
		_stocks = stocks;
		_expectedReturn = expectedReturn;
	}
	
	public int getId() {
		return _id;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setPortfolioValue(double value) {
		_portfolioValue = value;
	}
	
	public double getPortfolioValue() {
		return _portfolioValue;
	}
	
	public double getBalance() {
		return _balance;
	}
	
	public List<StockModel> getStocks() {
		return _stocks;
	}
	
	public double getExpectedReturn()
	{
		return _expectedReturn;
	}
}
