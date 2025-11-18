package doczilla.fileshare;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;

        Server server = new Server(port);
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        ServletHolder staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter("resourceBase", Main.class.getResource("/static").toExternalForm());
        staticHolder.setInitParameter("dirAllowed", "false");
        ctx.addServlet(staticHolder, "/");

        server.setHandler(ctx);
        server.start();
        server.join();
    }
}
