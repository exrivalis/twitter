package servlet.user;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONObject;

import tools.ServicesTools;

public class PicturePath extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		//recup params
		String key = request.getParameter("key");
		//String path = request.getParameter("path");
		JSONObject result = new JSONObject();
		//System.out.println("login servlet");
		System.out.println(key);
		
		Part part = request.getPart("file");
	
		System.out.println(part);
		
		File picture = new File("./profilepicture.png");
		//part.write("./profilepicture.png");
//		if(key == null || path == null) {
//			result = ServicesTools.serviceRefused("Arguments invalides", -1);
//		}
//		else if(key.length() == 0 || path.length() == 0)
//			result = ServicesTools.serviceRefused("Arguments invalides", -1);
//		
//		else {
//			//get user info
//			try {
//				result = service.User.setPicturePath(key, path);
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
//		
		out.print(ServicesTools.serviceAccepted("OK").toString());
	}
}