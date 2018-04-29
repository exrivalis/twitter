package service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import bd.Database;
import tools.FriendTools;
import tools.ServicesTools;
import tools.UserTools;

public class Friend {
	//ajout ami avec key et friend_id
	public static JSONObject addFriend(String key, String id_friend) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//etablissement connexion
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    
	    JSONObject res = ServicesTools.serviceAccepted("Ami ajouté");
	    if(UserTools.checkKeyValidity(key, co)) {
	   		// recuperer l'id via l'index
        int id = UserTools.getIdWithKey(key, co);
        
        //si id_friend exsite on continue sinon refuser service
        if(!UserTools.checkID(id_friend, co)) {
        	//id_friend n'existe pas
        	res = ServicesTools.serviceRefused("id_friend incorrect", -1);
        }else if(id == -1){
        	//key invalide
        	res = ServicesTools.serviceRefused("key invalide", -1);
        }else {
        	//tout va bien
        	
        	//Ajout dans la table amis de la relation
            String query = "INSERT INTO friends VALUES('"+id+"', '"+id_friend+ "', CURRENT_DATE());";
            Statement st = co.createStatement();
            //executer requete
            try {
    			st.executeUpdate(query);
    		} catch (SQLException e) {
    			res = ServicesTools.serviceRefused("error add to db", -1);
    		}
            //close connections
            st.close();
        }
        
	   	}
	   	else {
	   		res = ServicesTools.serviceRefused("timeout", -1);
	   	}
    	
	    //close connection
	    co.close();
	    return res;
	}
	
	//supprimer ami avec key et friend_id
	public static JSONObject removeFriend(String key, String id_friend) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//etablissement connexion
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    
	    JSONObject res = ServicesTools.serviceAccepted("Ami supprimé");
	    if(UserTools.checkKeyValidity(key, co)) {
	   		// recuperer l'id via l'index
        int id = UserTools.getIdWithKey(key, co);
        //si key valide
        if(id == -1) {
        	res = ServicesTools.serviceRefused("key invalide", -10);
        }else if(!UserTools.checkID(id_friend, co)){
        	res = ServicesTools.serviceRefused("id_friend invalide", -10);
        }else {
        	Statement st = co.createStatement();
        	
        	//verif s'ils sont amis ou si id s'invente une vie
        	String query = "SELECT * FROM friends WHERE source = '"+id+"' AND cible = '"+id_friend+"';";
        	ResultSet cursor = st.executeQuery(query);
        	
        	if(cursor.next()) {
        		//supprimer de la table
                query = "DELETE FROM friends WHERE source = "+id+" AND cible = "+id_friend+ ";";	
                st.executeUpdate(query);
        	}else {
        		res = ServicesTools.serviceRefused("ne sont pas amis", -10);
        	}
        	
            
            //close connections
            st.close();
        }
	   	}
	   	else {
	   		res = ServicesTools.serviceRefused("timeout", -1);
	   	}
    	
	    //close connection
	    co.close();
	    return res;
	}
	
	//cherche un ami avec nom et prenom
	public static JSONObject search(String key, String query) throws SQLException, JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//etablissement connexion
		//System.out.println("hjehkfjhekhf");
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
		String [] names = query.split(" ");
		JSONObject users = new JSONObject();
		JSONObject result = ServicesTools.serviceRefused("auucn resultat", -10);;
		if(UserTools.checkKeyValidity(key, co)) {
	   		//pour chaque mot dans names faire un recherche de nom etde prenom
			for(String name: names) {
				JSONObject tmpres = searchNom(name);
				if(tmpres.get("message").equals("found")) {
					
					Iterator<String> keysItr = tmpres.getJSONObject("users").keys();
					while(keysItr.hasNext()) {
						//System.out.println("ok");
						String k = keysItr.next();
						users.put(k, tmpres.getJSONObject("users").get(k));
					}			
				}
				tmpres = searchPrenom(name);
				if(tmpres.get("message").equals("found")) {
					
					Iterator<String> keysItr = tmpres.getJSONObject("users").keys();
					while(keysItr.hasNext()) {
						//System.out.println("ok");
						String k = keysItr.next();
						users.put(k, tmpres.getJSONObject("users").get(k));
					}			
				}
			}
	   	}
	   	else {
	   		co.close();
	   		return ServicesTools.serviceRefused("timeout", -1);
	   	}
		
		co.close();	
	    if(users.length() > 0)
	    	return ServicesTools.serviceAccepted("found").put("users", users);
	    return ServicesTools.serviceAccepted("notfound").put("users", users);
	}
	
	//cherche un ami avec nom
	public static JSONObject searchNom(String nom) throws SQLException, JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//etablissement connexion
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	    JSONObject result = FriendTools.search(nom, null, co);
	    co.close();
	    return result;
	}
	
	//cherche un ami avec prenom
	public static JSONObject searchPrenom(String prenom) throws SQLException, JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//etablissement connexion
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			    Connection co = Database.getMySQLConnection();
			    
			    JSONObject result = FriendTools.search(null, prenom, co);
			    co.close();
			    return result;
	}
	
	//recup amis du user
	public static JSONObject ListFriends(String key) throws SQLException, JSONException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//etablissement connexion
		Class.forName("com.mysql.jdbc.Driver").newInstance();
	    Connection co = Database.getMySQLConnection();
	   	int user_id = UserTools.getIdWithKey(key, co);
	   	JSONObject users = new JSONObject();
	   	JSONObject res = ServicesTools.serviceAccepted("liste friends");
	   	if(UserTools.checkKeyValidity(key, co)) {
	   		String query = "SELECT cible FROM friends WHERE source= '" +user_id+ "';";
	    	
	    	Statement st = co.createStatement();
			ResultSet cursor = st.executeQuery(query);
			while(cursor.next()) {
				//pour chaque id on recup user info
				//creation JSONObkect decrivant chaque user trouve
				int id = cursor.getInt("cible");
				JSONObject user = UserTools.getInfo(id, co);
				//System.out.println(id);
				
				users.put(""+id, UserTools.getInfo(id, co));
				
			}
			res.put("friends", users);
	   	}
	   	else {
	   		res = ServicesTools.serviceRefused("timeout", -1);
	   	}
    	
		co.close();
		return 	res;			
	}
}
