/**
 * GetUsersCriteria.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class GetUsersCriteria  implements java.io.Serializable {
    private String userID;

    private String partialUserID;

    private String lastName;

    private String firstName;

    private String otherName;

    private Integer departmentID;

    public GetUsersCriteria() {
    }

    public GetUsersCriteria(
           String userID,
           String partialUserID,
           String lastName,
           String firstName,
           String otherName,
           Integer departmentID) {
           this.userID = userID;
           this.partialUserID = partialUserID;
           this.lastName = lastName;
           this.firstName = firstName;
           this.otherName = otherName;
           this.departmentID = departmentID;
    }


    /**
     * Gets the userID value for this GetUsersCriteria.
     * 
     * @return userID
     */
    public String getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this GetUsersCriteria.
     * 
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }


    /**
     * Gets the partialUserID value for this GetUsersCriteria.
     * 
     * @return partialUserID
     */
    public String getPartialUserID() {
        return partialUserID;
    }


    /**
     * Sets the partialUserID value for this GetUsersCriteria.
     * 
     * @param partialUserID
     */
    public void setPartialUserID(String partialUserID) {
        this.partialUserID = partialUserID;
    }


    /**
     * Gets the lastName value for this GetUsersCriteria.
     * 
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this GetUsersCriteria.
     * 
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the firstName value for this GetUsersCriteria.
     * 
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this GetUsersCriteria.
     * 
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the otherName value for this GetUsersCriteria.
     * 
     * @return otherName
     */
    public String getOtherName() {
        return otherName;
    }


    /**
     * Sets the otherName value for this GetUsersCriteria.
     * 
     * @param otherName
     */
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }


    /**
     * Gets the departmentID value for this GetUsersCriteria.
     * 
     * @return departmentID
     */
    public Integer getDepartmentID() {
        return departmentID;
    }


    /**
     * Sets the departmentID value for this GetUsersCriteria.
     * 
     * @param departmentID
     */
    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetUsersCriteria)) return false;
        GetUsersCriteria other = (GetUsersCriteria) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            ((this.partialUserID==null && other.getPartialUserID()==null) || 
             (this.partialUserID!=null &&
              this.partialUserID.equals(other.getPartialUserID()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            ((this.otherName==null && other.getOtherName()==null) || 
             (this.otherName!=null &&
              this.otherName.equals(other.getOtherName()))) &&
            ((this.departmentID==null && other.getDepartmentID()==null) || 
             (this.departmentID!=null &&
              this.departmentID.equals(other.getDepartmentID())));
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
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        if (getPartialUserID() != null) {
            _hashCode += getPartialUserID().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        if (getOtherName() != null) {
            _hashCode += getOtherName().hashCode();
        }
        if (getDepartmentID() != null) {
            _hashCode += getDepartmentID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetUsersCriteria.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "getUsersCriteria"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partialUserID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "partialUserID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("otherName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "otherName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
