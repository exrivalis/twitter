package tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

public class UserTools {
	public UserTools() {
		
	}
	
	public static boolean userExists(String login, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//recup la ligne avec contenant ce login si elle existe
		String query = "SELECT * FROM users WHERE login=\"" + login + "\"";
		Statement st = co.createStatement();
		ResultSet cursor = st.executeQuery(query);
		boolean succeed = true;
		//si ma requete ne renvoie pas un resultat vide -> login deja utilise
		succeed = cursor.next();
		
		//close connections
		cursor.close();
		st.close();
		
		return succeed;
	}
	
	public static boolean checkPasswd(String login,String mdp, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//recup la ligne avec contenant ce login et mdp si elle existe
		String query = "SELECT * FROM users WHERE login=\"" + login + "\" AND psswd=\"" + mdp + "\";";
	
		Statement st = co.createStatement();
		ResultSet cursor = st.executeQuery(query);
		
		//si ma requete ne renvoie pas un resultat vide -> combi login, mdp correcte
		boolean succeed = cursor.next();
		
		//close connections
		cursor.close();
		st.close();
		
		return succeed;
	}
	
	//verif si id existe
	public static boolean checkID(String id, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//recup la ligne avec contenant ce login et mdp si elle existe
		String query = "SELECT * FROM users WHERE id='"+id+"';";
	
		Statement st = co.createStatement();
		ResultSet cursor = st.executeQuery(query);
		
		//si ma requete ne renvoie pas un resultat vide -> id existe
		boolean succeed = cursor.next();
		
		//close connections
		cursor.close();
		st.close();
		
		return succeed;
	}
	/*
	 * @param key
	 * @return true if connected
	 */
	public static boolean isConnected(String key, Connection co) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//recup la ligne avec contenant ce login
		String query = "SELECT * FROM session WHERE cle='"+key+"';";
	
		Statement st = co.createStatement();
		ResultSet cursor = st.executeQuery(query);
		boolean connected = false;
		while(cursor.next()) {
			connected = cursor.getBoolean("connected");
		}
		
		//close connections
		cursor.close();
		st.close();
		
		return connected;
	}
	
	public static String generateKey() {
		String key = "";
		char c;
		for(int i=0; i<32; i++) {
			Random r = new Random();
			if(Math.random() < 0.5) {
				//maj
				c = (char)(r.nextInt(26) + 'A');
				
			}else {
				//min
				c = (char)(r.nextInt(26) + 'a');
			}
			key += c;			
		}
		return key;		
	}
		
	//inserer une connection et retourne une cle
	public static String insertConnection(String login, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String userId = "\""+getUserId(login, co)+"\"";
		String key = generateKey();
		//ajout connection a la table session
		//SQL TRUE : 1, FALSE : 0
		
		//disconnect elsewhere
		String query = "UPDATE session SET connected='0' WHERE userId="+userId+";";
		Statement st = co.createStatement();
		st.executeUpdate(query);
		//connecte
		query = "INSERT INTO `session`(`cle`, `userId`, `connected`, `time`) VALUES ('"+key+"',"+userId+",'1','"+(new Timestamp(System.currentTimeMillis()))+"');";
		// = "INSERT INTO session VALUES('"+key+"', "+userId+ ", '1', '"+(new Timestamp(System.currentTimeMillis()))+"');";
		st.executeUpdate(query);
		
		//close connections
		st.close();
		
		return key;
	}
	
	//recup id selon login
	public static int getUserId(String login, Connection co) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//recup la ligne avec contenant ce login
		String query = "SELECT * FROM users WHERE login=\"" + login + "\";";
	
		Statement st = co.createStatement();
		ResultSet cursor = st.executeQuery(query);
		int id = -1;
		while(cursor.next()) {
			id = cursor.getInt("id");
		}
		
		//close connections
		cursor.close();
		st.close();
		
		return id;
	}
	
	//recup l'id d'un utilisateur en connaissant sa cle
	public static int getIdWithKey(String key, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
        //recupere l'id du detenteur de cette clé
        String query = "SELECT userId FROM session WHERE cle=\"" + key + "\";";
        Statement st = co.createStatement();
        ResultSet res = st.executeQuery(query);
        int id = -1;
        while(res.next()){
            id = res.getInt("userId");
        }
        //Close connections
        res.close();
        st.close();
        //si la cle est correcte on renvoie l'id, -1 sinon
        return id;
    }
	
	//recup info user avec id
	public static JSONObject getInfo(int id, Connection co) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, JSONException {
		JSONObject result = new JSONObject();
		//recupere l'id du detenteur de cette clé
        String query = "SELECT id, login, nom, prenom FROM users WHERE id='" + id + "';";
        List followers = new ArrayList<>();
        List followees = new ArrayList<>();
        Statement st = co.createStatement();
        ResultSet cursor = st.executeQuery(query);
        while(cursor.next()) {
        	result.put("id", cursor.getString("id"));
			result.put("login", cursor.getString("login"));
			result.put("nom", cursor.getString("nom"));
			result.put("prenom", cursor.getString("prenom"));
		}
        
        //recup nombre de follows et de followers
        String followsQuery = "SELECT * from friends WHERE source='"+id+"';";
        String followersQuery = "SELECT * from friends WHERE cible='"+id+"';";
        cursor = st.executeQuery(followsQuery);
        while(cursor.next()) {
        	
        	//result.put("follows", cursor.getInt(""));
        	followees.add(cursor.getString("source"));
        }
        
        
        cursor = st.executeQuery(followersQuery);
        while(cursor.next()) {
//        	result.put("followers", cursor.getArray("followers"));
        	followers.add(cursor.getString("source"));
        }
        
        
        //Close connections
        st.close();
        //si la cle est correcte on renvoie l'id, -1 sinon
        result.put("followers", followers);
        result.put("followees", followees);
        return result;
	}
	
	//verifier si une cle est toujours valide i.e. user actif
	public static boolean checkKeyValidity(String key, Connection co) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if(isConnected(key, co)){
			String query = "Select connected, time from session where cle='"+key+"';";
			Statement st = co.createStatement();
			ResultSet res = st.executeQuery(query);
			while(res.next()) {
				Timestamp time = new Timestamp(System.currentTimeMillis() - 10*60*1000); //10 minute
				Timestamp start = res.getTimestamp(2);
				//deco si inactif plus de x minutes
				if(time.after(start)) {
					//timeout : deco
					query = "update session SET connected='0' WHERE cle=\""+key+"\";";
					st.executeUpdate(query);
					return false;
				}else {
					//recharger key
					query = "update session SET time=\""+(new Timestamp(System.currentTimeMillis()))+"\" WHERE cle=\""+key+"\";";
					st = co.createStatement();
					st.executeUpdate(query);
					return true;
					
				}
			}
		}
		return false;
	}
   	
}