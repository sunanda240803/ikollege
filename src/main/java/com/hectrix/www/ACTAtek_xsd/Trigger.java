/**
 * Trigger.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Trigger  implements java.io.Serializable {
    private TriggerType triggerType;

    private String triggerName;

    private Timespec[] timeSpec;

    public Trigger() {
    }

    public Trigger(
           TriggerType triggerType,
           String triggerName,
           Timespec[] timeSpec) {
           this.triggerType = triggerType;
           this.triggerName = triggerName;
           this.timeSpec = timeSpec;
    }


    /**
     * Gets the triggerType value for this Trigger.
     * 
     * @return triggerType
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }


    /**
     * Sets the triggerType value for this Trigger.
     * 
     * @param triggerType
     */
    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }


    /**
     * Gets the triggerName value for this Trigger.
     * 
     * @return triggerName
     */
    public String getTriggerName() {
        return triggerName;
    }


    /**
     * Sets the triggerName value for this Trigger.
     * 
     * @param triggerName
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }


    /**
     * Gets the timeSpec value for this Trigger.
     * 
     * @return timeSpec
     */
    public Timespec[] getTimeSpec() {
        return timeSpec;
    }


    /**
     * Sets the timeSpec value for this Trigger.
     * 
     * @param timeSpec
     */
    public void setTimeSpec(Timespec[] timeSpec) {
        this.timeSpec = timeSpec;
    }

    public Timespec getTimeSpec(int i) {
        return this.timeSpec[i];
    }

    public void setTimeSpec(int i, Timespec _value) {
        this.timeSpec[i] = _value;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Trigger)) return false;
        Trigger other = (Trigger) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.triggerType==null && other.getTriggerType()==null) || 
             (this.triggerType!=null &&
              this.triggerType.equals(other.getTriggerType()))) &&
            ((this.triggerName==null && other.getTriggerName()==null) || 
             (this.triggerName!=null &&
              this.triggerName.equals(other.getTriggerName()))) &&
            ((this.timeSpec==null && other.getTimeSpec()==null) || 
             (this.timeSpec!=null &&
              java.util.Arrays.equals(this.timeSpec, other.getTimeSpec())));
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
        if (getTriggerType() != null) {
            _hashCode += getTriggerType().hashCode();
        }
        if (getTriggerName() != null) {
            _hashCode += getTriggerName().hashCode();
        }
        if (getTimeSpec() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getTimeSpec());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getTimeSpec(), i);
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
        new org.apache.axis.description.TypeDesc(Trigger.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Trigger"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("triggerType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "triggerType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "triggerType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("triggerName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "triggerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeSpec");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeSpec"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Timespec"));
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
