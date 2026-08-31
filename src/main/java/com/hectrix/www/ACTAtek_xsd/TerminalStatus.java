/**
 * TerminalStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class TerminalStatus  implements java.io.Serializable {
    private String modelNumber;

    private String serialNumber;

    private String firmwareVersion;

    private String FAMVersion;

    private String description;

    private int registeredUsers;

    private int maximumUsers;

    private int automatchUsers;

    private int maximumAutomatchUsers;

    private String currentStatus;

    public TerminalStatus() {
    }

    public TerminalStatus(
           String modelNumber,
           String serialNumber,
           String firmwareVersion,
           String FAMVersion,
           String description,
           int registeredUsers,
           int maximumUsers,
           int automatchUsers,
           int maximumAutomatchUsers,
           String currentStatus) {
           this.modelNumber = modelNumber;
           this.serialNumber = serialNumber;
           this.firmwareVersion = firmwareVersion;
           this.FAMVersion = FAMVersion;
           this.description = description;
           this.registeredUsers = registeredUsers;
           this.maximumUsers = maximumUsers;
           this.automatchUsers = automatchUsers;
           this.maximumAutomatchUsers = maximumAutomatchUsers;
           this.currentStatus = currentStatus;
    }


    /**
     * Gets the modelNumber value for this TerminalStatus.
     * 
     * @return modelNumber
     */
    public String getModelNumber() {
        return modelNumber;
    }


    /**
     * Sets the modelNumber value for this TerminalStatus.
     * 
     * @param modelNumber
     */
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }


    /**
     * Gets the serialNumber value for this TerminalStatus.
     * 
     * @return serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }


    /**
     * Sets the serialNumber value for this TerminalStatus.
     * 
     * @param serialNumber
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


    /**
     * Gets the firmwareVersion value for this TerminalStatus.
     * 
     * @return firmwareVersion
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }


    /**
     * Sets the firmwareVersion value for this TerminalStatus.
     * 
     * @param firmwareVersion
     */
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }


    /**
     * Gets the FAMVersion value for this TerminalStatus.
     * 
     * @return FAMVersion
     */
    public String getFAMVersion() {
        return FAMVersion;
    }


    /**
     * Sets the FAMVersion value for this TerminalStatus.
     * 
     * @param FAMVersion
     */
    public void setFAMVersion(String FAMVersion) {
        this.FAMVersion = FAMVersion;
    }


    /**
     * Gets the description value for this TerminalStatus.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this TerminalStatus.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets the registeredUsers value for this TerminalStatus.
     * 
     * @return registeredUsers
     */
    public int getRegisteredUsers() {
        return registeredUsers;
    }


    /**
     * Sets the registeredUsers value for this TerminalStatus.
     * 
     * @param registeredUsers
     */
    public void setRegisteredUsers(int registeredUsers) {
        this.registeredUsers = registeredUsers;
    }


    /**
     * Gets the maximumUsers value for this TerminalStatus.
     * 
     * @return maximumUsers
     */
    public int getMaximumUsers() {
        return maximumUsers;
    }


    /**
     * Sets the maximumUsers value for this TerminalStatus.
     * 
     * @param maximumUsers
     */
    public void setMaximumUsers(int maximumUsers) {
        this.maximumUsers = maximumUsers;
    }


    /**
     * Gets the automatchUsers value for this TerminalStatus.
     * 
     * @return automatchUsers
     */
    public int getAutomatchUsers() {
        return automatchUsers;
    }


    /**
     * Sets the automatchUsers value for this TerminalStatus.
     * 
     * @param automatchUsers
     */
    public void setAutomatchUsers(int automatchUsers) {
        this.automatchUsers = automatchUsers;
    }


    /**
     * Gets the maximumAutomatchUsers value for this TerminalStatus.
     * 
     * @return maximumAutomatchUsers
     */
    public int getMaximumAutomatchUsers() {
        return maximumAutomatchUsers;
    }


    /**
     * Sets the maximumAutomatchUsers value for this TerminalStatus.
     * 
     * @param maximumAutomatchUsers
     */
    public void setMaximumAutomatchUsers(int maximumAutomatchUsers) {
        this.maximumAutomatchUsers = maximumAutomatchUsers;
    }


    /**
     * Gets the currentStatus value for this TerminalStatus.
     * 
     * @return currentStatus
     */
    public String getCurrentStatus() {
        return currentStatus;
    }


    /**
     * Sets the currentStatus value for this TerminalStatus.
     * 
     * @param currentStatus
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof TerminalStatus)) return false;
        TerminalStatus other = (TerminalStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modelNumber==null && other.getModelNumber()==null) || 
             (this.modelNumber!=null &&
              this.modelNumber.equals(other.getModelNumber()))) &&
            ((this.serialNumber==null && other.getSerialNumber()==null) || 
             (this.serialNumber!=null &&
              this.serialNumber.equals(other.getSerialNumber()))) &&
            ((this.firmwareVersion==null && other.getFirmwareVersion()==null) || 
             (this.firmwareVersion!=null &&
              this.firmwareVersion.equals(other.getFirmwareVersion()))) &&
            ((this.FAMVersion==null && other.getFAMVersion()==null) || 
             (this.FAMVersion!=null &&
              this.FAMVersion.equals(other.getFAMVersion()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            this.registeredUsers == other.getRegisteredUsers() &&
            this.maximumUsers == other.getMaximumUsers() &&
            this.automatchUsers == other.getAutomatchUsers() &&
            this.maximumAutomatchUsers == other.getMaximumAutomatchUsers() &&
            ((this.currentStatus==null && other.getCurrentStatus()==null) || 
             (this.currentStatus!=null &&
              this.currentStatus.equals(other.getCurrentStatus())));
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
        if (getModelNumber() != null) {
            _hashCode += getModelNumber().hashCode();
        }
        if (getSerialNumber() != null) {
            _hashCode += getSerialNumber().hashCode();
        }
        if (getFirmwareVersion() != null) {
            _hashCode += getFirmwareVersion().hashCode();
        }
        if (getFAMVersion() != null) {
            _hashCode += getFAMVersion().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        _hashCode += getRegisteredUsers();
        _hashCode += getMaximumUsers();
        _hashCode += getAutomatchUsers();
        _hashCode += getMaximumAutomatchUsers();
        if (getCurrentStatus() != null) {
            _hashCode += getCurrentStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TerminalStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "TerminalStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modelNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modelNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serialNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serialNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firmwareVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firmwareVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("FAMVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "FAMVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registeredUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "registeredUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maximumUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automatchUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "automatchUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumAutomatchUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maximumAutomatchUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentStatus"));
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
