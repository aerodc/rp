package security;

import org.springframework.stereotype.Service;
import security.com.geodis.portal.mau.MAUException;
import security.com.geodis.portal.mau.UserSession;

import javax.xml.rpc.ServiceException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

@Service
public class MAUAuth {
    public static boolean isMAUUser(String login, String password) {

        boolean result = false;
        security.com.geodis.portal.mau.UserSessionFactorySoapBindingStub binding;

        security.com.geodis.portal.mau.UserSessionFactoryServiceLocator locator = new security.com.geodis.portal.mau
                .UserSessionFactoryServiceLocator();
        URL url = null;

        ResourceBundle resourceBundle = ResourceBundle.getBundle("properties.wsrp");

        try {
            url = new URL(resourceBundle.getString("mau.ws.url"));
            binding = (security.com.geodis.portal.mau.UserSessionFactorySoapBindingStub) locator
                    .getUserSessionFactory(url);
            UserSession userSession = binding.createSession(login, password, resourceBundle.getString("mau.appli.name"),
                    InetAddress.getLocalHost().getHostAddress());
            if (userSession.getIdentity() != null) {
                result = true;
            } else {
                result = false;
            }
        } catch (MalformedURLException ex) {
            result = false;
        } catch (ServiceException e) {
            result = false;
        } catch (MAUException e) {
            result = false;
        } catch (RemoteException e) {
            result = false;
        } catch (UnknownHostException e) {
            result = false;
        }
        return result;
    }
}
