<%@ page import ="java.util.ArrayList"%>
<%@ page import ="java.util.List"%>
<%@ page import ="abg35.*"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Class project which will simulate buying selling analyzing stocks">
        <meta name="author" content="Group M3">

        <title>CS673 - Stock Analyzer</title>
        
        <link href="theme-sb-admin-2/vendor/bootstrap/css/bootstrap.css" rel="stylesheet" />
        <link href="theme-sb-admin-2/vendor/bootstrap/css/bootstrap-slider.min.css" rel="stylesheet" />
        <link href="theme-sb-admin-2/vendor/metisMenu/metisMenu.css" rel="stylesheet" />
        <link href="theme-sb-admin-2/dist/css/sb-admin-2.css" rel="stylesheet" />
        <link href="theme-sb-admin-2/vendor/morrisjs/morris.css" rel="stylesheet">
        <link href="theme-sb-admin-2/vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="theme-sb-admin-2/vendor/bootstrap-social/bootstrap-social.css" rel="stylesheet">
        <link href="theme-sb-admin-2/vendor/datatables-plugins/dataTables.bootstrap.css" rel="stylesheet">
        <link href="theme-sb-admin-2/vendor/datatables-responsive/dataTables.responsive.css" rel="stylesheet">
    </head>
    <% 
        UserModel user = (UserModel)request.getAttribute("user");
        PortfolioModel portfolio = null;
        if(user.getPortfolioList().size() != 0){
            portfolio = user.getPortfolioList().get(0);
            for(PortfolioModel p : user.getPortfolioList()){
                if(p.getId() == (int)request.getAttribute("selectedPortfolioIndex"))
                    portfolio = p;
            }
        }
    %>
