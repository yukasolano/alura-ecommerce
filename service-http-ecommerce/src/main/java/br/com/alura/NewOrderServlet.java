package br.com.alura;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispacher<Order> orderDispacher = new KafkaDispacher<>();
    private final KafkaDispacher<Email> emailDispacher = new KafkaDispacher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispacher.close();
        emailDispacher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        try {

            //COlocar o mínimo de código possivel, pois dimuni a chance de erro
            // e aumenta chance de recuperar, so mandar msg novamente
            String orderId = UUID.randomUUID().toString();
            BigDecimal value = new BigDecimal(req.getParameter("amount"));
            String email = req.getParameter("email");
            Order order = new Order(orderId, value, email);

            //mesmas compras do mesmo usuario sao processadas sequenciamente
            orderDispacher.send("ECOMMERCE_NEW_ORDER", email, order);

            Email emailCode = new Email("New order", "Thank you for your order! We are processing your order!");
            emailDispacher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            System.out.println("New order sent successfully.");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent");
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
}
