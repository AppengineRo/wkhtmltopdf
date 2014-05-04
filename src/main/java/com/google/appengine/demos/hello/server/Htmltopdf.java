package com.google.appengine.demos.hello.server;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author student
 */
public class Htmltopdf extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Runtime rt = Runtime.getRuntime();
        String url = request.getParameter("url");
        String outputFileName = request.getParameter("filename");
        Double zoom = 1.0;
        try{
            zoom = Double.parseDouble(request.getParameter("zoom"));
        }catch (Exception ex){}

        Process p;

        if (outputFileName != null) {
            response.addHeader("Content-Disposition", "attachment; filename=\""+
                    outputFileName +"\"");
        }
        if(url != null && !url.isEmpty()){
            //hopefully this stops the program accessing local files.
            if(! (url.startsWith("http://") || url.startsWith("https://"))  ){
                url = "http://" + url;
            }
            p = rt.exec("xvfb-run -a wkhtmltopdf --disallow-local-file-access --zoom " + zoom + " "+ url +" -");

        }else{
            String html = request.getParameter("html");
            if(html == null){
                response.sendError(404);
                return;
            }
            p = rt.exec("xvfb-run -a wkhtmltopdf --disallow-local-file-access --zoom " + zoom + " - -");
            p.getOutputStream().write(html.getBytes("UTF-8"));
            p.getOutputStream().flush();
            p.getOutputStream().close();
        }
        //IOUtils.copy(p.getErrorStream(), System.out);
        //p.getErrorStream().close();
        try {
            rewrite(p.getInputStream(), response.getOutputStream());
        } finally {
            p.getInputStream().close();
            response.getOutputStream().close();
        }
    }
    static void rewrite(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[256];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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