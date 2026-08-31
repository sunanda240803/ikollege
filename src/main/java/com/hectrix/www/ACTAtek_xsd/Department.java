/**
 * Department.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Department  implements java.io.Serializable {
    private int departmentID;

    private String departmentName;

    private String departmentDescription;

    public Department() {
    }

    public Department(
           int departmentID,
           String departmentName,
           String departmentDescription) {
           this.departmentID = departmentID;
           this.departmentName = departmentName;
           this.departmentDescription = departmentDescription;
    }


    /**
     * Gets the departmentID value for this Department.
     * 
     * @return departmentID
     */
    public int getDepartmentID() {
        return departmentID;
    }


    /**
     * Sets the departmentID value for this Department.
     * 
     * @param departmentID
     */
    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }


    /**
     * Gets the departmentName value for this Department.
     * 
     * @return departmentName
     */
    public String getDepartmentName() {
        return departmentName;
    }


    /**
     * Sets the departmentName value for this Department.
     * 
     * @param departmentName
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }


    /**
     * Gets the departmentDescription value for this Department.
     * 
     * @return departmentDescription
     */
    public String getDepartmentDescription() {
        return departmentDescription;
    }


    /**
     * Sets the departmentDescription value for this Department.
     * 
     * @param departmentDescription
     */
    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Department)) return false;
        Department other = (Department) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.departmentID == other.getDepartmentID() &&
            ((this.departmentName==null && other.getDepartmentName()==null) || 
             (this.departmentName!=null &&
              this.departmentName.equals(other.getDepartmentName()))) &&
            ((this.departmentDescription==null && other.getDepartmentDescription()==null) || 
             (this.departmentDescription!=null &&
              this.departmentDescription.equals(other.getDepartmentDescription())));
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
        _hashCode += getDepartmentID();
        if (getDepartmentName() != null) {
            _hashCode += getDepartmentName().hashCode();
        }
        if (getDepartmentDescription() != null) {
            _hashCode += getDepartmentDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Department.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Department"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
