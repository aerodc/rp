/**
 * Identity.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package security.com.geodis.portal.mau;

public class Identity implements java.io.Serializable {
    private java.lang.String businessUnit;

    private java.lang.String email;

    private java.lang.String firstName;

    private java.lang.String function;

    private java.lang.Long idPers;

    private java.lang.String language;

    private java.lang.String lastName;

    private java.lang.String login;

    private java.lang.String password;

    private java.lang.String phone;

    public Identity() {
    }

    public Identity(java.lang.String businessUnit, java.lang.String email, java.lang.String firstName,
            java.lang.String function, java.lang.Long idPers, java.lang.String language, java.lang.String lastName,
            java.lang.String login, java.lang.String password, java.lang.String phone) {
        this.businessUnit = businessUnit;
        this.email = email;
        this.firstName = firstName;
        this.function = function;
        this.idPers = idPers;
        this.language = language;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.phone = phone;
    }

    /**
     * Gets the businessUnit value for this Identity.
     *
     * @return businessUnit
     */
    public java.lang.String getBusinessUnit() {
        return businessUnit;
    }

    /**
     * Sets the businessUnit value for this Identity.
     *
     * @param businessUnit
     */
    public void setBusinessUnit(java.lang.String businessUnit) {
        this.businessUnit = businessUnit;
    }

    /**
     * Gets the email value for this Identity.
     *
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }

    /**
     * Sets the email value for this Identity.
     *
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    /**
     * Gets the firstName value for this Identity.
     *
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName value for this Identity.
     *
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the function value for this Identity.
     *
     * @return function
     */
    public java.lang.String getFunction() {
        return function;
    }

    /**
     * Sets the function value for this Identity.
     *
     * @param function
     */
    public void setFunction(java.lang.String function) {
        this.function = function;
    }

    /**
     * Gets the idPers value for this Identity.
     *
     * @return idPers
     */
    public java.lang.Long getIdPers() {
        return idPers;
    }

    /**
     * Sets the idPers value for this Identity.
     *
     * @param idPers
     */
    public void setIdPers(java.lang.Long idPers) {
        this.idPers = idPers;
    }

    /**
     * Gets the language value for this Identity.
     *
     * @return language
     */
    public java.lang.String getLanguage() {
        return language;
    }

    /**
     * Sets the language value for this Identity.
     *
     * @param language
     */
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }

    /**
     * Gets the lastName value for this Identity.
     *
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName value for this Identity.
     *
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the login value for this Identity.
     *
     * @return login
     */
    public java.lang.String getLogin() {
        return login;
    }

    /**
     * Sets the login value for this Identity.
     *
     * @param login
     */
    public void setLogin(java.lang.String login) {
        this.login = login;
    }

    /**
     * Gets the password value for this Identity.
     *
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }

    /**
     * Sets the password value for this Identity.
     *
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }

    /**
     * Gets the phone value for this Identity.
     *
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }

    /**
     * Sets the phone value for this Identity.
     *
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Identity)) { return false; }
        Identity other = (Identity) obj;
        if (obj == null) { return false; }
        if (this == obj) { return true; }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                ((this.businessUnit == null && other.getBusinessUnit() == null) || (this.businessUnit != null
                        && this.businessUnit.equals(other.getBusinessUnit()))) &&
                ((this.email == null && other.getEmail() == null) || (this.email != null && this.email
                        .equals(other.getEmail()))) &&
                ((this.firstName == null && other.getFirstName() == null) || (this.firstName != null && this.firstName
                        .equals(other.getFirstName()))) &&
                ((this.function == null && other.getFunction() == null) || (this.function != null && this.function
                        .equals(other.getFunction()))) &&
                ((this.idPers == null && other.getIdPers() == null) || (this.idPers != null && this.idPers
                        .equals(other.getIdPers()))) &&
                ((this.language == null && other.getLanguage() == null) || (this.language != null && this.language
                        .equals(other.getLanguage()))) &&
                ((this.lastName == null && other.getLastName() == null) || (this.lastName != null && this.lastName
                        .equals(other.getLastName()))) &&
                ((this.login == null && other.getLogin() == null) || (this.login != null && this.login
                        .equals(other.getLogin()))) &&
                ((this.password == null && other.getPassword() == null) || (this.password != null && this.password
                        .equals(other.getPassword()))) &&
                ((this.phone == null && other.getPhone() == null) || (this.phone != null && this.phone
                        .equals(other.getPhone())));
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
        if (getBusinessUnit() != null) {
            _hashCode += getBusinessUnit().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getFunction() != null) {
            _hashCode += getFunction().hashCode();
        }
        if (getIdPers() != null) {
            _hashCode += getIdPers().hashCode();
        }
        if (getLanguage() != null) {
            _hashCode += getLanguage().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getLogin() != null) {
            _hashCode += getLogin().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            Identity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://mau.portal.geodis.com", "Identity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("businessUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("", "businessUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("function");
        elemField.setXmlName(new javax.xml.namespace.QName("", "function"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("idPers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "idPers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("language");
        elemField.setXmlName(new javax.xml.namespace.QName("", "language"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("login");
        elemField.setXmlName(new javax.xml.namespace.QName("", "login"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phone"));
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
