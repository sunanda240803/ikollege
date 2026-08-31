/**
 * FingerprintSecurityLevel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class FingerprintSecurityLevel implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected FingerprintSecurityLevel(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _LOWEST = "LOWEST";
    public static final String _LOW = "LOW";
    public static final String _NORMAL = "NORMAL";
    public static final String _HIGH = "HIGH";
    public static final String _HIGHEST = "HIGHEST";
    public static final FingerprintSecurityLevel LOWEST = new FingerprintSecurityLevel(_LOWEST);
    public static final FingerprintSecurityLevel LOW = new FingerprintSecurityLevel(_LOW);
    public static final FingerprintSecurityLevel NORMAL = new FingerprintSecurityLevel(_NORMAL);
    public static final FingerprintSecurityLevel HIGH = new FingerprintSecurityLevel(_HIGH);
    public static final FingerprintSecurityLevel HIGHEST = new FingerprintSecurityLevel(_HIGHEST);
    public String getValue() { return _value_;}
    public static FingerprintSecurityLevel fromValue(String value)
          throws IllegalArgumentException {
        FingerprintSecurityLevel enumeration = (FingerprintSecurityLevel)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static FingerprintSecurityLevel fromString(String value)
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
        new org.apache.axis.description.TypeDesc(FingerprintSecurityLevel.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "FingerprintSecurityLevel"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
