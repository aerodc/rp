package servlets;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import utils.OAuthUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2CallBackServlet extends AbstractAuthorizationCodeCallbackServlet {

    /**
     *
     */
    private static final long serialVersionUID = -1081961409711078538L;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        resp.sendRedirect("/todo");
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        super.onError(req, resp, errorResponse);
    }

    @Override
    protected String getRedirectUri(HttpServletRequest arg0) throws ServletException, IOException {
        return OAuthUtil.getRedirectUri(arg0);
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

}
