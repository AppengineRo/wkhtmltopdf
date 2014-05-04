/*
 * Copyright (c) 2013 Asociatia AppEngine . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package ro.appengine.server;

import ro.appengine.request.TrimRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


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
        }catch (Exception ignored){}

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
            p = rt.exec("xvfb-run -a wkhtmltopdf --quiet --disable-local-file-access --zoom " + zoom + " "+ url +" -");

        }else{
            String html = request.getParameter("html");
            if(html == null){
                response.sendError(404);
                return;
            }
            p = rt.exec("xvfb-run -a wkhtmltopdf --quiet --disable-local-file-access --zoom " + zoom + " - -");
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
        processRequest(new TrimRequest(request), response);
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