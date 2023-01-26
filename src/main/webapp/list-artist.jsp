<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Lista de Artistas</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
              integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
         <!-- Including jQuery -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js">
    </script>

    <!-- Including Bootstrap JS -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js">
    </script>
    </head>
    <body>
        <header>
            <nav class="navbar navbar-expand-md navbar-dark" style="background-color: tomato">
                <div>
                    <a href="https://www.unicauca.edu.co" class="navbar-brand"> App Music </a>
                </div>
                <ul class="navbar-nav">
                    <li><a href="<%=request.getContextPath()%>/list" class="nav-link">Lista Artist</a></li>
                </ul>
            </nav>
            <c:if test="${loginError != null}">
                <!-- Showing alert -->
                <div id="alert" class="alert alert-danger">
                    ${loginError}
                </div>

                <script type="text/javascript">
                    setTimeout(function () {

                        // Closing the alert
                        $('#alert').alert('close');
                    }, 5000);
                </script>
            </c:if>
        </header>
        <br>
        <div class="row">
            <!-- <div class="alert alert-success" *ngIf='message'>{{message}}</div> -->
            <div class="container">
                <h3 class="text-center">Lista de Artistas</h3>
                <hr>
                <div class="container text-left">
                    <a href="<%=request.getContextPath()%>/new" class="btn btn-success">Nuevo Artista</a>
                </div>
                <br>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- for (Todo todo: todos) { -->
                        <c:forEach var="artist" items="${listArtist}">
                            <tr>
                                <td>
                                    <c:out value="${artist.artistId}" />
                                </td>
                                <td>
                                    <c:out value="${artist.name}" />
                                </td>
                                <td><a href="edit?artistId=<c:out value='${artist.artistId}' />">Editar</a>
                                    &nbsp;&nbsp;&nbsp;&nbsp; <a href="delete?artistId=<c:out value='${artist.artistId}' />">Eliminar</a></td>
                            </tr>
                        </c:forEach>
                        <!-- } -->
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>