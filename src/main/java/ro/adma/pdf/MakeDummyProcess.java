package ro.adma.pdf;

import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.utils.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

@UrlPattern("/render.makeDummyProcess")
public class MakeDummyProcess extends HttpServlet {

    static void waitForDummyProcess(String command) throws InterruptedException {
        int waitTime = 100;
        int waited = 0;
        while (Exec.exec("pidof " + command).trim().isEmpty()) {
            Thread.sleep(waitTime);
            waited += waitTime;
            if (waited == 60000) {
                //wait 60 sec then kill this, its not starting
                throw new InterruptedException("Am asteptat prea mult dupa " + command);
            }
        }
    }

    static void waitForEndOfDummyProcess(String command) throws InterruptedException {
        while (!Exec.exec("pidof " + command).trim().isEmpty()) {
            Thread.sleep(100);
        }
    }

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
        try {
            Runtime rt = Runtime.getRuntime();
            Process dummyProcess = rt.exec("su");
        } catch (Exception e) {
            Log.s(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Checks if the version is up to date, if not, it updates it
     */
    private void updateVersion() {
        String version = Exec.exec("firefox --version");
        if (version.contains("Cannot run program")) {
            InitServlet.execUpdateFirefox();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Helper functions for arguments">
    private String getUrl(HttpServletRequest request) throws FileNotFoundException, UnsupportedEncodingException {
        String url = request.getParameter("url");
        String html = request.getParameter("html");
        if (url != null && !url.isEmpty()) {
            //hopefully this stops the program accessing local files.
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "http://" + url;
            }
            return url;
        } else if (html != null && !html.isEmpty()) {
            writeFile(html, request);
            url = getNakedUrl(request) + "/download?filename=html.html&timestamp=" + (new Date().getTime());
            return url;
        }
        return null;
    }

    private void writeFile(String html, HttpServletRequest request) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("images/html.html", "UTF-8");
        writer.print(html);
        writer.print("<script type='text/javascript' src='" + getNakedUrl(request) + "/js/onload.js'></script>");
        writer.close();
    }

    public static String getNakedUrl(HttpServletRequest req) {
        return req.getRequestURL().substring(0, req.getRequestURL().indexOf("/", 8));
    }

    private String getFormat(HttpServletRequest request) {
        String format = request.getParameter("format");
        if (format == null || format.isEmpty()) {
            format = "png";
        }
        return format;
    }

    private String getFileName(HttpServletRequest request, String format) {
        String fileName = request.getParameter("filename");
        if (fileName == null || fileName.isEmpty()) {
            fileName = "generated";
        }
        fileName = fileName + "." + format;
        return fileName;
    }

    private String getSmartWidth(HttpServletRequest request) {
        return ("no".equals(request.getParameter("smartWidth"))) ? "--disable-smart-width" : "";
    }

    private String getWidth(HttpServletRequest request) {
        int width = 1920;
        try {
            width = Integer.parseInt(request.getParameter("width"), 10);
        } catch (Exception ignored) {
        }
        return width + "";
    }

    private String getHeight(HttpServletRequest request) {
        int height = 1080;
        try {
            height = Integer.parseInt(request.getParameter("height"), 10);
        } catch (Exception ignored) {
        }
        return height + "";
    }

    private String getQuality(HttpServletRequest request) {
        int quality = 100;
        try {
            int qualityParam = Integer.parseInt(request.getParameter("quality"), 10);
            if (qualityParam >= 0 && qualityParam <= 100) {
                quality = qualityParam;
            }
        } catch (Exception ignored) {
        }
        return quality + "";
    }

    private String getZoom(HttpServletRequest request) {
        Double zoom = 1.0;
        try {
            zoom = Double.parseDouble(request.getParameter("zoom"));
        } catch (Exception ignored) {
        }
        return zoom + "";
    }

    private String getCrop(HttpServletRequest request) {
        String cropH = getCrop(request, "cropH");
        String cropW = getCrop(request, "cropW");
        String cropX = getCrop(request, "cropX");
        String cropY = getCrop(request, "cropY");
        if (!"0".equals(cropW) && !"0".equals(cropH)) {
            StringBuilder sb = new StringBuilder();
            sb.append(" --crop-h ").append(cropH);
            sb.append(" --crop-w ").append(cropW);
            sb.append(" --crop-x ").append(cropX);
            sb.append(" --crop-y ").append(cropY).append(" ");
            return sb.toString();
        }
        return "";
    }

    private String getCrop(HttpServletRequest request, String side) {
        int margin = 0;
        try {
            margin = Integer.parseInt(request.getParameter(side), 10);
        } catch (Exception ignored) {
        }
        return margin + "";
    }
    // </editor-fold>

    static void rewrite(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[256];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generic HttpServlet methods.">

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
        processRequest(request, response);
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