/**
 * Timespec.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Timespec  implements java.io.Serializable {
    private Weekday[] weekday;

    private String timePattern;

    public Timespec() {
    }

    public Timespec(
           Weekday[] weekday,
           String timePattern) {
           this.weekday = weekday;
           this.timePattern = timePattern;
    }


    /**
     * Gets the weekday value for this Timespec.
     * 
     * @return weekday
     */
    public Weekday[] getWeekday() {
        return weekday;
    }


    /**
     * Sets the weekday value for this Timespec.
     * 
     * @param weekday
     */
    public void setWeekday(Weekday[] weekday) {
        this.weekday = weekday;
    }

    public Weekday getWeekday(int i) {
        return this.weekday[i];
    }

    public void setWeekday(int i, Weekday _value) {
        this.weekday[i] = _value;
    }


    /**
     * Gets the timePattern value for this Timespec.
     * 
     * @return timePattern
     */
    public String getTimePattern() {
        return timePattern;
    }


    /**
     * Sets the timePattern value for this Timespec.
     * 
     * @param timePattern
     */
    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Timespec)) return false;
        Timespec other = (Timespec) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.weekday==null && other.getWeekday()==null) || 
             (this.weekday!=null &&
              java.util.Arrays.equals(this.weekday, other.getWeekday()))) &&
            ((this.timePattern==null && other.getTimePattern()==null) || 
             (this.timePattern!=null &&
              this.timePattern.equals(other.getTimePattern())));
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
        if (getWeekday() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWeekday());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getWeekday(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTimePattern() != null) {
            _hashCode += getTimePattern().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Timespec.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Timespec"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("weekday");
        elemField.setXmlName(new javax.xml.namespace.QName("", "weekday"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Weekday"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timePattern");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timePattern"));
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
