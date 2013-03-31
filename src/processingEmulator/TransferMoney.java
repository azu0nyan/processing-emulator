package processingEmulator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;



public class TransferMoney extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int from = Integer.valueOf(request.getParameter("from"));
        int to = Integer.valueOf(request.getParameter("to"));
        double money = Double.valueOf(request.getParameter("money"));
        if(!TransferMoneyActionCreator.createAndWaitExecution(from, to, money)){
            response.sendRedirect("error.jsp");
            return;
        }
        response.sendRedirect(".");
    }
}
