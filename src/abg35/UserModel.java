package abg35;

import java.util.List;

public class UserModel {
	private int _id;
	private String _name;
	private List<PortfolioModel> _portfolios;
	
	public UserModel(int id, String name, List<PortfolioModel> portfolios){
		_id = id;
		_name = name;
		_portfolios = portfolios;
	}
	
	public String getName() {
		return _name;
	}
	
	public List<PortfolioModel> getPortfolioList(){
		return _portfolios;
	}
	
	public int getId(){
		return _id;
	}
}