<body>
    <div id="wrapper">
        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <label class="hidden" id="userId"><%out.print(user.getId());%></label>
                <a class="navbar-brand" href=""><%out.print(user.getName());%></a>
            </div>
            <ul class="nav navbar-top-links navbar-right">
                <%if(portfolio != null){%>
                <li class="dropdown">
                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">
                        <i class="fa fa-user fa-fw"></i> <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-user">
                        <li>
                            <a id="lnkModifyPortfolio" href="" data-toggle="modal" data-target="#modifyPortfolio"><i class="fa fa-edit fa-fw"></i>Edit Portfolio</a>
                        </li>
                        <li>
                            <a id="lnkDeletePortfolio" href=""><i class="fa fa-times fa-fw"></i>Delete Portfolio</a>
                        </li>
                        <li>
                            <a id="buySellStocks" href="" data-toggle="modal" data-target="#buySellStockModal"><i class="fa fa-shopping-cart fa-fw"></i>Buy/Sell Stocks</a>
                        </li>
                        <li>
                            <a id="optimizePortfolio" href="" data-toggle="modal" data-target="#optimizeModal"><i class="fa fa-bullseye fa-fw"></i>Optimize Portfolio</a>
                        </li>
                    </ul>
                </li>
                <%}%>
                <li>
                    <a id="newPortfolioButton" href="" data-toggle="modal" data-target="#myModal"><i class="fa fa-plus fa-fw"></i>New Portfolio</a>
                </li>
                <li>
                    <a href="login.jsp"><i class="fa fa-sign-out fa-fw"></i>Logout</a>
                </li>
            </ul>
            <div class="navbar-default sidebar" role="navigation">
                <div class="sidebar-nav navbar-collapse">
                    <ul class="nav" id="side-menu">
                        <li>
                            <a href=""><i class="fa fa-users fa-fw"></i>Portfolios<span class="fa arrow"></span></a>
                            <ul class="nav nav-second-level">
                                <% List<PortfolioModel> portfolios = user.getPortfolioList();
                                for(int i = 0; i < portfolios.size(); i++){%>
                                <li>
                                    <a href="GetUser?id=<%out.print(user.getId() +"&index=" + portfolios.get(i).getId());%>"><%out.print(portfolios.get(i).getName());%></a>
                                </li>
                                <%}%>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div id="page-wrapper">
            <%if(portfolio != null){%>
            <div class="row page-header">
                <div class="col-lg-12">
                    <h1><%out.print(portfolio.getName());%></h1>
                    <label class="hidden" id="portfolioId"><%out.print(portfolio.getId());%></label>
                    <label class="hidden" id="portfolioValue"><%out.print(portfolio.getPortfolioValue());%></label>
                    <h3>Available Balance:$<%out.print(String.format("%.02f", portfolio.getBalance()));%></h3>
                </div>
                <div class="col-md-4">
                    <button type="button" class="btn btn-link transaction-btn" id="cashDeposit">Deposit</button>/
                    <button type="button" class="btn btn-link transaction-btn" id="cashWithdraw">Withdraw</button>
                </div>
                <div class="col-md-6">
                    <input type="number" class="form-control number-only" id="transactionAmount" placeholder="Amount">
                </div>
                <div class="col-sm-8">
                    <div class="text-danger" id="transactionError"></div>
                </div>
            </div>

            <div class="row">
            </div>
            <div class="panel-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Company</th>
                            <th>Type</th>
                            <th>Number of Shares</th>
                            <th>Invested Value($)</th>
                            <th>Current Value($)</th>
                            <th>Expected Return %</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%List<StockModel> stocks = portfolio.getStocks();
                        for(int i = 0; i < stocks.size(); i++){%>
                        <tr>
                            <td><p><%out.print(i + 1); %></p></td>
                            <td><p id="portfolioSymbol<%out.print(i + 1); %>"><%out.print(stocks.get(i).getCompany()); %></p></td>
                            <td><p id="portfolioSymbolType<%out.print(i + 1); %>"><%out.print(stocks.get(i).getType()); %></p></td>
                            <td><p id="portfolioNumberOfShares<%out.print(i + 1); %>"><%out.print(stocks.get(i).getShares()); %></p></td>
                            <td><p id="portfolioValue<%out.print(i + 1); %>"><%out.print(String.format("%.02f", stocks.get(i).getStockValue())); %></p></td>
                            <td><p id="portfolioStockPrice<%out.print(i + 1); %>"><%out.print(String.format("%.02f", stocks.get(i).getStockPrice() * stocks.get(i).getShares())); %></p></td>
                            <td><p id="portfolioExpectedReturn<%out.print(i + 1); %>"><%out.print(String.format("%.02f", stocks.get(i).getExpectedReturn())); %></p></td>
                        </tr>
                        <%}%>
                        </tbody>
                    </table>
                </div>
            </div>
            <div>
                <h3>Total Invested Value: $<%out.print(String.format("%.02f", portfolio.getPortfolioValue()));%></h3>
            </div>
            <div>
                <h3>Expected Return Value: $<%out.print(String.format("%.02f", portfolio.getExpectedReturn()));%></h3>
            </div>
            <div class="modal fade" id="buySellStockModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h4 class="modal-title" id="myModalLabel">Buy/Sell Stocks and Shares</h4>
                        </div>
                        <form>
                            <div class="modal-body">
                                <div class="form-group">
                                    <label class="text-danger" id="buySellStocksError"></label>
                                </div>
                                <h2>
                                    Portfolio Value: $<%out.print(portfolio.getPortfolioValue());%>
                                </h2>
                                <div class="text-info">
                                    - User can list minimum 7, maximum 10 stocks.
                                </div>
                                <div class="text-info">
                                    - Total money spent on DOW-30 type stocks must be between 68%-72%.
                                </div>
                                <div class="text-info">
                                    - Total money spent on NIFTY-FIFTY type stocks must be between 28%-32%.
                                </div>
                                <div class="text-info">
                                    - Remaining balance cannot exceed 10% of portfolio value (money spent on portfolio).
                                </div><br>
                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="table-responsive">
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th>Company</th>
                                                    <th>Stock Type</th>
                                                    <th>Stock Unit Price($)</th>
                                                    <th>Sell/Buy</th>
                                                    <th>Amount Spent($)</th>
                                                </tr>
                                                </thead>
                                                <tbody id="bsTable">
                                                <%for(int i = 0; i < stocks.size(); i++){%>
                                                <tr>
                                                    <td>
                                                        <label id="bsSymbol<%out.print(i);%>"><%out.print(stocks.get(i).getCompany());%></label>
                                                    </td>
                                                    <td>
                                                        <label id="bsType<%out.print(i);%>"><%out.print(stocks.get(i).getType());%></label>
                                                    </td>
                                                    <td>
                                                        <label id="bsUnitPrice<%out.print(i);%>"><%out.print(String.format("%.02f", stocks.get(i).getStockPrice()));%></label>
                                                    </td>
                                                    <td>
                                                        <input id="bsShares<%out.print(i);%>" data-slider-id='ex1Slider' type="text" data-slider-min="<%out.print(stocks.get(i).getShares() * -1);%>" data-slider-max="<%out.print(new Double(portfolio.getBalance() / stocks.get(i).getStockPrice()).intValue());%>" data-slider-step="1" data-slider-value="0"/>
                                                        <span>&nbsp;</span>
                                                        <span id="bsValShares<%out.print(i);%>">0</span>
                                                    </td>
                                                    <td>
                                                        <label id="bsSpent<%out.print(i);%>">0</label>
                                                        <label id="bsValue<%out.print(i);%>" class="hidden"><%out.print(stocks.get(i).getStockValue());%></label>
                                                    </td>
                                                </tr>
                                                <%}%>
                                                <%for(int i = stocks.size(); i < 10; i++){%>
                                                <tr>
                                                    <td>
                                                        <input type="text" class="bsStockSymbol" id="bsSymbol<%out.print(i);%>">
                                                    </td>
                                                    <td>
                                                        <label class="stockType" id="bsType<%out.print(i);%>"></label>
                                                    </td>
                                                    <td>
                                                        <label class="stockUnitPrice" id="bsStockUnitPrice<%out.print(i);%>"></label>
                                                    </td>
                                                    <td>
                                                        <input type="number" min="0" class="bsNewShares integer-only shares" id="bsValShares<%out.print(i);%>">
                                                        <label class="hidden" id="prevBsNewShares<%out.print(i);%>">0</label>
                                                    </td>
                                                    <td>
                                                        <label id="bsSpent<%out.print(i);%>">0</label>
                                                        <label class="prevTotalPrice hidden" id="bsPrevTotalPrice<%out.print(i);%>">0</label>
                                                        <label id="bsValue<%out.print(i);%>" class="hidden">0</label>
                                                    </td>
                                                </tr>
                                                <%}%>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                                
                                <div id="balanceInfo" class="form-group alert alert-success">
                                    <div class="col-lg-5">
                                        <label>Remaining Balance: </label>
                                    </div>
                                    <h4 >$<label id="bsBalance"><%out.print(String.format("%.02f", portfolio.getBalance()));%></label></h4>
                                </div>
                                <div id="percentageInfo" class="form-group alert alert-success">
                                    <h4>Percentages (2% error tolerance):</h4>
                                    <div class="col-lg-5">
                                        <label>DOW-30: </label>
                                    </div>
                                    <h4><label id="bsPercentage1">68</label>%</h4>
                                    <div class="col-lg-5">
                                        <label>NIFTY-FIFTY: </label>
                                    </div>
                                    <h4><label id="bsPercentage2">32</label>%</h4>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default pull-left" id="bsRefresh"><i class="fa fa-refresh fa-fw"></i></button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary" onclick="return submitBuySellStocks(this, event);">Save changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="optimizeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h4 class="modal-title">Optimization Results</h4>
                        </div>
                        <form>
                            <div class="modal-body">
                                <div class="form-group">
                                    <label class="text-danger" id="optimizationError"></label>
                                </div>
                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="table-responsive">
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th>Company</th>
                                                    <th>Type</th>
                                                    <th>Optimization</th>
                                                    <th>Stock Value $</th>
                                                    <th>Expected Return %</th>
                                                    <th>Risk Factor</th>
                                                </tr>
                                                </thead>
                                                <tbody id="optimizationTable">
                                                <%for(int i = 0; i < stocks.size(); i++){%>
                                                <tr>
                                                    <td>
                                                        <label id="optSymbol<%out.print(i);%>"><%out.print(stocks.get(i).getCompany());%></label>
                                                    </td>
                                                    <td>
                                                        <label id="optType<%out.print(i);%>"><%out.print(stocks.get(i).getType());%></label>
                                                    </td>
                                                    <td>
                                                        <label id="optShare<%out.print(i);%>"><%out.print(stocks.get(i).getShares());%></label>
                                                        <label id="optShareAdjust<%out.print(i);%>"><%out.print("");%></label>
                                                        <label class="hidden" id="optNewShare<%out.print(i);%>"><%out.print("");%></label>
                                                    </td>
                                                    <td>
                                                        <label id="optStockValue<%out.print(i);%>"><%out.print(String.format("%.02f",stocks.get(i).getStockPrice()));%></label>
                                                    </td>
                                                    <td>
                                                        <label id="optExpectedReturn<%out.print(i);%>"><%out.print(String.format("%.02f", stocks.get(i).getExpectedReturn()));%></label>
                                                    </td>
                                                    <td>
                                                        <%if(stocks.get(i).getBeta() >= 1.2){
                                                        	out.print("<label class=\"alert alert-danger\" id=\"optRisk" + i + "\">Risky</label>");	
                                                        }else if(stocks.get(i).getBeta() >= 0.9){
                                                        	out.print("<label class=\"alert alert-info\" id=\"optRisk" + i + "\">Natural</label>");	
                                                        }else{
                                                        	out.print("<label class=\"alert alert-success\" id=\"optRisk" + i + "\">Non-Risk</label>");	
                                                        }%>
                                                        <label class="hidden" id="optBeta<%out.print(i);%>"><%out.print(stocks.get(i).getBeta());%></label>
                                                    </td>
                                                </tr>
                                                <%}%>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-lg-5">
                                    <label>Invested Value: </label>
                                </div>
                                <h4 >$<label id="optInvestedValue"><%out.print(String.format("%.02f", portfolio.getPortfolioValue()));%></label></h4>
                                <label class="hidden" id="optRemainingBalance"><%out.print(portfolio.getBalance());%></label>
                            </div>
                            <div class="form-group">
                                <div class="col-lg-5">
                                    <label>Expected Value: </label>
                                </div>
                                <h4 >$<label><%out.print(String.format("%.02f",portfolio.getExpectedReturn()));%></label></h4>
                            </div><br>
                            <div class="form-group alert alert-success">
                                <div class="col-lg-5">
                                    <label>Optimized Expected Value: </label>
                                </div>
                                <h4 ><label id="optExpectedValue"></label></h4>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default pull-left" id="optimize">OPTIMIZE</button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary" onclick="return applyOptimization(this, event);">Apply Optimization</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="modal fade" id="modifyPortfolio" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h4 class="modal-title" id="myModalLabel">Modify Portfolio</h4>
                        </div>
                        <div class="modal-body">
                            <input type="text" id="updatedPortfolioName" placeholder="New portfolio name..." maxlength="20">
                            <p class="debug-url"></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default pull-left" data-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-danger" data-dismiss="modal" onclick="return modifyPortfolio();">Update</button>
                        </div>
                    </div>
                </div>
            </div> 
            <%}%>
            <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <div class="col-lg-8">
                            	<h4 class="modal-title pull-left">New Portfolio</h4>
                            </div>
                            <div class="col-lg-4">
                            	<input class="form-control pull-right" id="file" type="file">
                        	</div>
                        </div>
                        <form>
                            <div class="modal-body">
                                <div class="form-group">
                                    <label class="text-danger" id="newPortfolioError"></label>
                                </div>
                                <div class="form-group">
                                    <label>Portfolio Name</label>
                                    <input type="text" class="form-control" id="newPortfolioName" maxlength="20">
                                </div>
                                <div class="text-info">
                                    - User can list minimum 7, maximum 10 stocks.
                                </div>
                                <div class="text-info">
                                    - Total money spent on DOW-30 type stocks must be between 68%-72%.
                                </div>
                                <div class="text-info">
                                    - Total money spent on NIFTY-FIFTY type stocks must be between 28%-32%.
                                </div>
                                <div class="text-info">
                                    - Remaining balance cannot exceed 10% of portfolio value (money spent on portfolio).
                                </div><br>
                                <div class="row">
                                    <div class="col-lg-12">
                                        <div class="table-responsive">
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th>Company</th>
                                                    <th>Stock Type</th>
                                                    <th>Stock Unit Price($)</th>
                                                    <th>Number of Shares</th>
                                                    <th>Total Price($)</th>
                                                </tr>
                                                </thead>
                                                <tbody id="newPortfolioTable">
                                                <%for(int i = 0; i < 10; i++){%>
                                                <tr>
                                                    <td>
                                                        <input type="text" class="stockSymbol" id="newStock<%out.print(i);%>">
                                                    </td>
                                                    <td>
                                                        <label class="stockType" id="stockType<%out.print(i);%>"></label>
                                                    </td>
                                                    <td>
                                                        <label class="stockUnitPrice" id="stockUnitPrice<%out.print(i);%>"></label>
                                                    </td>
                                                    <td>
                                                        <input type="number" class="newShares integer-only shares" id="newShares<%out.print(i);%>">
                                                    </td>
                                                    <td>
                                                        <label class="totalPrice" id="totalPrice<%out.print(i);%>"></label>
                                                    </td>
                                                </tr>
                                                <%}%>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label>Deposit</label>
                                    <input type="number" class="form-control number-only" id="depositCash">
                                </div>
                            </div>
                            <div id="newPortfolioBalanceInfo" class="form-group alert alert-danger">
                                <div class="col-lg-5">
                                    <label>Remaining Balance: </label>
                                </div>
                                <h4 >$<label id="npBalance">0.00</label></h4>
                            </div>
                            <div id="newPortfolioPercentageInfo" class="form-group alert alert-danger">
                                <h4>Percentages (2% error tolerance):</h4>
                                <div class="col-lg-5">
                                    <label>DOW-30: </label>
                                </div>
                                <h4><label id="npPercentage1">50</label>%</h4>
                                <div class="col-lg-5">
                                    <label>NIFTY-FIFTY: </label>
                                </div>
                                <h4><label id="npPercentage2">50</label>%</h4>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default pull-left" data-toggle="modal" data-target="#confirmRefresh"><i class="fa fa-refresh fa-fw"></i></button>
                                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                                <button type="button" class="btn btn-primary" onclick="return submitNewPortfolio(this, event);">Save changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>   
            <div class="modal fade" id="confirmRefresh" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-sm">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                            <h4 class="modal-title" id="myModalLabel">Confirm Refresh</h4>
                        </div>
                        <div class="modal-body">
                            <p>All entered data will be gone.</p>
                            <p>Do you want to proceed?</p>
                            <p class="debug-url"></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                            <button type="button" class="btn btn-danger" data-dismiss="modal" onclick="return refreshModal();">Refresh</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="theme-sb-admin-2/vendor/jquery/jquery.js"></script>
    <script src="theme-sb-admin-2/vendor/bootstrap/js/bootstrap.js"></script>
    <script src="theme-sb-admin-2/vendor/bootstrap/js/bootstrap-slider.min.js"></script>
    <script src="theme-sb-admin-2/vendor/metisMenu/metisMenu.js"></script>
    <script src="theme-sb-admin-2/vendor/flot/excanvas.js"></script>
    <script src="theme-sb-admin-2/vendor/flot/jquery.flot.js"></script>
    <script src="theme-sb-admin-2/vendor/flot/jquery.flot.pie.js"></script>
    <script src="theme-sb-admin-2/vendor/flot/jquery.flot.resize.js"></script>
    <script src="theme-sb-admin-2/vendor/flot/jquery.flot.time.js"></script>
    <script src="theme-sb-admin-2/vendor/flot-tooltip/jquery.flot.tooltip.js"></script>
    <script src="theme-sb-admin-2/data/flot-data.js"></script>
    <script src="theme-sb-admin-2/vendor/raphael/raphael.js"></script>
    <script src="theme-sb-admin-2/vendor/morrisjs/morris.js"></script>
    <script src="theme-sb-admin-2/data/morris-data.js"></script>
    <script src="theme-sb-admin-2/dist/js/sb-admin-2.js"></script>
    <script src="theme-sb-admin-2/vendor/datatables/js/jquery.dataTables.min.js"></script>
    <script src="theme-sb-admin-2/vendor/datatables-plugins/dataTables.bootstrap.js"></script>
    <script src="theme-sb-admin-2/vendor/datatables-responsive/dataTables.responsive.js"></script> 
    
    <script type="text/javascript">
    $(document).ready(function() {
        $("#transaction").hide();
        $("#removeTransactionBtn").hide();
        
        $('#addTransactionBtn').click(function (){
            $("#transaction").show();
            $("#addTransactionBtn").hide();
            $("#removeTransactionBtn").show();
        });
        
        $('#removeTransactionBtn').click(function (){
            $("#transaction").hide();
            $("#removeTransactionBtn").hide();
            $("#addTransactionBtn").show();
            $('#symbolBuy').val('');
            $('#unitPriceBuy').text('');
            $('#sharesBuy').val('');
            $('#totalBuy').text('');
        });
    });

    $('#bsRefresh').click(function (){
        location.href='GetUser?id=' + $("#userId").text() + '&index=' + $("#portfolioId").text();
    });

    $(function(){
        $('.integer-only').keypress(function(e) {
            if(isNaN(this.value+""+String.fromCharCode(e.charCode)) || e.charCode == 46) return false;
        }).on("cut copy paste",function(e){
            e.preventDefault();
        });
        $('.number-only').keypress(function(e) {
            if(isNaN(this.value+""+String.fromCharCode(e.charCode))) return false;
        }).on("cut copy paste",function(e){
            e.preventDefault();
        });
    });

    $(function(){
        $(".stockSymbol").blur(function(){
            var symbol = $(this).val();
            if(symbol == '') return;
            var jason = JSON.stringify({symbol: symbol});
            var company = $(this);
            var newShares = $(this).closest('tr').find("input[class^='newShares']");
            var stockType = $(this).closest('tr').find("label[class^='stockType']");
            var unitPrice = $(this).closest('tr').find("label[class^='stockUnitPrice']");
            var totalPrice = $(this).closest('tr').find("label[class^='totalPrice']");
            $.ajax({
                contentType: 'application/json',
                data: jason,
                dataType: 'json',
                success: function(data){
                    if(data.unitPrice == null){
                        company.val('');
                    } else{
                        //newShares.val('1');
                        stockType.text(data.stockType);
                        unitPrice.text(parseFloat(data.unitPrice).toFixed(2));
                        totalPrice.text(parseFloat(data.unitPrice * newShares.val()).toFixed(2));
                        calculateNewPortfolioPercentages();
                        calculateNewPortfolioBalance();
                    }
                },
                error: function(){
                    alert('something went wrong...');
                },
                processData: false,
                type: 'POST',
                url: 'GetNewPrice'
            });
        });
        
        $(".bsStockSymbol").blur(function(){
            var symbol = $(this).val();
            if(symbol == '') return;
            var jason = JSON.stringify({symbol: symbol});
            var company = $(this);
            var newShares = $(this).closest('tr').find("input[class^='bsNewShares']");
            var stockType = $(this).closest('tr').find("label[class^='stockType']");
            var unitPrice = $(this).closest('tr').find("label[class^='stockUnitPrice']");
            var totalPrice = $(this).closest('tr').find("label[class^='totalPrice']");
            $.ajax({
                contentType: 'application/json',
                data: jason,
                dataType: 'json',
                success: function(data){
                    if(data.unitPrice == null){
                        company.val('');
                    } else{
                        newShares.val('0');
                        stockType.text(data.stockType);
                        unitPrice.text(data.unitPrice);
                        totalPrice.text(0); 
                    }
                },
                error: function(){
                    alert('something went wrong...');
                },
                processData: false,
                type: 'POST',
                url: 'GetNewPrice'
            });
        });
        $("#depositCash").blur(function(e){
            calculateNewPortfolioBalance();
        });
        $(".shares").blur(function(e){
            var symbol = $(this).closest('tr').find("input[id^='newStock']").val();
            if(symbol == null || symbol == '') return;
            var unitPrice = $(this).closest('tr').find("label[id^='stockUnitPrice']").text();
            $(this).closest('tr').find("label[id^='totalPrice']").text((unitPrice * $(this).val()).toFixed(2));
            calculateNewPortfolioPercentages();
            calculateNewPortfolioBalance();
        });
        $(".bsNewShares").blur(function(e){
            var symbol = $(this).closest('tr').find("input[id^='bsSymbol']").val();
            if(symbol == null || symbol == '') return;
            var unitPrice = $(this).closest('tr').find("label[id^='bsStockUnitPrice']").text();
            var balance = $("#bsBalance").text();
            var prevShares = $(this).closest('tr').find("label[id^='prevBsNewShares']").text();
            var spentValue = (unitPrice * (prevShares - $(this).val())).toFixed(2);
            var newBalance = parseFloat(spentValue)+ parseFloat(balance);
            if(newBalance >= 0){
                $(this).closest('tr').find("label[id^='bsSpent']").text($(this).val() * unitPrice);
                $(this).closest('tr').find("label[id^='prevBsNewShares']").text($(this).val());
                buySellStockScreenValues(newBalance, null);
            }else{
                $(this).val(prevShares);
            }
        });
        $("#symbolBuy").blur(function(e){
            var symbol = $(this).val();
            $('#unitPriceBuy').text(symbol == null || symbol == '' ? '':'1234.56');
            $('#sharesBuy').val(symbol == null || symbol == '' ? '':'1');
            $('#totalBuy').text(symbol == null || symbol == '' ? '':'1234.56');
        });
        $("#sharesBuy").blur(function(e){
            var symbol = $('#symbolBuy').val();
            if(symbol == null || symbol == ''){
                $(this).val('');
            }else{
                $('#totalBuy').text($(this).val() * $('#unitPriceBuy').text());
            }
        });
    });

    //WITHDRAW DEPOSIT SECTION
    $('.transaction-btn').click(function (){
        var action = "";
        
        if($(this).attr('id') === "cashWithdraw"){
            action = "W";
        }else{
            action = "D";
        }
        var data = {
                pid:$("#portfolioId").text(),
            amount:$("#transactionAmount").val(),
            action:action
        };
        var jason = JSON.stringify(data);
        $.ajax({
            contentType: 'application/json',
            data: jason,
            dataType: 'json',
            success: function(data){
                if(data.error != null){
                    $("#transactionError").text(data.error);
                }else{
                    location.href='GetUser?id=' + $("#userId").text() + '&index=' + $("#portfolioId").text();
                }
            },
            error: function(){
                alert('something went wrong...');
            },
            processData: false,
            type: 'POST',
            url: 'CashOperation'
        });
    });
    
    //DELETE PORTFOLIO SECTION
    $('#lnkDeletePortfolio').click(function (){
        var data = {
                userId:$("#userId").text(),
                pId:$("#portfolioId").text()
        };
        var jason = JSON.stringify(data);
        $.ajax({
            contentType: 'application/json',
            data: jason,
            dataType: 'json',
            success: function(data){
                location.href='GetUser?id=' + $("#userId").text();
            },
            error: function(){
                alert('something went wrong...');
            },
            processData: false,
            type: 'POST',
            url: 'DeletePortfolio'
        });
    });
    
    //OPTIMIZE PORTFOLIO
    $('#optimize').click(function (){
    	$("#optExpectedValue").text("OPTIMIZING...");
        var data = {
            investment: $("#optInvestedValue").text(),
            stocks:[]
        };
        var array = [];
        $("#optimizationTable").find('tr').each(function (i) {
            var symbol = $(this).closest('tr').find("label[id^='optSymbol']").text();
            var type = $(this).closest('tr').find("label[id^='optType']").text();
            var currentValue = $(this).closest('tr').find("[id^='optStockValue']").text();  
            var expectedReturn = $(this).closest('tr').find("[id^='optExpectedReturn']").text();  
            var beta = $(this).closest('tr').find("[id^='optBeta']").text();
            
            array.push({symbol:symbol,type:type,currentValue:currentValue,expectedReturn:expectedReturn,beta:beta});
        });
            
        data.stocks = array;
        var jason = JSON.stringify(data);
        $.ajax({
            contentType: 'application/json',
            data: jason,
            dataType: 'json',
            success: function(data){
                updateOptShares(data.resp);
            },
            error: function(){
                $("#optimizationError").text('Something went wrong.');
            },
            processData: false,
            type: 'POST',
            url: 'OptimizePortfolio'
        });
        return false;
    });
    
    function updateOptShares(optShares){
    	var shares = optShares.split(",");
    	var portfolioValue = $("#optInvestedValue").text();
    	var expectedReturn = 0;
    	var totalInvested = 0;
    	var remainingBalance = 0;
    	for (i = 0; i < shares.length; i++) {
    		var er = $("#optExpectedReturn" + i).text();
    		var price = $("#optStockValue" + i).text();
    		var newShare = parseInt(shares[i]);
    		var currentShare = parseInt($("#optShare" + i).text());
    		var adjustment = newShare - currentShare;
    		$("#optNewShare" + i).text(newShare);
    		if(adjustment > 0){
    			$("#optShareAdjust" + i).text("+" + adjustment);
    			$("#optShareAdjust" + i).addClass("alert-success");
    		}else if(adjustment < 0){
    			$("#optShareAdjust" + i).text(adjustment);
    			$("#optShareAdjust" + i).addClass("alert-danger");
    		}
    		totalInvested += price * newShare;
    		expectedReturn += price * newShare + price * newShare * er / 100;
    	}
    	$("#optExpectedValue").text("$" + expectedReturn.toFixed(2));
    	$("#optRemainingBalance").text(portfolioValue - totalInvested);
    }
    
    function applyOptimization(thisObj, thisEvent) {
    	var data = {
              remainingBalance: $("#optRemainingBalance").text(),
              portfolioId: $("#portfolioId").text(),
              dateTime:(new Date()).toISOString().substring(0,10),
                  companies:[]
          };
          var stocks = [];
          $("#optimizationTable").find('tr').each(function (i) {
        	  var numberOfShares = $(this).closest('tr').find("label[id^='optShareAdjust']").text();
              if(numberOfShares != ""){
                  var price = $(this).closest('tr').find("label[id^='optStockValue']").text();
                  var totalValue = numberOfShares * price;
                  var symbol = $(this).closest('tr').find("label[id^='optSymbol']").text();
                  stocks.push({symbol:symbol,numberOfShares:numberOfShares,totalValue:totalValue});  
              }
          });
              
          data.companies = stocks;
          var jason = JSON.stringify(data);
          $.ajax({
              contentType: 'application/json',
              data: jason,
              dataType: 'json',
              success: function(data){
                  if(data.error == null){
                      location.href='GetUser?id=' + $("#userId").text() + '&index=' + $("#portfolioId").text();
                  }else{
                      $("#buySellStocksError").text(data.error);
                  }
              },
              error: function(){
                  $("#buySellStocksError").text('Something went wrong.');
              },
              processData: false,
              type: 'POST',
              url: 'BuySellStocks'
          });
          return false;
    }
    
    // MODIFY PORTFOLIO SECTION
    function modifyPortfolio(thisObj, thisEvent) {
        var data = {
                userId:$("#userId").text(),
                pId:$("#portfolioId").text(),
                pName:$("#updatedPortfolioName").val()
        };
        var jason = JSON.stringify(data);
            $.ajax({
                contentType: 'application/json',
                data: jason,
                dataType: 'json',
                success: function(data){
                    location.href='GetUser?id=' + $("#userId").text() + '&index=' + $("#portfolioId").text();
                },
                error: function(){
                    alert('something went wrong...');
                },
                processData: false,
                type: 'POST',
                url: 'ModifyPortfolio'
            });
    }

    //BUY SELL STOCKS SECTION
    $('#buySellStocks').click(function (){
        calculatePercentages();
    });

    $('a[id^="sell"]').click(function(){
        var unitPrice = $(this).closest('tr').find("p[id^='portfolioStockPrice']").text();
        $('#stockToSell').text($(this).closest('tr').find("p[id^='portfolioSymbol']").text());
        $('#availableStocksToSell').text($(this).closest('tr').find("p[id^='portfolioNumberOfShares']").text());
        $('#unitPriceToSell').text(unitPrice);
        $('#sellStockValue').text(unitPrice);
        $("#sellStocksError").text('');
        $('#numberOfStocksToSell').val(1);
    });

    $("input[id^='bsShares']").slider({
        formatter: function(value) {
            return '' + value;
        }
    });
    
    $("input[id^='bsShares']").on("slideStop", function(slideEvt) {
        var unitPrice = $(this).closest('tr').find("label[id^='bsUnitPrice']").text();
        var balance = $("#bsBalance").text() - ((slideEvt.value - $(this).next().next().text()) * unitPrice);
        $(this).next().next().text(slideEvt.value);
        $(this).closest('tr').find("label[id^='bsSpent']").text((slideEvt.value * unitPrice).toFixed(2));
        buySellStockScreenValues(balance, $(this).attr("id"));
    });

    function buySellStockScreenValues(newBalance, currentSlider) {
        var portfolioValue = $("#portfolioValue").text();
        $("#bsBalance").text(newBalance.toFixed(2));
        if(newBalance > portfolioValue * 10 / 100 || newBalance < 0){
            $("#balanceInfo").removeClass("alert-success");
            $("#balanceInfo").addClass("alert-danger");
        }else{
            $("#balanceInfo").removeClass("alert-danger");
            $("#balanceInfo").addClass("alert-success");
        }
        $("#bsTable").find('tr').each(function (i) {
            $(this).find("input[id^='bsShares']").each(function (i) {
                if($(this).attr("id") != currentSlider) {
                    var unitPrice = $(this).closest('tr').find("label[id^='bsUnitPrice']").text();
                    var value = $(this).closest('tr').find("span[id^='bsValShares']").text();
                    var maxVal = Math.floor(newBalance / unitPrice) + parseInt(value);
                    $(this).slider({ max: maxVal, value: value });
                    $(this).slider('refresh');  
                }
            });
        });
        calculatePercentages();
    }
    
    function calculatePercentages() {
        var dow30Value = 0;
        var niftyValue = 0;
        $("#bsTable").find('tr').each(function (i) {
            var type = $(this).closest('tr').find("label[id^='bsType']").text();
            var spent = $(this).closest('tr').find("label[id^='bsSpent']").text();
            var value = $(this).closest('tr').find("label[id^='bsValue']").text();
            if(type == "DOW30"){
                dow30Value = dow30Value + parseFloat(value) + parseFloat(spent);
            }else if(type == "NIFTYFIFTY"){
                niftyValue = niftyValue + parseFloat(value) + parseFloat(spent);
            }
        });
        var dow30Percentage = dow30Value / (dow30Value + niftyValue) * 100;
        $("#bsPercentage1").text(dow30Percentage.toFixed(1));
        $("#bsPercentage2").text((100 - dow30Percentage).toFixed(1));
        if(dow30Percentage.toFixed(1) < 68 || dow30Percentage.toFixed(1) > 72){
            $("#percentageInfo").removeClass("alert-success");
            $("#percentageInfo").addClass("alert-danger");
        }else{
            $("#percentageInfo").removeClass("alert-danger");
            $("#percentageInfo").addClass("alert-success");
        }
    }
    
    function submitBuySellStocks(thisObj, thisEvent) {
        if($("#percentageInfo").hasClass("alert-danger")){
            $("#buySellStocksError").text('Please make sure stock percentages are correct.');
            return;
        }
        if($("#balanceInfo").hasClass("alert-danger")){
            $("#buySellStocksError").text('Please make sure balance does not exceed 10% of portfolio value.');
            return;
        }
        var data = {
              remainingBalance: $("#bsBalance").text(),
              portfolioId: $("#portfolioId").text(),
              dateTime:(new Date()).toISOString().substring(0,10),
                  companies:[]
        };
        var stocks = [];
        $("#bsTable").find('tr').each(function (i) {
            var totalValue = $(this).closest('tr').find("label[id^='bsSpent']").text();
            if(totalValue != 0){
                var symbol = $(this).closest('tr').find("[id^='bsSymbol']").text();
                if(symbol == "" || symbol == null)
                	symbol = $(this).closest('tr').find("[id^='bsSymbol']").val();
                alert("symbol: " + symbol);
                
                var numberOfShares = $(this).closest('tr').find("[id^='bsValShares']").text();  
                if(numberOfShares == "" || numberOfShares == null)
                	numberOfShares = $(this).closest('tr').find("[id^='bsValShares']").val();  
                alert("nos: " + numberOfShares);
                
                stocks.push({symbol:symbol,numberOfShares:numberOfShares,totalValue:totalValue});
            }
        });
            
        data.companies = stocks;
        var jason = JSON.stringify(data);
        alert(jason);
        $.ajax({
            contentType: 'application/json',
            data: jason,
            dataType: 'json',
            success: function(data){
                if(data.error == null){
                    location.href='GetUser?id=' + $("#userId").text() + '&index=' + $("#portfolioId").text();
                }else{
                    $("#buySellStocksError").text(data.error);
                }
            },
            error: function(){
                $("#buySellStocksError").text('Something went wrong.');
            },
            processData: false,
            type: 'POST',
            url: 'BuySellStocks'
        });
        return false;
    }
    
    //NEW PORTFOLIO SECTION
    function calculateNewPortfolioBalance(){
        var spent = 0;
        $("#newPortfolioTable").find('tr').each(function (i) {
            var type = $(this).closest('tr').find("label[id^='stockType']").text();
            if(type == "DOW30" || type == "NIFTYFIFTY")
                spent += parseFloat($(this).closest('tr').find("label[id^='totalPrice']").text());
        });
        var deposit =  parseFloat($("#depositCash").val());
        if(isNaN(deposit)) deposit = 0;
        var remaining = deposit - spent;
        $("#npBalance").text(remaining.toFixed(2));
        if((remaining < 0) || (remaining > deposit * 10 / 100)){
            $("#newPortfolioBalanceInfo").removeClass("alert-success");
            $("#newPortfolioBalanceInfo").addClass("alert-danger");
        }else{
            $("#newPortfolioBalanceInfo").removeClass("alert-danger");
            $("#newPortfolioBalanceInfo").addClass("alert-success");
        }
    }
    
    function calculateNewPortfolioPercentages() {
        var dow30Value = 0;
        var niftyValue = 0;
        $("#newPortfolioTable").find('tr').each(function (i) {
            var type = $(this).closest('tr').find("label[id^='stockType']").text();
            var spent = $(this).closest('tr').find("label[id^='totalPrice']").text();
            if(type == "DOW30"){
                dow30Value = dow30Value + parseFloat(spent);
            }else if(type == "NIFTYFIFTY"){
                niftyValue = niftyValue + parseFloat(spent);
            }
        });
        var dow30Percentage = dow30Value / (dow30Value + niftyValue) * 100;
        $("#npPercentage1").text(dow30Percentage.toFixed(1));
        $("#npPercentage2").text((100 - dow30Percentage).toFixed(1));
        if(dow30Percentage.toFixed(1) < 68 || dow30Percentage.toFixed(1) > 72){
            $("#newPortfolioPercentageInfo").removeClass("alert-success");
            $("#newPortfolioPercentageInfo").addClass("alert-danger");
        }else{
            $("#newPortfolioPercentageInfo").removeClass("alert-danger");
            $("#newPortfolioPercentageInfo").addClass("alert-success");
        }
    }
    
    function submitNewPortfolio(thisObj, thisEvent) {
        if($("#newPortfolioName").val() == '' || $("#newPortfolioName").val() == null){
            $("#newPortfolioError").text('Please enter a portfolio name.');
            return;
        }
        if($("#depositCash").val() == '' || $("#depositCash").val() == null){
            $("#newPortfolioError").text('You must deposit cash to purchase stocks.');
            return;
        }
        var data = {
              userId: $("#userId").text(),
              portfolioName: $("#newPortfolioName").val(),
              DOT:(new Date()).toISOString().substring(0,10),
              moneyUSD: $("#depositCash").val(),
              companies:[]
        };
        var stocks = [];
        $("#newPortfolioTable").find('tr').each(function (i) {
            var stock = $(this).closest('tr').find("input[id^='newStock']").val();
            var unitPrice = $(this).closest('tr').find("label[id^='stockUnitPrice']").text();
            if(unitPrice != '' && unitPrice != null){
                var shares = $(this).closest('tr').find("input[id^='newShares']").val();
                stocks.push({sym:stock,shares:shares});     
            }
        });
        
        if(stocks.length > 10 || stocks.length < 7){
            $("#newPortfolioError").text('You must enter at least 7 stocks.');
            return;
        }
            
        data.companies = stocks;
        var jason = JSON.stringify(data);
        
        $.ajax({
            contentType: 'application/json',
            data: jason,
            dataType: 'json',
            success: function(data){
                if(data.pid == null){
                    $("#newPortfolioError").text(data.resp);
                }else{
                    location.href='GetUser?id=' + $("#userId").text() + '&index=' + data.pid;   
                }
            },
            error: function(){
                $("#newPortfolioError").text('Something went wrong.');
            },
            processData: false,
            type: 'POST',
            url: 'AddPortfolio'
        });
        
        return false;  // prevents the page from refreshing before JSON is read from server response
    }

    $('#file').on('change', function(e) {
        var file = document.getElementById('file').files[0];

        var reader = new FileReader();
        reader.readAsText(file);
        reader.onload = function(e) {
        	var lines = reader.result.split("\r\n");
        	if(lines.length >= 10) alert('File not in correct format.');
        	for(var i = 0; i < lines.length; i ++){
        		var info = lines[i].split(",");
        		$('#newStock' + i).val(info[0]);
        		$('#newStock' + i).blur();
        		$('#newShares' + i).val(info[1]);
        		$('#newShares' + i).blur();
        	}
        }
    });
    
    function refreshModal(thisObj, thisEvent) {
        $('#newPortfolioName').val('');
        $("#depositCash").val('');
        $("#newPortfolioError").text('');
        $("#newPortfolioTable").find('tr').each(function (i) {
            $(this).find('input').each(function (i) {
                $(this).val('');
            });
            $(this).find('label').each(function (i) {
                $(this).text('');
            });
        });
    }
    </script>
    
</body>
</html>