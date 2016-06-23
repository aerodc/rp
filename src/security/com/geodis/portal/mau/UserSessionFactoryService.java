/**
 * UserSessionFactoryService.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package security.com.geodis.portal.mau;

public interface UserSessionFactoryService extends javax.xml.rpc.Service {
    public java.lang.String getUserSessionFactoryAddress();

    public security.com.geodis.portal.mau.UserSessionFactory getUserSessionFactory()
            throws javax.xml.rpc.ServiceException;

    public security.com.geodis.portal.mau.UserSessionFactory getUserSessionFactory(java.net.URL portAddress)
            throws javax.xml.rpc.ServiceException;
}
