<%--
  Created by IntelliJ IDEA.
  User: parksohee
  Date: 2023/02/03
  Time: 9:28 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form id="form1" action="./modify" method="post">
    <input type="text" name="tno" value="${dto.tno}" readonly>
    </div>
    <div>
        <div>
            <input type="text" name="title" value="${dto.title}" >
        </div>
        <div>
            <input type="date" name="dueDate" value="${dto.dueDate}">
        </div>
        <div>
            <input type="checkbox" name="finished" ${dto.finished ? "checked": ""}
        </div>
        <div>
            <button type="submit">Modify</button>
        </div>
    </div>
</form>


<form id="form2" action="./remove" method="post">
    <input type="hidden" name="tno" value="${dto.tno}" readonly>
    <div>
        <button type="submit">Remove</button>
    </div>
</form>

</body>
</html>
