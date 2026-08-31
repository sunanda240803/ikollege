/**
 * GetLogsCriteria.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class GetLogsCriteria  implements java.io.Serializable {
    private java.util.Calendar from;

    private java.util.Calendar to;

    private String employeeName;

    private String employeeID;

    private Integer departmentID;

    private EventType trigger;

    private String terminalSN;

    public GetLogsCriteria() {
    }

    public GetLogsCriteria(
           java.util.Calendar from,
           java.util.Calendar to,
           String employeeName,
           String employeeID,
           Integer departmentID,
           EventType trigger,
           String terminalSN) {
           this.from = from;
           this.to = to;
           this.employeeName = employeeName;
           this.employeeID = employeeID;
           this.departmentID = departmentID;
           this.trigger = trigger;
           this.terminalSN = terminalSN;
    }


    /**
     * Gets the from value for this GetLogsCriteria.
     * 
     * @return from
     */
    public java.util.Calendar getFrom() {
        return from;
    }


    /**
     * Sets the from value for this GetLogsCriteria.
     * 
     * @param from
     */
    public void setFrom(java.util.Calendar from) {
        this.from = from;
    }


    /**
     * Gets the to value for this GetLogsCriteria.
     * 
     * @return to
     */
    public java.util.Calendar getTo() {
        return to;
    }


    /**
     * Sets the to value for this GetLogsCriteria.
     * 
     * @param to
     */
    public void setTo(java.util.Calendar to) {
        this.to = to;
    }


    /**
     * Gets the employeeName value for this GetLogsCriteria.
     * 
     * @return employeeName
     */
    public String getEmployeeName() {
        return employeeName;
    }


    /**
     * Sets the employeeName value for this GetLogsCriteria.
     * 
     * @param employeeName
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }


    /**
     * Gets the employeeID value for this GetLogsCriteria.
     * 
     * @return employeeID
     */
    public String getEmployeeID() {
        return employeeID;
    }


    /**
     * Sets the employeeID value for this GetLogsCriteria.
     * 
     * @param employeeID
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }


    /**
     * Gets the departmentID value for this GetLogsCriteria.
     * 
     * @return departmentID
     */
    public Integer getDepartmentID() {
        return departmentID;
    }


    /**
     * Sets the departmentID value for this GetLogsCriteria.
     * 
     * @param departmentID
     */
    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }


    /**
     * Gets the trigger value for this GetLogsCriteria.
     * 
     * @return trigger
     */
    public EventType getTrigger() {
        return trigger;
    }


    /**
     * Sets the trigger value for this GetLogsCriteria.
     * 
     * @param trigger
     */
    public void setTrigger(EventType trigger) {
        this.trigger = trigger;
    }


    /**
     * Gets the terminalSN value for this GetLogsCriteria.
     * 
     * @return terminalSN
     */
    public String getTerminalSN() {
        return terminalSN;
    }


    /**
     * Sets the terminalSN value for this GetLogsCriteria.
     * 
     * @param terminalSN
     */
    public void setTerminalSN(String terminalSN) {
        this.terminalSN = terminalSN;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof GetLogsCriteria)) return false;
        GetLogsCriteria other = (GetLogsCriteria) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.from==null && other.getFrom()==null) || 
             (this.from!=null &&
              this.from.equals(other.getFrom()))) &&
            ((this.to==null && other.getTo()==null) || 
             (this.to!=null &&
              this.to.equals(other.getTo()))) &&
            ((this.employeeName==null && other.getEmployeeName()==null) || 
             (this.employeeName!=null &&
              this.employeeName.equals(other.getEmployeeName()))) &&
            ((this.employeeID==null && other.getEmployeeID()==null) || 
             (this.employeeID!=null &&
              this.employeeID.equals(other.getEmployeeID()))) &&
            ((this.departmentID==null && other.getDepartmentID()==null) || 
             (this.departmentID!=null &&
              this.departmentID.equals(other.getDepartmentID()))) &&
            ((this.trigger==null && other.getTrigger()==null) || 
             (this.trigger!=null &&
              this.trigger.equals(other.getTrigger()))) &&
            ((this.terminalSN==null && other.getTerminalSN()==null) || 
             (this.terminalSN!=null &&
              this.terminalSN.equals(other.getTerminalSN())));
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
        if (getFrom() != null) {
            _hashCode += getFrom().hashCode();
        }
        if (getTo() != null) {
            _hashCode += getTo().hashCode();
        }
        if (getEmployeeName() != null) {
            _hashCode += getEmployeeName().hashCode();
        }
        if (getEmployeeID() != null) {
            _hashCode += getEmployeeID().hashCode();
        }
        if (getDepartmentID() != null) {
            _hashCode += getDepartmentID().hashCode();
        }
        if (getTrigger() != null) {
            _hashCode += getTrigger().hashCode();
        }
        if (getTerminalSN() != null) {
            _hashCode += getTerminalSN().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetLogsCriteria.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "getLogsCriteria"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("from");
        elemField.setXmlName(new javax.xml.namespace.QName("", "from"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("to");
        elemField.setXmlName(new javax.xml.namespace.QName("", "to"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "employeeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("employeeID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "employeeID"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trigger");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trigger"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "eventType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalSN");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalSN"));
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
