/**
 * TOTPformat.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class TOTPformat implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected TOTPformat(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _value1 = "QR-PNG";
    public static final String _value2 = "QR-SVG";
    public static final String _value3 = "QR-ANSI";
    public static final String _value4 = "QR-PLAINTEXT";
    public static final String _value5 = "OTP-PASSCODE";
    public static final String _value6 = "NONE";
    public static final TOTPformat value1 = new TOTPformat(_value1);
    public static final TOTPformat value2 = new TOTPformat(_value2);
    public static final TOTPformat value3 = new TOTPformat(_value3);
    public static final TOTPformat value4 = new TOTPformat(_value4);
    public static final TOTPformat value5 = new TOTPformat(_value5);
    public static final TOTPformat value6 = new TOTPformat(_value6);
    public String getValue() { return _value_;}
    public static TOTPformat fromValue(String value)
          throws IllegalArgumentException {
        TOTPformat enumeration = (TOTPformat)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static TOTPformat fromString(String value)
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
        new org.apache.axis.description.TypeDesc(TOTPformat.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "TOTPformat"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
