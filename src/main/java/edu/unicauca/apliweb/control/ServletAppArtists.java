/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package edu.unicauca.apliweb.control;

import edu.unicauca.apliweb.persistence.entities.Artist;
import edu.unicauca.apliweb.persistence.jpa.ArtistJpaController;
import edu.unicauca.apliweb.persistence.jpa.exceptions.IllegalOrphanException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.NonexistentEntityException;
import edu.unicauca.apliweb.persistence.jpa.exceptions.PreexistingEntityException;
import java.io.IOException;
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
 * @author jefer
 */
@WebServlet("/")
public class ServletAppArtists extends HttpServlet {

    private ArtistJpaController artistJPA;
    private final static String PU = "edu.unicauca.apliweb_AppMusicWeb_war_1.0-SNAPSHOTPU";

    @Override
    public void init() throws ServletException {
        super.init();
//creamos una instancia de la clase EntityManagerFactory
//esta clase se encarga de gestionar la construcción de entidades y
//permite a los controladores JPA ejecutar las operaciones CRUD
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU);
//creamos una instancia del controldor JPA para la clase clients y le
//pasamos el gestor de entidades
        artistJPA = new ArtistJpaController(emf);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws edu.unicauca.apliweb.persistence.jpa.exceptions.IllegalOrphanException
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, IllegalOrphanException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getServletPath();
        System.out.println("accion servlet app artist "+action);
//toma la acción solicitada desde la petición enviada al Servlet
        try {
            switch (action) {
                case "/new": //Muestra el formulario para crear un nuevo cliente
                    showNewForm(request, response);
                    break;
                case "/insert": //ejecuta la creación de un nuevo cliente en la BD 
                    insertArtist(request, response);
                    break;
                case "/delete": //Ejecuta la eliminación de un cliente de la BD
                    deleteArtist(request, response);
                    break;
                case "/edit": //Muestra el formulario para editar un cliente
                    showEditForm(request, response);
                    break;
                case "/update": //Ejecuta la edición de un cliente de la BD
                    updateArtist(request, response);
                    break;
                default:
                    listArtist(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listArtist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<Artist> listaArtist = artistJPA.findArtistEntities();
        request.setAttribute("listArtist", listaArtist);
        RequestDispatcher dispatcher = request.getRequestDispatcher("list-artist.jsp");
        dispatcher.forward(request, response);
    }

//muestra el formulario para crear un nuevo usuario
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("artist-form.jsp");
        dispatcher.forward(request, response);
    }

    //muestra el formulario para editar un usuario
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
//toma el id del cliente a ser editaro
        int artistId = Integer.parseInt(request.getParameter("artistId"));
//busca al cliente en la base de datos
        Artist existingArtist = artistJPA.findArtist(artistId);
        RequestDispatcher dispatcher = null;
        if (existingArtist != null) {
//si lo encuentra lo envía al formulario
            dispatcher = request.getRequestDispatcher("artist-form.jsp");
            request.setAttribute("artist", existingArtist);
        } else {
            request.setAttribute("loginError","Captura de error : The artist with id " + artistId + " no longer exists.");
            //si no lo encuentra regresa a la página con la lista de los clientes
            dispatcher = request.getRequestDispatcher("list");
        }
        dispatcher.forward(request, response);
    }

    //método para crear un cliente en la base de datos
    private void insertArtist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, Exception {
//toma los datos del formulario de clientes
        int artistId = Integer.parseInt(request.getParameter("artistId"));
        String Name = request.getParameter("Name");
        RequestDispatcher dispatcher = null;
//crea un objeto de tipo Clients vacío y lo llena con los datos obtenidos 
        Artist art = new Artist();
        art.setArtistId(artistId);
        art.setName(Name);
//Crea el cliente utilizando el objeto controlador JPA
        try{
        artistJPA.create(art);
        dispatcher = request.getRequestDispatcher("list");
        } catch (RuntimeException re){
            dispatcher = request.getRequestDispatcher("artist-form.jsp");
            request.setAttribute("loginError","Captura de error :"+re.getMessage());
            System.out.println("*******************");
            System.out.println("lance la excepción");
            System.out.println("*******************");
        } catch (PreexistingEntityException pre){
            dispatcher = request.getRequestDispatcher("artist-form.jsp");
            request.setAttribute("loginError","Captura de error :"+pre.getMessage());
            System.out.println("*******************");
            System.out.println("lance la excepción");
            System.out.println("*******************");
        }
//solicita al Servlet que muestre la página actualizada con la lista de clientes 
        dispatcher.forward(request, response);
    }
//Método para editar un cliente

    private void updateArtist(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
//toma los datos enviados por el formulario de clientes
        int artistId = Integer.parseInt(request.getParameter("artistId"));
        String Name = request.getParameter("Name");
//crea un objeto vacío y lo llena con los datos del cliente
        Artist art = new Artist();
        art.setArtistId(artistId);
        art.setName(Name);
        System.out.println("-----------------");
        System.out.println(art.getArtistId() + "----- y -----" +  art.getName());
        System.out.println("-----------------");
        try {
//Edita el cliente en la BD
            artistJPA.edit(art);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.sendRedirect("list");
    }

    //Elimina un cliente de la BD
    private void deleteArtist(HttpServletRequest request, HttpServletResponse response)
            throws IOException, IllegalOrphanException, ServletException {
        RequestDispatcher dispatcher = null;
//Recibe el ID del cliente que se espera eliminar de la BD
        int id = Integer.parseInt(request.getParameter("artistId"));
        try {
//Elimina el cliente con el id indicado
            dispatcher = request.getRequestDispatcher("list");
            artistJPA.destroy(id);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalOrphanException e){
            request.setAttribute("loginError","Captura de error :"+e.getMessage());
            System.out.println("*******************");
            System.out.println("lance la excepción");
            System.out.println("*******************");
        } catch (RuntimeException re){
            request.setAttribute("loginError","Captura de error :"+re.getMessage());
            System.out.println("*******************");
            System.out.println("lance la excepción base ");
            System.out.println("*******************");
        }
        dispatcher.forward(request, response);
        //response.sendRedirect("list");
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
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServletAppArtists.class.getName()).log(Level.SEVERE, null, ex);
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
