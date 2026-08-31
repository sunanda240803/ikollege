/**
 * JobCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class JobCode  implements java.io.Serializable {
    private int jobCodeID;

    private String jobCodeDescription;

    private int enable;

    private int jobCode;

    public JobCode() {
    }

    public JobCode(
           int jobCodeID,
           String jobCodeDescription,
           int enable,
           int jobCode) {
           this.jobCodeID = jobCodeID;
           this.jobCodeDescription = jobCodeDescription;
           this.enable = enable;
           this.jobCode = jobCode;
    }


    /**
     * Gets the jobCodeID value for this JobCode.
     * 
     * @return jobCodeID
     */
    public int getJobCodeID() {
        return jobCodeID;
    }


    /**
     * Sets the jobCodeID value for this JobCode.
     * 
     * @param jobCodeID
     */
    public void setJobCodeID(int jobCodeID) {
        this.jobCodeID = jobCodeID;
    }


    /**
     * Gets the jobCodeDescription value for this JobCode.
     * 
     * @return jobCodeDescription
     */
    public String getJobCodeDescription() {
        return jobCodeDescription;
    }


    /**
     * Sets the jobCodeDescription value for this JobCode.
     * 
     * @param jobCodeDescription
     */
    public void setJobCodeDescription(String jobCodeDescription) {
        this.jobCodeDescription = jobCodeDescription;
    }


    /**
     * Gets the enable value for this JobCode.
     * 
     * @return enable
     */
    public int getEnable() {
        return enable;
    }


    /**
     * Sets the enable value for this JobCode.
     * 
     * @param enable
     */
    public void setEnable(int enable) {
        this.enable = enable;
    }


    /**
     * Gets the jobCode value for this JobCode.
     * 
     * @return jobCode
     */
    public int getJobCode() {
        return jobCode;
    }


    /**
     * Sets the jobCode value for this JobCode.
     * 
     * @param jobCode
     */
    public void setJobCode(int jobCode) {
        this.jobCode = jobCode;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof JobCode)) return false;
        JobCode other = (JobCode) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.jobCodeID == other.getJobCodeID() &&
            ((this.jobCodeDescription==null && other.getJobCodeDescription()==null) || 
             (this.jobCodeDescription!=null &&
              this.jobCodeDescription.equals(other.getJobCodeDescription()))) &&
            this.enable == other.getEnable() &&
            this.jobCode == other.getJobCode();
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
        _hashCode += getJobCodeID();
        if (getJobCodeDescription() != null) {
            _hashCode += getJobCodeDescription().hashCode();
        }
        _hashCode += getEnable();
        _hashCode += getJobCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JobCode.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "JobCode"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobCodeID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "JobCodeID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobCodeDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "JobCodeDescription"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobCode"));
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
