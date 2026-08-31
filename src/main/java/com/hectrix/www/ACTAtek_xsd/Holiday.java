/**
 * Holiday.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Holiday  implements java.io.Serializable {
    private int holidayDay;

    private int holidayMonth;

    private int holidayYear;

    public Holiday() {
    }

    public Holiday(
           int holidayDay,
           int holidayMonth,
           int holidayYear) {
           this.holidayDay = holidayDay;
           this.holidayMonth = holidayMonth;
           this.holidayYear = holidayYear;
    }


    /**
     * Gets the holidayDay value for this Holiday.
     * 
     * @return holidayDay
     */
    public int getHolidayDay() {
        return holidayDay;
    }


    /**
     * Sets the holidayDay value for this Holiday.
     * 
     * @param holidayDay
     */
    public void setHolidayDay(int holidayDay) {
        this.holidayDay = holidayDay;
    }


    /**
     * Gets the holidayMonth value for this Holiday.
     * 
     * @return holidayMonth
     */
    public int getHolidayMonth() {
        return holidayMonth;
    }


    /**
     * Sets the holidayMonth value for this Holiday.
     * 
     * @param holidayMonth
     */
    public void setHolidayMonth(int holidayMonth) {
        this.holidayMonth = holidayMonth;
    }


    /**
     * Gets the holidayYear value for this Holiday.
     * 
     * @return holidayYear
     */
    public int getHolidayYear() {
        return holidayYear;
    }


    /**
     * Sets the holidayYear value for this Holiday.
     * 
     * @param holidayYear
     */
    public void setHolidayYear(int holidayYear) {
        this.holidayYear = holidayYear;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Holiday)) return false;
        Holiday other = (Holiday) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.holidayDay == other.getHolidayDay() &&
            this.holidayMonth == other.getHolidayMonth() &&
            this.holidayYear == other.getHolidayYear();
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
        _hashCode += getHolidayDay();
        _hashCode += getHolidayMonth();
        _hashCode += getHolidayYear();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Holiday.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Holiday"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holidayDay");
        elemField.setXmlName(new javax.xml.namespace.QName("", "HolidayDay"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holidayMonth");
        elemField.setXmlName(new javax.xml.namespace.QName("", "HolidayMonth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("holidayYear");
        elemField.setXmlName(new javax.xml.namespace.QName("", "HolidayYear"));
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
