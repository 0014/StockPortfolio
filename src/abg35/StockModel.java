package abg35;

public class StockModel {
	private String _company;
	private String _type;
	private double _stockInitialPrice;
	private double _stockPrice;
	private int _shares;
	private double _stockValue;
	private float _expectedReturn;
	
	public StockModel(String company, String type, double initialPrice, int shares, double value, float expectedReturn) {
		_company = company;
		_type = type;
		_shares = shares;
		_stockInitialPrice = initialPrice;
		_stockValue = value;
		_expectedReturn = expectedReturn;
	}
	
	public String getType() {
		return _type;
	}
	
	public String getCompany() {
		return _company;
	}
	
	public void setStockPrice(double price) {
		_stockPrice = price;
	}
	
	public double getStockPrice() {
		return _stockPrice;
	}
	
	public double getInitialStockPrice() {
		return _stockInitialPrice;
	}
	
	public int getShares() {
		return _shares;
	}
	
	public double getStockValue() {
		return _stockValue;
	}
	
	public void setStockValue(double value) {
		_stockValue = value;
	}
	
	public float getExpectedReturn() {
		return _expectedReturn;
	}
	
	public void setExpectedReturn(float er) {
		_expectedReturn = er;
	}
}
