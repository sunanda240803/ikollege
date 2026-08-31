/**
 * Agent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Agent  implements java.io.Serializable {
    private int port;

    private String ip;

    private java.util.Calendar registrationTime;

    private java.util.Calendar lastUpdate;

    private java.util.Calendar lastCheck;

    private boolean lastCheckStatus;

    private boolean active;

    private String link;

    public Agent() {
    }

    public Agent(
           int port,
           String ip,
           java.util.Calendar registrationTime,
           java.util.Calendar lastUpdate,
           java.util.Calendar lastCheck,
           boolean lastCheckStatus,
           boolean active,
           String link) {
           this.port = port;
           this.ip = ip;
           this.registrationTime = registrationTime;
           this.lastUpdate = lastUpdate;
           this.lastCheck = lastCheck;
           this.lastCheckStatus = lastCheckStatus;
           this.active = active;
           this.link = link;
    }


    /**
     * Gets the port value for this Agent.
     * 
     * @return port
     */
    public int getPort() {
        return port;
    }


    /**
     * Sets the port value for this Agent.
     * 
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Gets the ip value for this Agent.
     * 
     * @return ip
     */
    public String getIp() {
        return ip;
    }


    /**
     * Sets the ip value for this Agent.
     * 
     * @param ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }


    /**
     * Gets the registrationTime value for this Agent.
     * 
     * @return registrationTime
     */
    public java.util.Calendar getRegistrationTime() {
        return registrationTime;
    }


    /**
     * Sets the registrationTime value for this Agent.
     * 
     * @param registrationTime
     */
    public void setRegistrationTime(java.util.Calendar registrationTime) {
        this.registrationTime = registrationTime;
    }


    /**
     * Gets the lastUpdate value for this Agent.
     * 
     * @return lastUpdate
     */
    public java.util.Calendar getLastUpdate() {
        return lastUpdate;
    }


    /**
     * Sets the lastUpdate value for this Agent.
     * 
     * @param lastUpdate
     */
    public void setLastUpdate(java.util.Calendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    /**
     * Gets the lastCheck value for this Agent.
     * 
     * @return lastCheck
     */
    public java.util.Calendar getLastCheck() {
        return lastCheck;
    }


    /**
     * Sets the lastCheck value for this Agent.
     * 
     * @param lastCheck
     */
    public void setLastCheck(java.util.Calendar lastCheck) {
        this.lastCheck = lastCheck;
    }


    /**
     * Gets the lastCheckStatus value for this Agent.
     * 
     * @return lastCheckStatus
     */
    public boolean isLastCheckStatus() {
        return lastCheckStatus;
    }


    /**
     * Sets the lastCheckStatus value for this Agent.
     * 
     * @param lastCheckStatus
     */
    public void setLastCheckStatus(boolean lastCheckStatus) {
        this.lastCheckStatus = lastCheckStatus;
    }


    /**
     * Gets the active value for this Agent.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this Agent.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the link value for this Agent.
     * 
     * @return link
     */
    public String getLink() {
        return link;
    }


    /**
     * Sets the link value for this Agent.
     * 
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Agent)) return false;
        Agent other = (Agent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.port == other.getPort() &&
            ((this.ip==null && other.getIp()==null) || 
             (this.ip!=null &&
              this.ip.equals(other.getIp()))) &&
            ((this.registrationTime==null && other.getRegistrationTime()==null) || 
             (this.registrationTime!=null &&
              this.registrationTime.equals(other.getRegistrationTime()))) &&
            ((this.lastUpdate==null && other.getLastUpdate()==null) || 
             (this.lastUpdate!=null &&
              this.lastUpdate.equals(other.getLastUpdate()))) &&
            ((this.lastCheck==null && other.getLastCheck()==null) || 
             (this.lastCheck!=null &&
              this.lastCheck.equals(other.getLastCheck()))) &&
            this.lastCheckStatus == other.isLastCheckStatus() &&
            this.active == other.isActive() &&
            ((this.link==null && other.getLink()==null) || 
             (this.link!=null &&
              this.link.equals(other.getLink())));
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
        _hashCode += getPort();
        if (getIp() != null) {
            _hashCode += getIp().hashCode();
        }
        if (getRegistrationTime() != null) {
            _hashCode += getRegistrationTime().hashCode();
        }
        if (getLastUpdate() != null) {
            _hashCode += getLastUpdate().hashCode();
        }
        if (getLastCheck() != null) {
            _hashCode += getLastCheck().hashCode();
        }
        _hashCode += (isLastCheckStatus() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getLink() != null) {
            _hashCode += getLink().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Agent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Agent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("port");
        elemField.setXmlName(new javax.xml.namespace.QName("", "port"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ip");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ip"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registrationTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "registrationTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastUpdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastCheckStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastCheckStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link"));
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
