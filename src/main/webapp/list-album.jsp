<%-- 
    Document   : list-album
    Created on : 25/01/2023, 1:23:44 p. m.
    Author     : Danny
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Lista de Clientes</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    </head>
    <body>
        <header>
            <nav class="navbar navbar-expand-md navbar-dark" style="background-color: tomato">
                <div>
                    <a href="https://www.unicauca.edu.co" class="navbar-brand"> App Music </a>
                </div>
                <ul class="navbar-nav">
                    <li><a href="<%=request.getContextPath()%>/albums/list" class="nav-link">Lista Album</a></li>
                </ul>
            </nav>
        </header>
        <br>
        <div class="row">
            <!-- <div class="alert alert-success" *ngIf='message'>{{message}}</div> -->
            <div class="container">
                <h3 class="text-center">Lista de Albums</h3>
                <hr>
                <div class="container text-left">
                    <a href="<%=request.getContextPath()%>/new" class="btn btn-success">Nuevo Album</a>
                </div>
                <br>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Titulo</th>
                            <th>Artista</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>

                        <!-- for (Todo todo: todos) { -->
                        <c:forEach var="album" items="${listaAlbum}">
                            <tr>
                                <td>
                                    <c:out value="${album.albumId}" />
                                </td>
                                <td>
                                    <c:out value="${album.title}" />
                                </td>
                                <td>
                                    <c:out value="${album.artistId.name}" />
                                </td>
                                <td>
                                    <a href="albums/edit?id=<c:out value='${album.albumId}' />">Editar</a>
                                    <a href="albums/delete?id=<c:out value='${album.albumId}' />">Eliminar</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <!-- } -->
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
