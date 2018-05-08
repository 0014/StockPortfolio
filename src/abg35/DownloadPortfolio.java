package abg35;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileSystems;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/DownloadPortfolio")
public class DownloadPortfolio extends HttpServlet {
	
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    private String filePath = "C:\\Users\\arifg\\Desktop\\test\\portfolio.csv";
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition",
	                     "attachment;filename=downloadname.csv");
		ServletContext ctx = getServletContext();
		InputStream is = ctx.getResourceAsStream("/portfolio.txt");
		generateCsv();
		int read=0;
		byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
		OutputStream os = response.getOutputStream();

		while((read = is.read(bytes))!= -1){
			os.write(bytes, 0, read);
		}
		os.flush();
		os.close();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private void generateCsv() throws IOException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(FileSystems.getDefault().getPath(".") + "\\the-file-name.txt", "UTF-8");
		writer.println("The first line");
		writer.println("The second line");
		writer.close();
	}
	
	private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it.
                e.printStackTrace();
            }
        }
    }

}
