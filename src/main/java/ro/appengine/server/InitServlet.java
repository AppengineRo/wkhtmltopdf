package ro.appengine.server;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InitServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Runtime rt = Runtime.getRuntime();

        Process p;
        p = rt.exec("wget http://downloads.sourceforge.net/project/wkhtmltopdf/0.12.1-dev/wkhtmltox-0.12.1-f6f29b0_linux-wheezy-amd64.tar.xz");
        p = rt.exec("tar xf wkhtmltox-0.12.1-f6f29b0_linux-wheezy-amd64.tar.xz -C /");
        p = rt.exec("cp /wkhtmltox-0.12.1-f6f29b0/bin/wkhtmltopdf /usr/local/bin");

        p.getOutputStream().flush();
        p.getOutputStream().close();
        try {
            rewrite(p.getInputStream(), response.getOutputStream());
            response.getOutputStream().flush();
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
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException      if an I/O error occurs
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
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException if a servlet-specific error occurs
     * @throws java.io.IOException      if an I/O error occurs
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