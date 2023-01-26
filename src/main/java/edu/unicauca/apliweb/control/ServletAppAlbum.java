/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.unicauca.apliweb.control;

import edu.unicauca.apliweb.persistence.entities.Album;
import edu.unicauca.apliweb.persistence.entities.Artist;
import edu.unicauca.apliweb.persistence.jpa.AlbumJpaController;
import edu.unicauca.apliweb.persistence.jpa.ArtistJpaController;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Danny
 */
@WebServlet("/albums")
public class ServletAppAlbum extends HttpServlet {
    
    private AlbumJpaController albumJPA;
    private ArtistJpaController artistJPA;
    private final static String PU = "edu.unicauca.apliweb_AppMusicWeb_war_1.0-SNAPSHOTPU";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getServletPath();
        System.out.println("accion servlet app album "+action);
        try {
            switch (action) {
                case "/albums/new":
                    showNewForm(request, response);
                    break;
                case "/albums/insert":
                    insertAlbum(request, response);
                    break;
                case "/albums/delete":
                    deleteAlbum(request, response);
                    break;
                case "/albums/edit":
                    System.out.println("Editando album");
                    showEditForm(request, response);
                    break;
                case "/albums/update":
                    updateAlbum(request, response);
                    break;
                default:
                    listAlbums(request,response);
                    break;
                    
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
        /*try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Aplicación Clientes Web con JPA</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Lista de Clientes en el Servlet " + request.getContextPath() + "</h1>");
            List<Album> listaAlbums = albumJPA.findAlbumEntities();
            for (Album album : listaAlbums) {
                System.out.println("Album: " + album.getTitle() +" Artista: " + album.getArtistId().getName());
                out.println("Album "+album.getTitle()+ " Artista: "+album.getArtistId().getName()+"<br>");
            }
            out.println("</body>");
            out.println("</html>");
        }*/
    }
    
    @Override
    public void init() throws ServletException{
        super.init();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU);
        albumJPA = new AlbumJpaController(emf);
        artistJPA = new ArtistJpaController(emf);
        List<Album> listaAlbums = albumJPA.findAlbumEntities();
        List<Artist> listaArtistas = artistJPA.findArtistEntities();
    }
    
    private void listAlbums(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        List<Album> listaAlbums = albumJPA.findAlbumEntities();
        request.setAttribute("listaAlbum", listaAlbums);
        //TODO
        RequestDispatcher dispatcher = request.getRequestDispatcher("list-album.jsp");

        dispatcher.forward(request, response);
    }
    
    //muestra el formulario para crear un nuevo album
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        List<Artist> listaArtistas = artistJPA.findArtistEntities();
        request.setAttribute("listaArtist",listaArtistas );
        RequestDispatcher dispatcher = request.getRequestDispatcher("album-form.jsp");
        dispatcher.forward(request, response);
    }
    
    //muestra el formulario para editar un album
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        //toma el id del album a ser editaro
        int id = Integer.parseInt(request.getParameter("id"));
         List<Artist> listaArtistas = artistJPA.findArtistEntities();
        //busca al album en la base de datos
        Album existingAlbum = albumJPA.findAlbum(id);
        RequestDispatcher dispatcher = null;
        if (existingAlbum != null) {
            //si lo encuentra lo envía al formulario
            //TODO
            request.setAttribute("listaArtist",listaArtistas );
            dispatcher = request.getRequestDispatcher("album-form.jsp");
            request.setAttribute("album", existingAlbum);
        } else {
            //si no lo encuentra regresa a la página con la lista de los albums
            dispatcher = request.getRequestDispatcher("list-album.jsp");
        }
        dispatcher.forward(request, response);
    }
    
    //método para crear un album en la base de datos
    private void insertAlbum(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, Exception {
        //toma los datos del formulario de albums
        String albumId = request.getParameter("albumId");
        String title = request.getParameter("title");
        String artistId = request.getParameter("artist");
        
        Artist selectedArtist = artistJPA.findArtist(Integer.parseInt(artistId));
        
        //crea un objeto de tipo album vacío y lo llena con los datos obtenidos
        Album al = new Album();
        al.setAlbumId(Integer.parseInt(albumId));
        al.setTitle(title);
        System.out.println(albumId);
        System.out.println(title);
        System.out.println(selectedArtist.getArtistId());
        System.out.println(selectedArtist.getName());
        
        //System.out.println(request.getAttribute("artist"));
        al.setArtistId(selectedArtist);
        //Crea el cliente utilizando el objeto controlador JPA
        albumJPA.create(al);
        //solicita al Servlet que muestre la página actualizada con la lista de clientes
        response.sendRedirect("albums");
    }
    
    //Método para editar un album
    private void updateAlbum(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        //toma los datos enviados por el formulario de albums
        int id_album = Integer.parseInt(request.getParameter("albumId"));
        String albumId = request.getParameter("albumId");
        String title = request.getParameter("title");
        String artistId = request.getParameter("artist");
        
        Artist selectedArtist = artistJPA.findArtist(Integer.parseInt(artistId));
        
        
        //crea un objeto vacío y lo llena con los datos del album
        Album al = new Album();
        al.setAlbumId(Integer.valueOf(albumId));
        al.setTitle(title);
        al.setArtistId(selectedArtist);
        try {
            //Edita el cliente en la BD
            System.out.println(al.getAlbumId());
            System.out.println(al.getTitle());
            System.out.println(al.getArtistId().getArtistId());
            System.out.println(al.getArtistId().getName());
            albumJPA.edit(al);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppAlbum.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.sendRedirect("albums");
    }
    
    //Elimina un album de la BD
    private void deleteAlbum(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        //Recibe el ID del album que se espera eliminar de la BD
        int id = Integer.parseInt(request.getParameter("id"));
        try {
        //Elimina el album con el id indicado
            albumJPA.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ServletAppAlbum.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.sendRedirect("albums");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppAlbum.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppAlbum.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
