/**
 * UserSessionFactory.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package security.com.geodis.portal.mau;

public interface UserSessionFactory extends java.rmi.Remote {
    public security.com.geodis.portal.mau.UserSession createSession(java.lang.String login, java.lang.String password,
            java.lang.String application) throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException;

    public security.com.geodis.portal.mau.UserSession createSession(java.lang.String login, java.lang.String password,
            java.lang.String application, java.lang.String remoteIp)
            throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException;

    public security.com.geodis.portal.mau.UserSession continueSession(java.lang.String ssoKey,
            java.lang.String application) throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException;

    public security.com.geodis.portal.mau.UserSession continueSession(java.lang.String ssoKey,
            java.lang.String application, java.lang.String remoteIp)
            throws java.rmi.RemoteException, security.com.geodis.portal.mau.MAUException;
}
