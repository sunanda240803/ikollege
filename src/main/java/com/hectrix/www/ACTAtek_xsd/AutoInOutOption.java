/**
 * AutoInOutOption.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class AutoInOutOption implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AutoInOutOption(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _DISABLE = "DISABLE";
    public static final String _ENABLE = "ENABLE";
    public static final String _SPECIAL = "SPECIAL";
    public static final String _REJECTREPEATEDLOGIN = "REJECTREPEATEDLOGIN";
    public static final AutoInOutOption DISABLE = new AutoInOutOption(_DISABLE);
    public static final AutoInOutOption ENABLE = new AutoInOutOption(_ENABLE);
    public static final AutoInOutOption SPECIAL = new AutoInOutOption(_SPECIAL);
    public static final AutoInOutOption REJECTREPEATEDLOGIN = new AutoInOutOption(_REJECTREPEATEDLOGIN);
    public String getValue() { return _value_;}
    public static AutoInOutOption fromValue(String value)
          throws IllegalArgumentException {
        AutoInOutOption enumeration = (AutoInOutOption)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static AutoInOutOption fromString(String value)
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
        new org.apache.axis.description.TypeDesc(AutoInOutOption.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "AutoInOutOption"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
