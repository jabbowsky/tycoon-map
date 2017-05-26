<%-- 
    Document   : nameView
    Created on : May 13, 2017, 11:33:22 PM
    Author     : mikhail
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Page with input</title>
    </head>
    <body>
        <h1>Title just for fun!</h1>
        <spring:nestedPath path="name">
            <form action="" method="post">
                Name: 
                <spring:bind path="value">
                    <input type="text" name="${status.expression}" value="${status.value}">
                </spring:bind>
                    <input type="submit" value="send">
                </form>
        </spring:nestedPath>
    </body>
</html>
