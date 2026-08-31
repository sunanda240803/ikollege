/**
 * Keystorage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Keystorage  implements java.io.Serializable {
    private int keytype;

    private String keystring;

    private String keyid;

    private boolean keyenabled;

    public Keystorage() {
    }

    public Keystorage(
           int keytype,
           String keystring,
           String keyid,
           boolean keyenabled) {
           this.keytype = keytype;
           this.keystring = keystring;
           this.keyid = keyid;
           this.keyenabled = keyenabled;
    }


    /**
     * Gets the keytype value for this Keystorage.
     * 
     * @return keytype
     */
    public int getKeytype() {
        return keytype;
    }


    /**
     * Sets the keytype value for this Keystorage.
     * 
     * @param keytype
     */
    public void setKeytype(int keytype) {
        this.keytype = keytype;
    }


    /**
     * Gets the keystring value for this Keystorage.
     * 
     * @return keystring
     */
    public String getKeystring() {
        return keystring;
    }


    /**
     * Sets the keystring value for this Keystorage.
     * 
     * @param keystring
     */
    public void setKeystring(String keystring) {
        this.keystring = keystring;
    }


    /**
     * Gets the keyid value for this Keystorage.
     * 
     * @return keyid
     */
    public String getKeyid() {
        return keyid;
    }


    /**
     * Sets the keyid value for this Keystorage.
     * 
     * @param keyid
     */
    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }


    /**
     * Gets the keyenabled value for this Keystorage.
     * 
     * @return keyenabled
     */
    public boolean isKeyenabled() {
        return keyenabled;
    }


    /**
     * Sets the keyenabled value for this Keystorage.
     * 
     * @param keyenabled
     */
    public void setKeyenabled(boolean keyenabled) {
        this.keyenabled = keyenabled;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Keystorage)) return false;
        Keystorage other = (Keystorage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.keytype == other.getKeytype() &&
            ((this.keystring==null && other.getKeystring()==null) || 
             (this.keystring!=null &&
              this.keystring.equals(other.getKeystring()))) &&
            ((this.keyid==null && other.getKeyid()==null) || 
             (this.keyid!=null &&
              this.keyid.equals(other.getKeyid()))) &&
            this.keyenabled == other.isKeyenabled();
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
        _hashCode += getKeytype();
        if (getKeystring() != null) {
            _hashCode += getKeystring().hashCode();
        }
        if (getKeyid() != null) {
            _hashCode += getKeyid().hashCode();
        }
        _hashCode += (isKeyenabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Keystorage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "keystorage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keytype");
        elemField.setXmlName(new javax.xml.namespace.QName("", "keytype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keystring");
        elemField.setXmlName(new javax.xml.namespace.QName("", "keystring"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keyid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "keyid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keyenabled");
        elemField.setXmlName(new javax.xml.namespace.QName("", "keyenabled"));
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
