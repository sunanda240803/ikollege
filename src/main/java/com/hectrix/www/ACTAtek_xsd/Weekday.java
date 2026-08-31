/**
 * Weekday.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class Weekday implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected Weekday(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _SUNDAY = "SUNDAY";
    public static final String _MONDAY = "MONDAY";
    public static final String _TUESDAY = "TUESDAY";
    public static final String _WEDNESDAY = "WEDNESDAY";
    public static final String _THURSDAY = "THURSDAY";
    public static final String _FRIDAY = "FRIDAY";
    public static final String _SATURDAY = "SATURDAY";
    public static final String _HOLIDAY = "HOLIDAY";
    public static final Weekday SUNDAY = new Weekday(_SUNDAY);
    public static final Weekday MONDAY = new Weekday(_MONDAY);
    public static final Weekday TUESDAY = new Weekday(_TUESDAY);
    public static final Weekday WEDNESDAY = new Weekday(_WEDNESDAY);
    public static final Weekday THURSDAY = new Weekday(_THURSDAY);
    public static final Weekday FRIDAY = new Weekday(_FRIDAY);
    public static final Weekday SATURDAY = new Weekday(_SATURDAY);
    public static final Weekday HOLIDAY = new Weekday(_HOLIDAY);
    public String getValue() { return _value_;}
    public static Weekday fromValue(String value)
          throws IllegalArgumentException {
        Weekday enumeration = (Weekday)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static Weekday fromString(String value)
          throws IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public String toString() { return _value_;}
    public Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Weekday.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "Weekday"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
