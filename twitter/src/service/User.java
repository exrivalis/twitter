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
			co.close();
			return ServicesTools.serviceRefused("arguments manquants", -1);
			}

		//tester si login deja utilise
		if(UserTools.userExists(login, co)) {
			co.close();
			return ServicesTools.serviceRefused("Login indisponnible", -1);
		}
		
		/*
		 * inserer user dans la BD 
		 */
		//resultat de retour ok/ko
		JSONObject result = ServicesTools.serviceAccepted("added succesfully");;
	
		//ajouter une ligne a la bd
		//ajout guillemets
		nom = nom.substring(0, 1).toUpperCase() + nom.substring(1, nom.length());
		prenom = prenom.substring(0, 1).toUpperCase() + prenom.substring(1, prenom.length());
		
		String query = "INSERT INTO users VALUES(null, '" + login +"','" + mdp + "','" + nom + "','" + prenom + "');";
		Statement st = co.createStatement();
		try {
			st.executeUpdate(query);
		} catch (SQLException e) {
			result = ServicesTools.serviceRefused("error add to db", -1);
		}
		
		//close connections
		co.close();
		st.close();		
		
		return result;
		
	}
	
	//se connecter avec un compte existant
	public static JSONObject login(String login, String mdp) throws JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//creation connection
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
		JSONObject res = ServicesTools.serviceAccepted("Disconnected");
		if (login == null || mdp == null) {
			res = ServicesTools.serviceRefused("argument manquant", -1);
		}
		
		
		else if(!tools.UserTools.userExists(login, co)) {
			//si l'utilisateur n'existe pas on arrete
		
			return ServicesTools.serviceRefused("Utilisateur inexistant", -1);
		}
		
		else if(!tools.UserTools.checkPasswd(login, mdp, co)) {
			//si le mdp incorrect on arrete
			res = ServicesTools.serviceRefused("Mot de passe incorrect", -1);
		}
		else {
			//generate new connection key
			String key = UserTools.insertConnection(login, co);
			int id = UserTools.getUserId(login, co);
			res = ServicesTools.serviceAccepted("succesfully loged in");
			try {
				res.put("key", key);
				res.put("id",  id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		co.close();
		return res;
	}
	
	
	//se deconnecter
	public static JSONObject logout(String key) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//creation connection
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    JSONObject result = new JSONObject();
	    
		if (key == null) {
			result = ServicesTools.serviceRefused("argument manquant", -1);
		}

		//si deja deconnecte on fait rien sinon on le deconnecte
		else if(!UserTools.isConnected(key, co)) {
			result = ServicesTools.serviceRefused("User deja deconnecté", -1);
			
		}
		else {
		//modifier la ligne correspondante en inserant 0 au chanmp connected
			//SQL TRUE : 1, FALSE : 0
			String query = "UPDATE session SET connected = '0' WHERE cle = \""+key+"\";";
			Statement st = co.createStatement();
			st.executeUpdate(query);
			st.close();
			result = ServicesTools.serviceAccepted("Disconnected");
					
		}		
		//close connections
		co.close();
		return result;
		
	}

	//get user info
	public static JSONObject getInfo(String key, int id) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JSONException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    JSONObject result = ServicesTools.serviceAccepted("user info");
	  	if(UserTools.checkKeyValidity(key, co)) {
	   		 if(UserTools.checkID(Integer.toString(id), co)) {
		    	//recup nom, prenom, login ...
		    	JSONObject info = UserTools.getInfo(id, co);
		    	result.put("info", info);	
		    }else {
		    	result = ServicesTools.serviceRefused("arguments manquants", -1);
		    }
	   	}
	   	else {
	   		result = ServicesTools.serviceRefused("timeout", -1);
	   	}
	   
	    co.close();
	    return result;
	}
}