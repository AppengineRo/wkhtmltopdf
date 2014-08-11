package ro.adma.pdf;

import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.request.TrimRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@UrlPattern("/download")
public class Download extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException            if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getParameter("filename");
        getFile(request, response, filename);

    }

    public static void getFile(HttpServletRequest request, HttpServletResponse response, String filename) throws IOException {
        String contentDisposition = request.getParameter("contentDisposition");
        if (filename.toLowerCase().endsWith(".html")) {
            response.setContentType("text/html; charset=UTF-8");
            contentDisposition = "inline";
        } else if (filename.toLowerCase().endsWith(".png")) {
            response.setContentType("image/png; charset=UTF-8");
        } else if (filename.toLowerCase().endsWith(".jpg")) {
            response.setContentType("image/jpeg; charset=UTF-8");
        } else if (filename.toLowerCase().endsWith(".bmp")) {
            response.setContentType("image/bmp; charset=UTF-8");
        } else if (filename.toLowerCase().endsWith(".pdf")) {
            response.setContentType("application/pdf; charset=UTF-8");
        } else if (filename.toLowerCase().endsWith(".js")) {
            response.setContentType("text/javascript; charset=UTF-8");
            contentDisposition = "inline";
        } else if (filename.toLowerCase().endsWith(".css")) {
            response.setContentType("text/css; charset=UTF-8");
            contentDisposition = "inline";
        } else if (filename.toLowerCase().endsWith(".svg")) {
            response.setContentType("image/svg+xml; charset=UTF-8");
        } else {
            response.setContentType("text/plain; charset=UTF-8");
        }

        if ("inline".equalsIgnoreCase(contentDisposition)) {
            response.setHeader("Content-Disposition", "inline; filename=" + filename);
        } else {
            response.setHeader("Content-Disposition", "download; filename=" + filename);
        }


        InputStream is = new FileInputStream(new File("images/" + filename));
        OutputStream oos = response.getOutputStream();

        byte[] buf = new byte[8192];

        int c = 0;

        while ((c = is.read(buf, 0, buf.length)) > 0) {
            oos.write(buf, 0, c);
            oos.flush();
        }
        oos.close();
        is.close();
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods.">

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException            if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(new TrimRequest(request), response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException            if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(new TrimRequest(request), response);
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