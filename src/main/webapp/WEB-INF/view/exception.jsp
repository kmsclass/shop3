<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
<%-- /WEB-INF/view/exception.jsp 
     isErrorPage="true" : exception 객체가 내장 객체로 할당됨.--%>    
<script>
   alert("${exception.message}")
   location.href="${exception.url}"
</script>