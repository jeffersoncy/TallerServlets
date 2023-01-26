<%-- 
    Document   : artist-form
    Created on : 25/01/2023, 3:06:28 p. m.
    Author     : jefer
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Formulario Artistas</title>
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
                    <li><a href="<%=request.getContextPath()%>/list" class="nav-link">Lista Artistas</a></li>
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
        <div class="container col-md-5">
            <div class="card">
                <div class="card-body">
                    <c:if test="${artist != null}">
                        <form action="update" method="post">
                        </c:if>

                        <c:if test="${artist == null}">
                            <form action="insert" method="post">
                            </c:if>
                            <caption>
                                <h2>
                                    <c:if test="${artist != null}">
                                        Editar Artista
                                    </c:if>

                                    <c:if test="${artist == null}">
                                        Nuevo Artista
                                    </c:if>
                                </h2>
                            </caption>

                            <c:if test="${artist != null}">

                                <input type="hidden" name="artistId" value="<c:out value='${artist.artistId}' />" />
                            </c:if>
                            <c:if test="${artist == null}">
                                <fieldset class="form-group">
                                    <label>Id Artista</label> <input type="number" value="<c:out value='${artist.artistId}' />"
                                                                     class="form-control" name="artistId" required="required">
                                </fieldset>
                            </c:if>
                            <fieldset class="form-group">
                                <label>Nombre</label> <input type="text" value="<c:out value='${artist.name}' />"
                                                             class="form-control" name="Name" required="required">
                            </fieldset>
                            <button type="submit" class="btn btn-success">Guardar</button>
                        </form>
                </div>
            </div>
        </div>
    </body>
</html>