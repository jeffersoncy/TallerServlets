<%-- 
    Document   : album-form
    Created on : 25/01/2023, 2:44:18 p. m.
    Author     : Danny
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Formulario Album</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
              integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    </head>
    <body>
        <header>
            <nav class="navbar navbar-expand-md navbar-dark" style="background-color: #092168">
                <div>
                    <a href="https://www.unicauca.edu.co" class="navbar-brand"> App Music </a>
                </div>
                <ul class="navbar-nav">
                    <li><a href="<%=request.getContextPath()%>/list" class="nav-link">Lista Artist</a></li>
                </ul>

                <ul class="navbar-nav">
                    <li><a href="<%=request.getContextPath()%>/albums" class="nav-link">Lista Albums</a></li>
                </ul>

            </nav>
        </header>
        <br>
        <div class="container col-md-5">
            <div class="card">
                <div class="card-body">
                    <c:if test="${album != null}">
                        <form action="updatealbum" method="post">
                        </c:if>

                        <c:if test="${album == null}">
                            <form action="insertalbum" method="post">
                            </c:if>
                            <caption>
                                <h2>
                                    <c:if test="${album != null}">
                                        Editar Album
                                    </c:if>

                                    <c:if test="${album == null}">
                                        Nuevo Album
                                    </c:if>
                                </h2>
                            </caption>

                            <c:if test="${album != null}">
                                <input type="hidden" name="albumId" value="<c:out value='${album.albumId}' />" />
                            </c:if>

                            <c:if test="${album == null}">
                                <fieldset class="form-group">
                                    <label>Id del album</label> <input type="number" value="<c:out value='${album.albumId}' />"
                                                                       class="form-control" name="albumId" required="required" />

                                </fieldset>
                            </c:if>
                            <fieldset class="form-group">
                                <label>Titulo</label> <input type="text" value="<c:out value='${album.title}' />"
                                                             class="form-control" name="title" required="required">
                            </fieldset>

                            <fieldset class="form-group">
                                <label>Artista</label>
                                <br>
                                <select name="artist" class="form-select" aria-label="seleccione" style="width: 100%">

                                    <c:if test="${album == null}">
                                        <option selected>Seleccione un artista</option>
                                    </c:if>
                                    <c:forEach var="artist" items="${listaArtist}">
                                        <option value="<c:out value='${artist.artistId}' />"><c:out value='${artist.name}' /></option>
                                    </c:forEach>
                                    <c:if test="${album != null}">
                                        <option value="<c:out value='${album.artistId.artistId}' />" selected><c:out value='${album.artistId.name}' /></option>
                                    </c:if>
                                </select>
                            </fieldset>
                            <button type="submit" class="btn btn-success">Guardar</button>
                            <a href="<%=request.getContextPath()%>/albums" class="btn btn-danger">Cancelar</a>
                        </form>
                </div>
            </div>
        </div>
    </body>
</html>
