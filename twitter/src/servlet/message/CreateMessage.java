package servlet.message;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/*
 * cree un nouvel utilisateur et l'ajout a la bd
 * @param user_id, 
 * @return JSONObject decrivant le deroulement de l'operation
 */
public class CreateMessage extends HttpServlet{
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//recup parametre
		String key = request.getParameter("key");
		String text = request.getParameter("text");
		String cible = request.getParameter("cible");
		JSONObject result = new JSONObject();//contien la reponse seucces/echec
		if(key==null || text==null || cible == null) {
			result = tools.ServicesTools.serviceRefused("argument invalides", -1);
		}
		else {
			try {
				result = service.Message.createMessage(key, text, cible);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.print(result.toString());
	}
}
