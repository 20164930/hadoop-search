<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="searcher.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" href="show.css" type="text/css" />
</head>
<body>

<div class="search bar7">
        <form action="<%=request.getContextPath()%>/get.html" method="post">
            <input type="text" placeholder="请输入关键字" name="text">
            <button type="submit"></button>
        </form>
</div>


<div class="show">
	<%
		Url_power[] urls=(Url_power[])request.getAttribute("urls");
				for(int i=0;i<urls.length;i++){
					String[] line=urls[urls.length-i-1].getUrl().split("#");
					int power=urls[urls.length-i-1].getPower();
			out.print("<span style='font-size:20px'><b>链接:</b></span> &nbsp;&nbsp;<a href="+line[0]
					+">"+line[1]+"</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
			"<span style='font-size:18px'><b>匹配度：</b></span> &nbsp;&nbsp;"+power+"<br><br>");
		}
	%>
</div>	
</body>
</html>