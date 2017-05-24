<%@page import="com.github.jjYBdx4IL.maven.examples.aspectj.ContentGenerator"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2><%= new ContentGenerator().getContent() %></h2>
    </body>
</html>
