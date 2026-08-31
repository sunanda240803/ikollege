/**
 * JobCodeSettings.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class JobCodeSettings  implements java.io.Serializable {
    private int jobcode;

    private String jobcodeName;

    private int enable;

    public JobCodeSettings() {
    }

    public JobCodeSettings(
           int jobcode,
           String jobcodeName,
           int enable) {
           this.jobcode = jobcode;
           this.jobcodeName = jobcodeName;
           this.enable = enable;
    }


    /**
     * Gets the jobcode value for this JobCodeSettings.
     * 
     * @return jobcode
     */
    public int getJobcode() {
        return jobcode;
    }


    /**
     * Sets the jobcode value for this JobCodeSettings.
     * 
     * @param jobcode
     */
    public void setJobcode(int jobcode) {
        this.jobcode = jobcode;
    }


    /**
     * Gets the jobcodeName value for this JobCodeSettings.
     * 
     * @return jobcodeName
     */
    public String getJobcodeName() {
        return jobcodeName;
    }


    /**
     * Sets the jobcodeName value for this JobCodeSettings.
     * 
     * @param jobcodeName
     */
    public void setJobcodeName(String jobcodeName) {
        this.jobcodeName = jobcodeName;
    }


    /**
     * Gets the enable value for this JobCodeSettings.
     * 
     * @return enable
     */
    public int getEnable() {
        return enable;
    }


    /**
     * Sets the enable value for this JobCodeSettings.
     * 
     * @param enable
     */
    public void setEnable(int enable) {
        this.enable = enable;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof JobCodeSettings)) return false;
        JobCodeSettings other = (JobCodeSettings) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.jobcode == other.getJobcode() &&
            ((this.jobcodeName==null && other.getJobcodeName()==null) || 
             (this.jobcodeName!=null &&
              this.jobcodeName.equals(other.getJobcodeName()))) &&
            this.enable == other.getEnable();
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
        _hashCode += getJobcode();
        if (getJobcodeName() != null) {
            _hashCode += getJobcodeName().hashCode();
        }
        _hashCode += getEnable();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JobCodeSettings.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "JobCodeSettings"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobcode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobcodeName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobcodeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enable");
        elemField.setXmlName(new javax.xml.namespace.QName("", "enable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
