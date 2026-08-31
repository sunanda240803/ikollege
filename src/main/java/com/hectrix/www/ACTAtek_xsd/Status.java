/**
 * Status.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Status  implements java.io.Serializable {
    private boolean active;

    private boolean autoMatch;

    private boolean smartCard;

    private boolean fingerprint;

    private boolean password;

    private boolean facial;

    private boolean facialAutoMatch;

    public Status() {
    }

    public Status(
           boolean active,
           boolean autoMatch,
           boolean smartCard,
           boolean fingerprint,
           boolean password,
           boolean facial,
           boolean facialAutoMatch) {
           this.active = active;
           this.autoMatch = autoMatch;
           this.smartCard = smartCard;
           this.fingerprint = fingerprint;
           this.password = password;
           this.facial = facial;
           this.facialAutoMatch = facialAutoMatch;
    }


    /**
     * Gets the active value for this Status.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this Status.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the autoMatch value for this Status.
     * 
     * @return autoMatch
     */
    public boolean isAutoMatch() {
        return autoMatch;
    }


    /**
     * Sets the autoMatch value for this Status.
     * 
     * @param autoMatch
     */
    public void setAutoMatch(boolean autoMatch) {
        this.autoMatch = autoMatch;
    }


    /**
     * Gets the smartCard value for this Status.
     * 
     * @return smartCard
     */
    public boolean isSmartCard() {
        return smartCard;
    }


    /**
     * Sets the smartCard value for this Status.
     * 
     * @param smartCard
     */
    public void setSmartCard(boolean smartCard) {
        this.smartCard = smartCard;
    }


    /**
     * Gets the fingerprint value for this Status.
     * 
     * @return fingerprint
     */
    public boolean isFingerprint() {
        return fingerprint;
    }


    /**
     * Sets the fingerprint value for this Status.
     * 
     * @param fingerprint
     */
    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }


    /**
     * Gets the password value for this Status.
     * 
     * @return password
     */
    public boolean isPassword() {
        return password;
    }


    /**
     * Sets the password value for this Status.
     * 
     * @param password
     */
    public void setPassword(boolean password) {
        this.password = password;
    }


    /**
     * Gets the facial value for this Status.
     * 
     * @return facial
     */
    public boolean isFacial() {
        return facial;
    }


    /**
     * Sets the facial value for this Status.
     * 
     * @param facial
     */
    public void setFacial(boolean facial) {
        this.facial = facial;
    }


    /**
     * Gets the facialAutoMatch value for this Status.
     * 
     * @return facialAutoMatch
     */
    public boolean isFacialAutoMatch() {
        return facialAutoMatch;
    }


    /**
     * Sets the facialAutoMatch value for this Status.
     * 
     * @param facialAutoMatch
     */
    public void setFacialAutoMatch(boolean facialAutoMatch) {
        this.facialAutoMatch = facialAutoMatch;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Status)) return false;
        Status other = (Status) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.active == other.isActive() &&
            this.autoMatch == other.isAutoMatch() &&
            this.smartCard == other.isSmartCard() &&
            this.fingerprint == other.isFingerprint() &&
            this.password == other.isPassword() &&
            this.facial == other.isFacial() &&
            this.facialAutoMatch == other.isFacialAutoMatch();
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
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isAutoMatch() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isSmartCard() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isFingerprint() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isPassword() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isFacial() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isFacialAutoMatch() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Status.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Status"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("autoMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("", "autoMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("smartCard");
        elemField.setXmlName(new javax.xml.namespace.QName("", "smartCard"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fingerprint");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fingerprint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facial");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facialAutoMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facialAutoMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
