package security.com.geodis.portal.mau;

public class UserSessionFactoryProxy implements security.com.geodis.portal.mau.UserSessionFactory {
    private String _endpoint = null;
    private security.com.geodis.portal.mau.UserSessionFactory userSessionFactory = null;

    public UserSessionFactoryProxy() {
        _initUserSessionFactoryProxy();
    }

    public UserSessionFactoryProxy(String endpoint) {
        _endpoint = endpoint;
        _initUserSessionFactoryProxy();
    }

    private void _initUserSessionFactoryProxy() {
        try {
            userSessionFactory = (new security.com.geodis.portal.mau.UserSessionFactoryServiceLocator())
                    .getUserSessionFactory();
            if (userSessionFactory != null) {
                if (_endpoint != null) {
                    ((javax.xml.rpc.Stub) userSessionFactory)
                            ._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                } else {
                    _endpoint = (String) ((javax.xml.rpc.Stub) userSessionFactory)
                            ._getProperty("javax.xml.rpc.service.endpoint.address");
                }
            }

        } catch (javax.xml.rpc.ServiceException serviceException) {}
    }

    public String getEndpoint() {
        return _endpoint;
    }

    public void setEndpoint(String endpoint) {
        _endpoint = endpoint;
        if (userSessionFactory != null) {
            ((javax.xml.rpc.Stub) userSessionFactory)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        }

    }

    public security.com.geodis.portal.mau.UserSessionFactory getUserSessionFactory() {
        if (userSessionFactory == null) { _initUserSessionFactoryProxy(); }
        return userSessionFactory;
    }

    public security.com.geodis.portal.mau.UserSession createSession(java.lang.String login, java.lang.String password,
            java.lang.String application) throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException {
        if (userSessionFactory == null) { _initUserSessionFactoryProxy(); }
        return userSessionFactory.createSession(login, password, application);
    }

    public security.com.geodis.portal.mau.UserSession createSession(java.lang.String login, java.lang.String password,
            java.lang.String application, java.lang.String remoteIp)
            throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException {
        if (userSessionFactory == null) { _initUserSessionFactoryProxy(); }
        return userSessionFactory.createSession(login, password, application, remoteIp);
    }

    public security.com.geodis.portal.mau.UserSession continueSession(java.lang.String ssoKey,
            java.lang.String application) throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException {
        if (userSessionFactory == null) { _initUserSessionFactoryProxy(); }
        return userSessionFactory.continueSession(ssoKey, application);
    }

    public security.com.geodis.portal.mau.UserSession continueSession(java.lang.String ssoKey,
            java.lang.String application, java.lang.String remoteIp)
            throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException {
        if (userSessionFactory == null) { _initUserSessionFactoryProxy(); }
        return userSessionFactory.continueSession(ssoKey, application, remoteIp);
    }

}