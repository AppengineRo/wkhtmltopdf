package ro.adma.pdf;

import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.utils.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@UrlPattern("/render.pdf")
public class HtmlToPdf extends HttpServlet {

    /*static final String AUTH_CALLBACK_SERVLET_PATH = "/apps/oauth2callback";
    static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static GoogleClientSecrets clientSecrets = null;
    private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();*/

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException    if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            updateVersion();

            String orientation = getOrientation(request);
            String pageSize = getPageSize(request);
            String url = request.getParameter("url");
            String fileName = getFileName(request);
            String fileNameWithoutExtension = fileName.substring(0,fileName.lastIndexOf(".")).replaceAll("\\.","_");
            String zoom = getZoom(request);
            String screenSize = getScreenSize(request);
            String margin = getMargin(request);


            Process p;
            Runtime rt = Runtime.getRuntime();
            response.addHeader("Content-Disposition", MessageFormat.format("attachment; filename=\"{0}\"", fileName));
            if (url != null && !url.isEmpty()) {
                //hopefully this stops the program accessing local files.
                if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                    url = "http://" + url;
                }
                String command = MessageFormat.format("xvfb-run -s=\"{1}\" -a wkhtmltopdf --quiet --disable-local-file-access --zoom {2} --orientation {3} --page-size {4} {5} {0} -",
                        url, screenSize, zoom, orientation, pageSize, margin);
                Log.i(command);
                p = rt.exec(command);

            } else {
                String html = request.getParameter("html");
                if (html == null) {
                    response.sendError(404);
                    return;
                }
                String command = MessageFormat.format("xvfb-run -s=\"{1}\" -a wkhtmltopdf --quiet --disable-local-file-access --zoom {2} --orientation {3} --page-size {4} {5} {0} -",
                        "-", screenSize, zoom, orientation, pageSize, margin);
                Log.i(command);
                p = rt.exec(command);
                p.getOutputStream().write(html.getBytes("UTF-8"));
                p.getOutputStream().flush();
                p.getOutputStream().close();
            }
            try {


                //save file in cloud storage
                //create nice folders pdf/year/month/day/year-month-day-hh-min-sec-timestamp.pdf
                Date today = new Date();
                SimpleDateFormat folderSdf = new SimpleDateFormat("yyyy/MM/dd/");

                folderSdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                String filename = "pdf/"+folderSdf.format(today)+fileNameWithoutExtension+"-"+today.getTime()+".pdf";
                ByteArrayOutputStream pdfByteArrayBuffer = new ByteArrayOutputStream();
                rewrite(p.getInputStream(), pdfByteArrayBuffer);
                byte[] bytes = pdfByteArrayBuffer.toByteArray();
                GcsUtils.writeFile(GcsUtils.getGcsFileName(filename), bytes, "application/pdf");

                response.getOutputStream().write(bytes);
            } finally {
                p.getInputStream().close();
                response.getOutputStream().close();
            }
        } catch (Exception e) {
            Log.s(e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Helper functions for arguments">
    private void updateVersion() {
        String version = Exec.exec("wkhtmltopdf --version");
        if (!version.contains("0.12.1")) {
            InitServlet.execUpdateWKhtmlToPDF();
        }
    }

    private String getFileName(HttpServletRequest request) {
        String fileName = request.getParameter("filename");
        if (fileName == null || fileName.isEmpty()) {
            fileName = "generated";
        } else if (fileName.endsWith(".pdf")) {
            return fileName;
        }
        String format = request.getParameter("format");
        if (format == null || format.isEmpty()) {
            format = "pdf";
        }

        return fileName + "." + format;
    }

    private String getOrientation(HttpServletRequest request) {
        String orientation;
        if ("Landscape".equalsIgnoreCase(request.getParameter("orientation"))) {
            orientation = "Landscape";
        } else {
            orientation = "Portrait";
        }
        return orientation;
    }

    private String getPageSize(HttpServletRequest request) {
        String pageSize = "A4";
        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            if (pageSizeParam.matches("[a-zA-Z0-9]{2,9}")) {
                pageSize = pageSizeParam;
            }
        }
        return pageSize;
    }

    private String getMargin(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(" --margin-left ").append(getMargin(request, "marginLeft")).append("mm ");
        sb.append("--margin-top ").append(getMargin(request, "marginTop")).append("mm ");
        sb.append("--margin-right ").append(getMargin(request, "marginRight")).append("mm ");
        sb.append("--margin-bottom ").append(getMargin(request, "marginBottom")).append("mm ");
        return sb.toString();
    }

    private String getMargin(HttpServletRequest request, String side) {
        double margin = 10;
        try {
            margin = Double.parseDouble(request.getParameter(side));
        } catch (Exception ignored) {
        }
        return margin + "";
    }

    private String getScreenSize(HttpServletRequest request) {
        int screenSizeW = 1024;
        try {
            screenSizeW = Integer.parseInt(request.getParameter("screenSizeW"), 10);
        } catch (Exception ignored) {
        }
        int screenSizeH = 768;
        try {
            screenSizeH = Integer.parseInt(request.getParameter("screenSizeH"), 10);
        } catch (Exception ignored) {
        }
        int screenSizeC = 32;
        try {
            screenSizeC = Integer.parseInt(request.getParameter("screenSizeC"), 10);
        } catch (Exception ignored) {
        }
        return screenSizeW + "x" + screenSizeH + "x" + screenSizeC;
    }

    private String getZoom(HttpServletRequest request) {
        Double zoom = 1.0;
        try {
            zoom = Double.parseDouble(request.getParameter("zoom"));
        } catch (Exception ignored) {
        }
        return zoom + "";
    }

    // </editor-fold>
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
     * @throws javax.servlet.ServletException    if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
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
     * @throws javax.servlet.ServletException    if a servlet-specific error occurs
     * @throws java.io.IOException if an I/O error occurs
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

    /*@Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        Set<String> scopes = new HashSet<>();
        scopes.add("email");
        scopes.add("profile");

        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, getClientSecrets(), scopes)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .build();

    }
    private static GoogleClientSecrets getClientSecrets() throws IOException {
        if (clientSecrets == null) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(HtmlToPdf.class.getResourceAsStream("/client_secrets.json")));
            Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("Enter ")
                            && !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
                    "Download client_secrets.json file from "
                            + "https://code.google.com/apis/console/?api=admin#project:378356568566 into "
                            + "src/main/resources/client_secrets.json"
            );
        }
        return clientSecrets;
    }
    @Override
    protected String getRedirectUri(HttpServletRequest request) throws ServletException, IOException {
        return null;
    }*/
}