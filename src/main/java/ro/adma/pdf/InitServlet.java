package ro.adma.pdf;

import ro.appenigne.web.framework.annotation.UrlPattern;
import ro.appenigne.web.framework.utils.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@UrlPattern({"/init", "/_ah/start"})
public class InitServlet extends HttpServlet {

    public static final String[] initWkHtmlToPDFCmds = {
            //"dpkg -i wkhtmltox-0.12.1_linux-wheezy-amd64.deb"
            "dpkg -i wkhtmltox-0.12.2_linux-jessie-amd64.deb"
            /*"tar xf wkhtmltox-0.12.1-c22928d_linux-wheezy-amd64.tar.xz -C /",
            "cp /wkhtmltox-0.12.1-c22928d/bin/wkhtmltopdf /usr/local/bin",
            "cp /wkhtmltox-0.12.1-c22928d/bin/wkhtmltoimage /usr/local/bin"*/
    };
    public static final String[] initFirefoxCmds = {
            "wget --quiet https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb",
            "dpkg -i google-chrome-stable_current_amd64.deb",
            "apt-get --quiet --yes -f install",
            "dpkg -i google-chrome-stable_current_amd64.deb",
            "rm -rf google-chrome-stable_current_amd64.deb",

            "wget --quiet https://download-installer.cdn.mozilla.net/pub/firefox/releases/29.0.1/linux-x86_64/en-US/firefox-29.0.1.tar.bz2",
            "tar -jxvf firefox-29.0.1.tar.bz2 -C /opt",
            "ln -s /opt/firefox/firefox /usr/bin/firefox",
            "mkdir images",
            "cp firefox/local-settings.js /opt/firefox/defaults/pref/local-settings.js",
            "cp firefox/mozilla.cfg /opt/firefox/mozilla.cfg",


            "tar -xf flash/install_flash_player_11_linux.x86_64.tar.gz -C flash/",
            "mkdir /usr/lib/mozilla",
            "mkdir /usr/lib/mozilla/plugins",
            "cp flash/libflashplayer.so /usr/lib/mozilla/plugins",
            "cp -r -f fonts /usr/local/share/",
            "fc-cache -f"
            //install flash player


            /*"Xvfb :1 -screen 0 1920x1080x24 +extension RANDR -xinerama",
            "firefox --display=:1  -setDefaultBrowser -width 1920 -height 1080 http://google.com -private-window",
            "import -display :1 -type TrueColor -window root -crop 1920x1080+0+0 -quality 100 images/xxz.png",*/


/*
            "cp -f /home/vmagent/appengine-java-vmruntime/webapps/root/chrome/chromedriver_linux64.zip /srv/",
            "/usr/bin/unzip /srv/chromedriver_linux64.zip -d /usr/local/bin",
            "rm -rf /srv/chromedriver_linux64.zip",
            "cp -f /home/vmagent/appengine-java-vmruntime/webapps/root/chrome/xvfb_init /etc/init.d/xvfb",
            "chmod a+x /etc/init.d/xvfb",
            "cp -f /home/vmagent/appengine-java-vmruntime/webapps/root/chrome/xvfb-daemon-run /usr/bin/xvfb-daemon-run",
            "chmod a+x /usr/bin/xvfb-daemon-run",
            "cp -f /home/vmagent/appengine-java-vmruntime/webapps/root/chrome/google-chrome-launcher /usr/bin/google-chrome",
            "chmod a+x /usr/bin/google-chrome"*/


    };

    public static final String[] initChromeTests = {
            "xvfb-run -a -s \"1024x768x32\" google-chrome http://16.cloudvm.edu2bits.appspot.com/?merge1",
            "xvfb-daemon-run google-chrome http://16.cloudvm.edu2bits.appspot.com/"
    };

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     */
    protected static void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        for (String cmd : initWkHtmlToPDFCmds) {
            String respString = Exec.exec(cmd);
            out.println("<h2>" + cmd + "</h2><br />");
            out.println("<pre>" + respString + "</pre>");
        }
        execUpdateFirefox();
    }

    public static void execUpdateWKhtmlToPDF() {
        for (String cmd : initWkHtmlToPDFCmds) {
            Exec.exec(cmd);
        }
    }

    public static void execUpdateFirefox() {
        try {
            for (String cmd : initFirefoxCmds) {
                Exec.exec(cmd);
            }

        } catch (Exception e) {
            Log.s(e);
        } finally {
            Exec.exec("pkill Xvfb");
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