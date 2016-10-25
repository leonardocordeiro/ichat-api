<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<fieldset>
	<legend>Mensagens</legend>
	<div id="mensagens"></div>
</fieldset>
<br>
<div>
	Texto: <input type="text" id="text">
	<button onclick="enviar()">Enviar</button>
</div>

<script src="js/jquery-3.0.0.min.js"></script>
<script>
	function poll() {
		$.ajax({ 
			type: "GET",
			cache: false,
			url: "polling",
			success: function(mensagem) {
				if(mensagem.text != null)
					$("#mensagens").append(mensagem.text + "<br>");
				poll();
			},
			error: function(err) { 
				poll();
			}
		});
	};
	poll();
	
	function enviar() {
		$.ajax({ 
			type: "POST",
			cache: false,
			contentType: "application/json",
			url: "polling",
			data: JSON.stringify({ "text" : $("#text").val() })
		});
	}
	
</script>
</body>
</html>