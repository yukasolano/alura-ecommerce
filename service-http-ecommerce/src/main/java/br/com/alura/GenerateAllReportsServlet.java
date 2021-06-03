package br.com.alura;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispacher<String> batchDispacher = new KafkaDispacher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispacher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        try {
            batchDispacher.send("ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", "ECOMMERCE_USER_GENERATE_READING_REPORT",
                    new CorrelationId(GenerateAllReportsServlet.class.getSimpleName()), "ECOMMERCE_USER_GENERATE_READING_REPORT");

            System.out.println("Generate report to all users sent successfully.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated");
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
}
