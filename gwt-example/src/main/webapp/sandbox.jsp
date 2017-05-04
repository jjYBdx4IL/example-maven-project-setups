<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%><!DOCTYPE html>
<%@page import="java.util.Enumeration"%>
<% // force session: %>
<% request.getSession().setMaxInactiveInterval(7*24*3600); %>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <meta name="gwt:property" content="locale=en">
        <title>GWT Sandbox</title>
        <script type="text/javascript" src="sandbox/sandbox.nocache.js"></script>
    </head>
</html>
