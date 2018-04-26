package service;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import bd.Database;
import tools.ServicesTools;
import tools.UserTools;

public class User {
	//creer un nouvel utilisateur
	public static JSONObject createUser(String login, String mdp, String nom, String prenom) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//creation connection
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    
		if (login == null || mdp == null || nom == null || prenom == null) {			
			//informer du succes ou echec de l'operation
			return ServicesTools.serviceRefused("arguments manquants", -1);
			}

		//tester si login deja utilise
		if(UserTools.userExists(login, co))
			return ServicesTools.serviceRefused("Login indisponnible", -1);
		
		/*
		 * inserer user dans la BD 
		 */
		//resultat de retour ok/ko
		JSONObject result = ServicesTools.serviceAccepted("added succesfully");;
	
		//ajouter une ligne a la bd
		//ajout guillemets
		login = "\"" + login + "\"";
		nom = "\"" + nom + "\"";
		prenom = "\"" + prenom + "\"";
		mdp = "\"" + mdp + "\"";
		
		String query = "INSERT INTO users VALUES(null, " + login +"," + mdp + "," + nom + "," + prenom + ");";
		Statement st = co.createStatement();
		try {
			st.executeUpdate(query);
		} catch (SQLException e) {
			result = ServicesTools.serviceRefused("error add to db", -1);
		}
		
		//close connections
		st.close();		
		
		return result;
		
	}
	
	//se connecter avec un compte existant
	public static JSONObject login(String login, String mdp) throws JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//creation connection
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
		//SONObject res;
		if (login == null || mdp == null) {
			return ServicesTools.serviceRefused("argument manquant", -1);
		}
		
		//si l'utilisateur n'existe pas on arrete
		boolean is_user = tools.UserTools.userExists(login, co);
		if(!is_user) {
			return ServicesTools.serviceRefused("Utilisateur inexistant", -1);
		}
		
		//si le mdp incorrect on arrete
		boolean mdp_ok = tools.UserTools.checkPasswd(login, mdp, co);
		if(!mdp_ok) {
			return ServicesTools.serviceRefused("Mot de passe incorrect", -1);
		}
	
		//generate new connection key
		String key = UserTools.insertConnection(login, co);
		int id = UserTools.getUserId(login, co);
		JSONObject res = ServicesTools.serviceAccepted("succesfully loged in");
		try {
			res.put("key", key);
			res.put("id",  id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	
	//se deconnecter
	public static JSONObject logout(String key) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//creation connection
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    
		if (key == null) {
			return ServicesTools.serviceRefused("argument manquant", -1);
		}

		//si deja deconnecte on fait rien sinon on le deconnecte
		if(!UserTools.isConnected(key, co)) {
			return ServicesTools.serviceRefused("User deja deconnecté", -1);
			
		}
		else {
		//modifier la ligne correspondante en inserant 0 au chanmp connected
			//SQL TRUE : 1, FALSE : 0
			String query = "UPDATE session SET connected = '0' WHERE cle = \""+key+"\";";
			Statement st = co.createStatement();
			st.executeUpdate(query);
					
			//close connections
			st.close();
		}		
		
		return ServicesTools.serviceAccepted("Disconnected");
		
	}

	//get user info
	public static JSONObject getInfo(int id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JSONException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    JSONObject result = ServicesTools.serviceAccepted("user info");
	    
	    if(UserTools.checkID(Integer.toString(id), co)) {
	    	//recup nom, prenom, login ...
	    	JSONObject info = UserTools.getInfo(id, co);
	    	result.put("info", info);	
	    }else {
	    	result = ServicesTools.serviceRefused("arguments manquants", -1);
	    }
	    
	    return result;
	}
}