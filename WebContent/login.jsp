<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CS673 - Portfolio</title>

    <link rel="stylesheet" href="lib/bootstrap/dist/css/bootstrap-lumen.css" />
    <link rel="stylesheet" href="css/site.css" />
</head>
<body>
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <div class="navbar-brand">
                    CS673 - Portfolio
                </div>
            </div>
        </div>
    </nav>
    <div class="container body-content">
        <br><br><br>
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-body">
                        <form method="POST" class="form-horizontal"  action="Authenticate">
                            <fieldset>
                                <legend>Portfolio Login</legend><br>
                                <div class="form-group">
                                    <div class="col-lg-12">
                                        <input class="form-control" name="userName" id="userName" type="text" placeholder="User Name">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-lg-12">
                                        <input class="form-control" name="password" id="password" type="password" placeholder="Password">
                                    </div>
                                </div><br><br>
                                <div class="form-group">
								  <label for="comment">R Code:</label>
								  <textarea class="form-control" rows="7" name="rCode" id="rCode"></textarea>
								</div><br><br>
                                <div class="form-group">
                                    <div class="col-lg-12">
                                        <input name="submit" type="submit" value="login" class="btn btn-info btn-block" />
                                        <br>
                                        <span class="info">
                                            <a href="#register">Register</a>
                                        </span>
                                        <span class="pull-right">
                                            <a href="#forgot-password">Forgot Password</a>
                                        </span>
                                    </div>
                                </div>
                                <%String error=(String)request.getAttribute("error");
                                if(error != null){ %>
                                    <p class="text-danger"><%out.print(error);%></p>
                                <%}%>
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<footer>
    <hr />
    &copy; 2018 - Group M3
</footer>
</html>
