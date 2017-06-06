/*
 * Copyright (c) 2017, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 03-06-2016
 */
public class HttpServer {

    public interface HttpGetHandler extends Function<HttpServletRequest, String> {
        String apply(HttpServletRequest req);
    }

    private final Server server;
    private final ServletContextHandler container;

    public HttpServer(int port) {
        server = new Server(port);        // Http Server no port
        container = new ServletContextHandler(); // Contentor de Servlets
        server.setHandler(container);
    }

    public HttpServer addServletHolder(String path, ServletHolder holder) {
        container.addServlet(holder, path);
        return this;
    }

    public HttpServer addHandler(String path, HttpGetHandler handler) {
        /*
         * Associação entre Path <-> Servlet
         */
        container.addServlet(new ServletHolder(new HtmlRenderServlet(handler)), path);
        return this;
    }

    public void run() throws Exception {
        server.start();
        server.join();
    }

    static class HtmlRenderServlet extends HttpServlet {
        private final HttpGetHandler handler;

        public HtmlRenderServlet(HttpGetHandler handler) {
            this.handler = handler;
        }

        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, IOException {
            Charset utf8 = Charset.forName("utf-8");
            resp.setContentType(String.format("text/html; charset=%s", utf8.name()));

            String respBody = handler.apply(req);

            byte[] respBodyBytes = respBody.getBytes(utf8);
            resp.setStatus(200);
            resp.setContentLength(respBodyBytes.length);
            OutputStream os = resp.getOutputStream();
            os.write(respBodyBytes);
            os.close();
        }
    }
}