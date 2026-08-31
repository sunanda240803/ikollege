/**
 * AdminLevel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class AdminLevel implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected AdminLevel(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _PERSONALUSER = "PERSONALUSER";
    public static final String _NETWORKADMIN = "NETWORKADMIN";
    public static final String _USERADMIN = "USERADMIN";
    public static final String _SUPERADMIN = "SUPERADMIN";
    public static final AdminLevel PERSONALUSER = new AdminLevel(_PERSONALUSER);
    public static final AdminLevel NETWORKADMIN = new AdminLevel(_NETWORKADMIN);
    public static final AdminLevel USERADMIN = new AdminLevel(_USERADMIN);
    public static final AdminLevel SUPERADMIN = new AdminLevel(_SUPERADMIN);
    public String getValue() { return _value_;}
    public static AdminLevel fromValue(String value)
          throws IllegalArgumentException {
        AdminLevel enumeration = (AdminLevel)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static AdminLevel fromString(String value)
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
        new org.apache.axis.description.TypeDesc(AdminLevel.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "AdminLevel"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
