function connexion(form){
    // console.log(form.html());
	var login = form.login.value;
    var passwd = form.psw.value;
    
	var ok = verif_connexion(login,passwd);
	if(ok){
        console.log("connexion")
		connecte(login,passwd);
	}else{
        console.log("error connexion");
    }

}


// JSON.parse(res, revival)
// integrer 
// //pour ajax 
// en GET
// url : "user/login" par exemple

//etablie la connexion avec le servlet user/login
function connecte(login, pswd){
    // !noConnection
    if(!noConnection){     
        console.log("dedans aussi")
        $.ajax({
            type:"GET",
            url:"user/login",//comme indiqué dans web.xml
            data:"login="+login+"&mdp="+pswd,
            datatype:"json",
            success :function(resp){console.log("reponse")},
            error:function(jqXHR, textStatus, errorThrown){alert(textStatus);},
        });
    }
}

//traitement de la reponse du server: parser le JSON reçu
function responseConnexion(resp){
    console.log("ok");
    alert("yahooo");

}

function erreur(message){
    var msg = "<div id =\"message_erreur\">"+message+"></div>";
    if(message.length <= 0){
        $("form").prepend(msg);
    }
    else{
        $("#erreur").replaceWith(msg);
    }    
    $("#erreur").css({"color":"red"}); 
}


function verif_connexion(login,passwd) {
	if(login.length == 0 || passwd.length == 0){
		erreur("Argument manquant");
		return false;
	}
	else{
		return true;
	}
}


function inscription(nom,prenom,login,email,mdp,check_mdp){



    var form = document.getElementById("form");
    form.addEventListener('submit', function(e) {
        var login = form.elements.login.value;
        var nom = form.elements.nom.value;
        var prenom = form.elements.prenom.value;
        var mail = form.elements.mail.value;
        var mdp = form.elements.mdp.value;
        console.log(inscription(nom,prenom,login,email,mdp,check_mdp));
            if(!ok){
                console.log("la");
            }else{
                console.log("ok");
                }
                                                
    });

    var mdp = form.elements.mdp.value;
    console.log(inscription(nom,prenom,login,email,mdp,check_mdp));
        if(!ok){
            console.log("la");
        }else{
            console.log("ok");
            }
                                                
};

// function connecte(login, passwd){
// 	if(!noConnection){
// 		$.ajax({
// 			type : "get",
// 			url : "/user/login",
// 			data : "login=" + login + "&password=" + password,
// 			success : function(rep){
// 				connexionResponse(rep);
// 			}
// 			error : function(jqXHR, textStatus, errorThrown){
// 				alert(textStatus),
// 			} 
// 		});
// 	}else{
// 		connexionResponse_local();
// 	}
	
// }

function connexionResponse(resp){
	var status = resp.status;
	if(status == "OK"){
		var res = resp.res;
		JSON.parse(res,revival);
	}



}

function connexionResponse_local(){
	return false;

}