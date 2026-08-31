/**
 * TriggerType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class TriggerType implements java.io.Serializable {
    private String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected TriggerType(String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final String _value1 = "UNKNOWN";
    public static final String _value2 = "IN";
    public static final String _value3 = "OUT";
    public static final String _value4 = "F1";
    public static final String _value5 = "F2";
    public static final String _value6 = "F3";
    public static final String _value7 = "F4";
    public static final String _value8 = "F5";
    public static final String _value9 = "F6";
    public static final String _value10 = "F7";
    public static final String _value11 = "F8";
    public static final String _value12 = "F9";
    public static final String _value13 = "F10";
    public static final String _value14 = "ID-UNKNOWN";
    public static final String _value15 = "REJECTED";
    public static final String _value16 = "ID-RESERVED";
    public static final String _value17 = "RESET-ALL";
    public static final String _value18 = "RESET-ACC";
    public static final String _value19 = "RESET-DEVICE";
    public static final String _value20 = "RESET-PASSWORD";
    public static final String _value21 = "RESET-DEFAULT";
    public static final String _value22 = "SYSTEM-LOGIN";
    public static final String _value23 = "ADMIN-LOGIN";
    public static final String _value24 = "USER-LOGIN";
    public static final String _value25 = "NETWORK-CHANGE";
    public static final String _value26 = "PATCHED";
    public static final String _value27 = "BACKUP-DB";
    public static final String _value28 = "RESTORE-DB";
    public static final String _value29 = "SYSTEM-RSVD";
    public static final String _value30 = "ENROLL-USER";
    public static final String _value31 = "REMOTE-DOOR-OPEN";
    public static final String _value32 = "REMOVE-USER";
    public static final String _value33 = "SUSPEND-USER";
    public static final String _value34 = "EDIT-EVENT";
    public static final String _value35 = "RESET-ADMIN-PW";
    public static final String _value36 = "ADMIN-RSVDIT";
    public static final String _value37 = "F11";
    public static final String _value38 = "F12";
    public static final String _value39 = "F13";
    public static final String _value40 = "F14";
    public static final String _value41 = "F15";
    public static final String _value42 = "F16";
    public static final String _value43 = "F17";
    public static final String _value44 = "F18";
    public static final String _value45 = "F19";
    public static final String _value46 = "F20";
    public static final String _value47 = "F21";
    public static final String _value48 = "F22";
    public static final String _value49 = "F23";
    public static final String _value50 = "F24";
    public static final String _value51 = "F25";
    public static final String _value52 = "F26";
    public static final String _value53 = "F27";
    public static final String _value54 = "F28";
    public static final String _value55 = "F29";
    public static final String _value56 = "F30";
    public static final String _value57 = "F31";
    public static final String _value58 = "F32";
    public static final String _value59 = "F33";
    public static final String _value60 = "F34";
    public static final String _value61 = "F35";
    public static final String _value62 = "F36";
    public static final String _value63 = "F37";
    public static final String _value64 = "F38";
    public static final String _value65 = "F39";
    public static final String _value66 = "F40";
    public static final String _value67 = "SCHEDULE-DOOR-OPEN";
    public static final String _value68 = "SCHEDULE-DOOR-CLOSE";
    public static final String _value69 = "SCHEDULE-BELL-ON";
    public static final String _value70 = "DOOR-IS-OPENED";
    public static final String _value71 = "DOOR-IS-CLOSED";
    public static final String _value72 = "CASE-IS-OPENED";
    public static final String _value73 = "CASE-IS-CLOSED";
    public static final String _value74 = "TRIGGEREND";
    public static final TriggerType value1 = new TriggerType(_value1);
    public static final TriggerType value2 = new TriggerType(_value2);
    public static final TriggerType value3 = new TriggerType(_value3);
    public static final TriggerType value4 = new TriggerType(_value4);
    public static final TriggerType value5 = new TriggerType(_value5);
    public static final TriggerType value6 = new TriggerType(_value6);
    public static final TriggerType value7 = new TriggerType(_value7);
    public static final TriggerType value8 = new TriggerType(_value8);
    public static final TriggerType value9 = new TriggerType(_value9);
    public static final TriggerType value10 = new TriggerType(_value10);
    public static final TriggerType value11 = new TriggerType(_value11);
    public static final TriggerType value12 = new TriggerType(_value12);
    public static final TriggerType value13 = new TriggerType(_value13);
    public static final TriggerType value14 = new TriggerType(_value14);
    public static final TriggerType value15 = new TriggerType(_value15);
    public static final TriggerType value16 = new TriggerType(_value16);
    public static final TriggerType value17 = new TriggerType(_value17);
    public static final TriggerType value18 = new TriggerType(_value18);
    public static final TriggerType value19 = new TriggerType(_value19);
    public static final TriggerType value20 = new TriggerType(_value20);
    public static final TriggerType value21 = new TriggerType(_value21);
    public static final TriggerType value22 = new TriggerType(_value22);
    public static final TriggerType value23 = new TriggerType(_value23);
    public static final TriggerType value24 = new TriggerType(_value24);
    public static final TriggerType value25 = new TriggerType(_value25);
    public static final TriggerType value26 = new TriggerType(_value26);
    public static final TriggerType value27 = new TriggerType(_value27);
    public static final TriggerType value28 = new TriggerType(_value28);
    public static final TriggerType value29 = new TriggerType(_value29);
    public static final TriggerType value30 = new TriggerType(_value30);
    public static final TriggerType value31 = new TriggerType(_value31);
    public static final TriggerType value32 = new TriggerType(_value32);
    public static final TriggerType value33 = new TriggerType(_value33);
    public static final TriggerType value34 = new TriggerType(_value34);
    public static final TriggerType value35 = new TriggerType(_value35);
    public static final TriggerType value36 = new TriggerType(_value36);
    public static final TriggerType value37 = new TriggerType(_value37);
    public static final TriggerType value38 = new TriggerType(_value38);
    public static final TriggerType value39 = new TriggerType(_value39);
    public static final TriggerType value40 = new TriggerType(_value40);
    public static final TriggerType value41 = new TriggerType(_value41);
    public static final TriggerType value42 = new TriggerType(_value42);
    public static final TriggerType value43 = new TriggerType(_value43);
    public static final TriggerType value44 = new TriggerType(_value44);
    public static final TriggerType value45 = new TriggerType(_value45);
    public static final TriggerType value46 = new TriggerType(_value46);
    public static final TriggerType value47 = new TriggerType(_value47);
    public static final TriggerType value48 = new TriggerType(_value48);
    public static final TriggerType value49 = new TriggerType(_value49);
    public static final TriggerType value50 = new TriggerType(_value50);
    public static final TriggerType value51 = new TriggerType(_value51);
    public static final TriggerType value52 = new TriggerType(_value52);
    public static final TriggerType value53 = new TriggerType(_value53);
    public static final TriggerType value54 = new TriggerType(_value54);
    public static final TriggerType value55 = new TriggerType(_value55);
    public static final TriggerType value56 = new TriggerType(_value56);
    public static final TriggerType value57 = new TriggerType(_value57);
    public static final TriggerType value58 = new TriggerType(_value58);
    public static final TriggerType value59 = new TriggerType(_value59);
    public static final TriggerType value60 = new TriggerType(_value60);
    public static final TriggerType value61 = new TriggerType(_value61);
    public static final TriggerType value62 = new TriggerType(_value62);
    public static final TriggerType value63 = new TriggerType(_value63);
    public static final TriggerType value64 = new TriggerType(_value64);
    public static final TriggerType value65 = new TriggerType(_value65);
    public static final TriggerType value66 = new TriggerType(_value66);
    public static final TriggerType value67 = new TriggerType(_value67);
    public static final TriggerType value68 = new TriggerType(_value68);
    public static final TriggerType value69 = new TriggerType(_value69);
    public static final TriggerType value70 = new TriggerType(_value70);
    public static final TriggerType value71 = new TriggerType(_value71);
    public static final TriggerType value72 = new TriggerType(_value72);
    public static final TriggerType value73 = new TriggerType(_value73);
    public static final TriggerType value74 = new TriggerType(_value74);
    public String getValue() { return _value_;}
    public static TriggerType fromValue(String value)
          throws IllegalArgumentException {
        TriggerType enumeration = (TriggerType)
            _table_.get(value);
        if (enumeration==null) throw new IllegalArgumentException();
        return enumeration;
    }
    public static TriggerType fromString(String value)
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
        new org.apache.axis.description.TypeDesc(TriggerType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "triggerType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
