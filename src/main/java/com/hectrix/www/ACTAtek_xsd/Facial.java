/**
 * Facial.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Facial  implements java.io.Serializable {
    private int facialTemplateType;

    private byte[] facialTemplate;

    private byte[] facialPhoto;

    public Facial() {
    }

    public Facial(
           int facialTemplateType,
           byte[] facialTemplate,
           byte[] facialPhoto) {
           this.facialTemplateType = facialTemplateType;
           this.facialTemplate = facialTemplate;
           this.facialPhoto = facialPhoto;
    }


    /**
     * Gets the facialTemplateType value for this Facial.
     * 
     * @return facialTemplateType
     */
    public int getFacialTemplateType() {
        return facialTemplateType;
    }


    /**
     * Sets the facialTemplateType value for this Facial.
     * 
     * @param facialTemplateType
     */
    public void setFacialTemplateType(int facialTemplateType) {
        this.facialTemplateType = facialTemplateType;
    }


    /**
     * Gets the facialTemplate value for this Facial.
     * 
     * @return facialTemplate
     */
    public byte[] getFacialTemplate() {
        return facialTemplate;
    }


    /**
     * Sets the facialTemplate value for this Facial.
     * 
     * @param facialTemplate
     */
    public void setFacialTemplate(byte[] facialTemplate) {
        this.facialTemplate = facialTemplate;
    }


    /**
     * Gets the facialPhoto value for this Facial.
     * 
     * @return facialPhoto
     */
    public byte[] getFacialPhoto() {
        return facialPhoto;
    }


    /**
     * Sets the facialPhoto value for this Facial.
     * 
     * @param facialPhoto
     */
    public void setFacialPhoto(byte[] facialPhoto) {
        this.facialPhoto = facialPhoto;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Facial)) return false;
        Facial other = (Facial) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.facialTemplateType == other.getFacialTemplateType() &&
            ((this.facialTemplate==null && other.getFacialTemplate()==null) || 
             (this.facialTemplate!=null &&
              java.util.Arrays.equals(this.facialTemplate, other.getFacialTemplate()))) &&
            ((this.facialPhoto==null && other.getFacialPhoto()==null) || 
             (this.facialPhoto!=null &&
              java.util.Arrays.equals(this.facialPhoto, other.getFacialPhoto())));
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
        _hashCode += getFacialTemplateType();
        if (getFacialTemplate() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFacialTemplate());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getFacialTemplate(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFacialPhoto() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFacialPhoto());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getFacialPhoto(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Facial.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Facial"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facialTemplateType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FacialTemplateType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facialTemplate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FacialTemplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facialPhoto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FacialPhoto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
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
