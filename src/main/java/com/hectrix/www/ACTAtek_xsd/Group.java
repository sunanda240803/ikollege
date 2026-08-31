/**
 * Group.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Group  implements java.io.Serializable {
    private int groupID;

    private String groupName;

    private int departmentID;

    private AccessRight[] accessRight;

    public Group() {
    }

    public Group(
           int groupID,
           String groupName,
           int departmentID,
           AccessRight[] accessRight) {
           this.groupID = groupID;
           this.groupName = groupName;
           this.departmentID = departmentID;
           this.accessRight = accessRight;
    }


    /**
     * Gets the groupID value for this Group.
     * 
     * @return groupID
     */
    public int getGroupID() {
        return groupID;
    }


    /**
     * Sets the groupID value for this Group.
     * 
     * @param groupID
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }


    /**
     * Gets the groupName value for this Group.
     * 
     * @return groupName
     */
    public String getGroupName() {
        return groupName;
    }


    /**
     * Sets the groupName value for this Group.
     * 
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    /**
     * Gets the departmentID value for this Group.
     * 
     * @return departmentID
     */
    public int getDepartmentID() {
        return departmentID;
    }


    /**
     * Sets the departmentID value for this Group.
     * 
     * @param departmentID
     */
    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }


    /**
     * Gets the accessRight value for this Group.
     * 
     * @return accessRight
     */
    public AccessRight[] getAccessRight() {
        return accessRight;
    }


    /**
     * Sets the accessRight value for this Group.
     * 
     * @param accessRight
     */
    public void setAccessRight(AccessRight[] accessRight) {
        this.accessRight = accessRight;
    }

    public AccessRight getAccessRight(int i) {
        return this.accessRight[i];
    }

    public void setAccessRight(int i, AccessRight _value) {
        this.accessRight[i] = _value;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Group)) return false;
        Group other = (Group) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.groupID == other.getGroupID() &&
            ((this.groupName==null && other.getGroupName()==null) || 
             (this.groupName!=null &&
              this.groupName.equals(other.getGroupName()))) &&
            this.departmentID == other.getDepartmentID() &&
            ((this.accessRight==null && other.getAccessRight()==null) || 
             (this.accessRight!=null &&
              java.util.Arrays.equals(this.accessRight, other.getAccessRight())));
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
        _hashCode += getGroupID();
        if (getGroupName() != null) {
            _hashCode += getGroupName().hashCode();
        }
        _hashCode += getDepartmentID();
        if (getAccessRight() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAccessRight());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getAccessRight(), i);
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
        new org.apache.axis.description.TypeDesc(Group.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Group"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessRight");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accessRight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "AccessRight"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
