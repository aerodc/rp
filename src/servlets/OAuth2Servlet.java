package servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import utils.OAuthUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2Servlet extends AbstractAuthorizationCodeServlet {

    /**
     *
     */
    private static final long serialVersionUID = -9121179696413850714L;

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return OAuthUtil.getRedirectUri(req);
    }

    @Override
    protected String getUserId(HttpServletRequest arg0) throws ServletException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return OAuthUtil.newFlow();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // to do redirect to html
        System.out.println("server OAuth2Servlet: catch the req:" + req.toString() + " /resp: " + resp.toString());
    }

}
