<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Todo Read</title>
</head>
<body>
	<h1>Todo Read</h1>
	<form action="./read" method="get">
	    <div>
	        <h2>${dto.title}</h2>
	        <p>${dto.dueDate}</p>
	        <p>${dto.finished}</p>
	    </div>
</form>
</body>
</html>