<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Todo Register</title>
</head>
<body>
	<form action="./register" method="post">
		<div>
			<input type="text" name="title" placeholder="INSERT TITLE">
		</div>
		<div>
			<input type="date" name="dueDate">
		</div>
		<div>
			<button type="reset">RESET</button>
			<button type="submit">REGISTER</button>
		</div>
	</form>
</body>
</html>