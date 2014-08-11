package ro.adma.pdf;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.utils.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

@UrlPattern("/render.firefox")
public class HtmlToFirefox extends HttpServlet {

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
            updateVersion();

            String url = getUrl(request);
            String format = getFormat(request);
            String fileName = getFileName(request, format);
            String width = getWidth(request);
            String height = getHeight(request);
            String quality = getQuality(request);
            String crop = getCrop(request);

            Process p;
            Runtime rt = Runtime.getRuntime();
            String xvfbCmd = MessageFormat.format("Xvfb :1 -screen 0 {0}x{1}x24", width, height);
            String firefoxCmd = MessageFormat.format("firefox --display=:1 -width {0} -height {1} {2}", width, height, url);
            String printscreenCmd = MessageFormat.format("import -display :1 -type TrueColor -window root {0} -quality {1} images/{2}", crop, quality, fileName);

            MakeDummyProcess.waitForEndOfDummyProcess("Xvfb");
            try {
                Process xvfbProcess = rt.exec(xvfbCmd);
                MakeDummyProcess.waitForDummyProcess("Xvfb");
                Process firefoxProcess = rt.exec(firefoxCmd);
                MakeDummyProcess.waitForDummyProcess("su");

                Exec.exec(printscreenCmd);

                Download.getFile(request, response, fileName);
            } finally {
                Exec.exec("pkill su");
                Exec.exec("pkill Xvfb");
                Exec.exec("pkill firefox");
            }
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
    private String getUrl(HttpServletRequest request) throws IOException {
        String urlString = request.getParameter("url");
        String html = request.getParameter("html");
        if (urlString != null && !urlString.isEmpty()) {
            //hopefully this stops the program accessing local files.
            if (!(urlString.startsWith("http://") || urlString.startsWith("https://"))) {
                urlString = "http://" + urlString;
            }
            URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
            URL url = new URL(urlString);
            HTTPResponse response = fetcher.fetch(url);
            if (response != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : response.getContent()) {
                    stringBuilder.append((char) b);
                }
                String urlFetchedHtml = stringBuilder.toString();
                //add the base tag in order to import the correct CSS
                Document doc = Jsoup.parse(urlFetchedHtml);
                Element content = doc.html(urlFetchedHtml);
                Elements headList = content.getElementsByTag("head");
                Element head;
                Elements baseTags = content.getElementsByTag("base");
                if (baseTags.size() == 0) {
                    if (headList.size() == 0) {
                        head = doc.prepend("head");
                    } else {
                        head = headList.get(0);
                    }
                    Element base = head.prependElement("base");
                    base.attr("href", urlString);
                }
                writeFile(doc.outerHtml(), request);
                urlString = getNakedUrl(request) + "/download?filename=html.html&timestamp=" + (new Date().getTime());
                return urlString;
            }
        } else if (html != null && !html.isEmpty()) {
            writeFile(html, request);
            urlString = getNakedUrl(request) + "/download?filename=html.html&timestamp=" + (new Date().getTime());
            return urlString;
        }
        return null;
    }

    private void writeFile(String html, HttpServletRequest request) throws FileNotFoundException, UnsupportedEncodingException {
        writeFile("images/html.html", html + "<script type='text/javascript' src='" + getNakedUrl(request) + "/js/onload.js'></script>");
    }

    public static void writeFile(String fileName, String html) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.print(html);
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
        if (!fileName.endsWith("." + format)) {
            fileName = fileName + "." + format;
        }
        return fileName;
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
            height += 71;//mozilla firefox header height
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

    private String getCrop(HttpServletRequest request) {
        //{0}x{1}+0+71
        //width x height + left + top

        String cropH = getCrop(request, "cropH");
        String cropW = getCrop(request, "cropW");
        String cropX = getCrop(request, "cropX");
        String cropY = getCrop(request, "cropY");
        cropY = (Integer.parseInt(cropY, 10) + 71) + "";

        if ("0".equals(cropW)) {
            cropW = getWidth(request);
        }
        if ("0".equals(cropH)) {
            cropH = getHeight(request);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" -crop ").append(cropW);
        sb.append("x").append(cropH);
        sb.append("+").append(cropX);
        sb.append("+").append(cropY).append(" ");
        return sb.toString();
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