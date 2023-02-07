<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="mvc.model.BoardDTO" %>

<%
    BoardDTO notice = (BoardDTO) request.getAttribute("board");
    int num = ((Integer) request.getAttribute("num")).intValue();
    int nowpage = ((Integer) request.getAttribute("page")).intValue();
%>
<!doctype html>
<html lang="ko">
<head>
    <link rel="stylesheet" href="../resources/css/bootstrap.min.css"/>
    <meta charset="UTF-8">
    <title>Board</title>
    <script>
        let goUpdate=function() {
            const frm = document.frmUpdate;
            // document.querySelector("#del").setAttribute("href", "./BoardDeleteAction.do?num=<%=notice.getNum()%>&pageNum=<%=nowpage%>");
            frm.action="../boardController/BoardUpdateForm.do";
            frm.submit();
        }
        let goDelete = function(){
            if(confirm("삭제하시겠습니까?"))
            {
                const frm = document.frmUpdate;
                frm.action = "../boardController/BoardDeleteAction.do";
                frm.submit();
            }
        }
    </script>
</head>
<body>
<jsp:include page="../inc/menu.jsp" />
<div class="jumbotron">
    <div class="container">
        <h1 class="display-3">게시판</h1>
    </div>
</div>

<div class="container">
    <!--form name="newUpdate" class="form-horizontal"  action="./BoardUpdateAction.do?num=<%=notice.getNum()%>&pageNum=<%=nowpage%>" method="post"-->
        <div class="form-group  row">
            <label class="col-sm-2 control-label">성명</label>
            <div class ="col-sm-3"><%=notice.getName() %></div>

        </div>
        <div class="form-group  row">
            <label class="col-sm-2 control-label">제목</label>
            <div class="col-sm-5"><%=notice.getSubject() %></div>
        </div>
        <div class="form-group  row">
            <label class="col-sm-2 control-label">내용</label>
            <div class="col-sm-8" style="word-break: break-all">
               <%=notice.getContent()%>
            </div>
        </div>
        <div class="form-group row">
            <div class="col-sm-offset-2 col-sm-10 ">
                <c:set var="userId" value="<%=notice.getId()%>" />
                <c:if test="${sessionId==userId}">
                   <p>
                       <span class="btn btn-danger" onclick="goDelete();">삭제</span>
                       <span class="btn btn-success" onclick="goUpdate();">수정</span></p>

                   </p>
                </c:if>
                <a href="./BoardListAction.do?pageNum=<%=nowpage%>" class="btn btn-primary"> 목록</a>
            </div>
        </div>
    <!--/form-->
    <hr>

    <form name="frmUpdate" method="post">
        <input type="hidden" name="num" value="<%=num%>">
        <input type="hidden" name="pageNum" value="<%=nowpage%>">

    </form>


    <!-- 리플 목록. -->
    <hr>
    <c:forEach var="ripple" items="${rippleList}">
        <p>${ripple.content} | ${ripple.name}
            <c:if test="${sessionId == ripple.memberId}">
                <span class="btn btn-danger"  onclick="goRippleDelete('${ripple.rippleId}');">>삭제</span>
            </c:if>
        </p>
    </c:forEach>
    <form name="frmRippleDelete" class="form-horizontal" method="post">
        <input type="hidden" name="num" value="<%=notice.getNum() %>">
        <input type="hidden" name="pageNum" value="${page}">
        <input type="hidden" name="rippleId">
    </form>
    <script>
        function goRippleDelete (ID) {
            if (confirm("삭제하시겠습니까?")) {
                const frm= document.frmRippleDelete;
                frm.rippleId.value = ID;
                frm.action = "BoardRippleDeleteAction.do";
                frm.submit();
            }
        }
    </script>

<!--리플목록-->
<!-- 리플쓰기, 로그인 상태에서만 나옴. -->
        <hr>
        <c:if test="${sessionId != null}">
        <form name="frmRipple" class="form-horizontal" method="post">
            <input type="hidden" name="num" value="<%=notice.getNum() %>">
            <input type="hidden" name="pageNum" value="${page}">
            <div class="form-group row">
                <label class="col-sm-2 control-Label" >성명</label>
                <div class="col-sm-3">
                    <input name="name" type="text" class="form-control" value="${sessionMemberName}" placeholder="name">
                </div>
            </div>
            <div class="form-group row">
                <label class="col-sm-2 control-Label" >내용</label>
                <div class="col-sm-8" style="word-break: break-all;">
                    <textarea name="content" class="form-control" cols="50" rows="3"></textarea>
                </div>
            </div>
            <div class="form-group row">
            <label class="col-sm-2 control-Label"></label>
            <div class="col-sm-3">
                <span class="btn btn-primary" onclick="goRippleSubmit();">등록</span>
            </div>
            </div>
        </form>
        <script>
            function goRippleSubmit() {
                let frm = document.frmRipple;
                //컨트롤러로 전달
                frm.action = "BoardRippleWriteAction.do";
                frm.submit();
            }
        </script>
        </c:if>
</div>
    <!-- //리플 쓰기 -->
<jsp:include page="../inc/footer.jsp"/>
    </body>
</html>
