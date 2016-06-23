/**
 * UserSessionFactoryServiceLocator.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package security.com.geodis.portal.mau;

public class UserSessionFactoryServiceLocator extends org.apache.axis.client.Service
        implements security.com.geodis.portal.mau.UserSessionFactoryService {

    public UserSessionFactoryServiceLocator() {
    }

    public UserSessionFactoryServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public UserSessionFactoryServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
            throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for UserSessionFactory
    private java.lang.String UserSessionFactory_address = "http://192.168.184.10/mausw/services/UserSessionFactory";

    public java.lang.String getUserSessionFactoryAddress() {
        return UserSessionFactory_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String UserSessionFactoryWSDDServiceName = "UserSessionFactory";

    public java.lang.String getUserSessionFactoryWSDDServiceName() {
        return UserSessionFactoryWSDDServiceName;
    }

    public void setUserSessionFactoryWSDDServiceName(java.lang.String name) {
        UserSessionFactoryWSDDServiceName = name;
    }

    public security.com.geodis.portal.mau.UserSessionFactory getUserSessionFactory()
            throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(UserSessionFactory_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getUserSessionFactory(endpoint);
    }

    public security.com.geodis.portal.mau.UserSessionFactory getUserSessionFactory(java.net.URL portAddress)
            throws javax.xml.rpc.ServiceException {
        try {
            security.com.geodis.portal.mau.UserSessionFactorySoapBindingStub _stub = new security.com.geodis.portal
                    .mau.UserSessionFactorySoapBindingStub(
                    portAddress, this);
            _stub.setPortName(getUserSessionFactoryWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setUserSessionFactoryEndpointAddress(java.lang.String address) {
        UserSessionFactory_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (security.com.geodis.portal.mau.UserSessionFactory.class.isAssignableFrom(serviceEndpointInterface)) {
                security.com.geodis.portal.mau.UserSessionFactorySoapBindingStub _stub = new security.com.geodis
                        .portal.mau.UserSessionFactorySoapBindingStub(
                        new java.net.URL(UserSessionFactory_address), this);
                _stub.setPortName(getUserSessionFactoryWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (
                serviceEndpointInterface == null ?
                        "null" :
                        serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
            throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("UserSessionFactory".equals(inputPortName)) {
            return getUserSessionFactory();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://mau.portal.geodis.com", "UserSessionFactoryService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://mau.portal.geodis.com", "UserSessionFactory"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address)
            throws javax.xml.rpc.ServiceException {

        if ("UserSessionFactory".equals(portName)) {
            setUserSessionFactoryEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
            throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
