/**
 * UserSession.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package security.com.geodis.portal.mau;

public class UserSession implements java.io.Serializable {
    private java.lang.String application;

    private java.lang.String currentLanguage;

    private security.com.geodis.portal.mau.Identity identity;

    private java.lang.String roles;

    private java.lang.String ssoKey;

    public UserSession() {
    }

    public UserSession(java.lang.String application, java.lang.String currentLanguage,
            security.com.geodis.portal.mau.Identity identity, java.lang.String roles, java.lang.String ssoKey) {
        this.application = application;
        this.currentLanguage = currentLanguage;
        this.identity = identity;
        this.roles = roles;
        this.ssoKey = ssoKey;
    }

    /**
     * Gets the application value for this UserSession.
     *
     * @return application
     */
    public java.lang.String getApplication() {
        return application;
    }

    /**
     * Sets the application value for this UserSession.
     *
     * @param application
     */
    public void setApplication(java.lang.String application) {
        this.application = application;
    }

    /**
     * Gets the currentLanguage value for this UserSession.
     *
     * @return currentLanguage
     */
    public java.lang.String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the currentLanguage value for this UserSession.
     *
     * @param currentLanguage
     */
    public void setCurrentLanguage(java.lang.String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }

    /**
     * Gets the identity value for this UserSession.
     *
     * @return identity
     */
    public security.com.geodis.portal.mau.Identity getIdentity() {
        return identity;
    }

    /**
     * Sets the identity value for this UserSession.
     *
     * @param identity
     */
    public void setIdentity(security.com.geodis.portal.mau.Identity identity) {
        this.identity = identity;
    }

    /**
     * Gets the roles value for this UserSession.
     *
     * @return roles
     */
    public java.lang.String getRoles() {
        return roles;
    }

    /**
     * Sets the roles value for this UserSession.
     *
     * @param roles
     */
    public void setRoles(java.lang.String roles) {
        this.roles = roles;
    }

    /**
     * Gets the ssoKey value for this UserSession.
     *
     * @return ssoKey
     */
    public java.lang.String getSsoKey() {
        return ssoKey;
    }

    /**
     * Sets the ssoKey value for this UserSession.
     *
     * @param ssoKey
     */
    public void setSsoKey(java.lang.String ssoKey) {
        this.ssoKey = ssoKey;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserSession)) { return false; }
        UserSession other = (UserSession) obj;
        if (obj == null) { return false; }
        if (this == obj) { return true; }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((this.application == null && other.getApplication() == null) || (this.application != null
                        && this.application.equals(other.getApplication()))) &&
                ((this.currentLanguage == null && other.getCurrentLanguage() == null) || (this.currentLanguage != null
                        && this.currentLanguage.equals(other.getCurrentLanguage()))) &&
                ((this.identity == null && other.getIdentity() == null) || (this.identity != null && this.identity
                        .equals(other.getIdentity()))) &&
                ((this.roles == null && other.getRoles() == null) || (this.roles != null && this.roles
                        .equals(other.getRoles()))) &&
                ((this.ssoKey == null && other.getSsoKey() == null) || (this.ssoKey != null && this.ssoKey
                        .equals(other.getSsoKey())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getApplication() != null) {
            _hashCode += getApplication().hashCode();
        }
        if (getCurrentLanguage() != null) {
            _hashCode += getCurrentLanguage().hashCode();
        }
        if (getIdentity() != null) {
            _hashCode += getIdentity().hashCode();
        }
        if (getRoles() != null) {
            _hashCode += getRoles().hashCode();
        }
        if (getSsoKey() != null) {
            _hashCode += getSsoKey().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            UserSession.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://mau.portal.geodis.com", "UserSession"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("application");
        elemField.setXmlName(new javax.xml.namespace.QName("", "application"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentLanguage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentLanguage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identity");
        elemField.setXmlName(new javax.xml.namespace.QName("", "identity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://mau.portal.geodis.com", "Identity"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "roles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ssoKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ssoKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
            java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
            java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
