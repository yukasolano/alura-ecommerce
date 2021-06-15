package br.com.alura;

import br.com.alura.dispacher.KafkaDispacher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispacher<Order> orderDispacher = new KafkaDispacher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispacher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        try {

            //Colocar o mínimo de código possivel, pois dimuni a chance de erro
            // e aumenta chance de recuperar, so mandar msg novamente
            String orderId = req.getParameter("uuid");
            BigDecimal value = new BigDecimal(req.getParameter("amount"));
            String email = req.getParameter("email");
            Order order = new Order(orderId, value, email);

            OrderDatabase orderDatabase = new OrderDatabase();
            if (orderDatabase.saveNew(order)) {
                //mesmas compras do mesmo usuario sao processadas sequenciamente
                orderDispacher.send("ECOMMERCE_NEW_ORDER", email,
                        new CorrelationId(NewOrderServlet.class.getSimpleName()), order);

                System.out.println("New order sent successfully.");

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("New order sent");
            } else {
                System.out.println("Old order sent.");

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("Old order sent");
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
}
