/**
 * AccessRight.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class AccessRight  implements java.io.Serializable {
    private int accessID;

    private String terminalSN;

    private boolean quickAccess;

    private Timespec[] timeSpec;

    public AccessRight() {
    }

    public AccessRight(
           int accessID,
           String terminalSN,
           boolean quickAccess,
           Timespec[] timeSpec) {
           this.accessID = accessID;
           this.terminalSN = terminalSN;
           this.quickAccess = quickAccess;
           this.timeSpec = timeSpec;
    }


    /**
     * Gets the accessID value for this AccessRight.
     * 
     * @return accessID
     */
    public int getAccessID() {
        return accessID;
    }


    /**
     * Sets the accessID value for this AccessRight.
     * 
     * @param accessID
     */
    public void setAccessID(int accessID) {
        this.accessID = accessID;
    }


    /**
     * Gets the terminalSN value for this AccessRight.
     * 
     * @return terminalSN
     */
    public String getTerminalSN() {
        return terminalSN;
    }


    /**
     * Sets the terminalSN value for this AccessRight.
     * 
     * @param terminalSN
     */
    public void setTerminalSN(String terminalSN) {
        this.terminalSN = terminalSN;
    }


    /**
     * Gets the quickAccess value for this AccessRight.
     * 
     * @return quickAccess
     */
    public boolean isQuickAccess() {
        return quickAccess;
    }


    /**
     * Sets the quickAccess value for this AccessRight.
     * 
     * @param quickAccess
     */
    public void setQuickAccess(boolean quickAccess) {
        this.quickAccess = quickAccess;
    }


    /**
     * Gets the timeSpec value for this AccessRight.
     * 
     * @return timeSpec
     */
    public Timespec[] getTimeSpec() {
        return timeSpec;
    }


    /**
     * Sets the timeSpec value for this AccessRight.
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
        if (!(obj instanceof AccessRight)) return false;
        AccessRight other = (AccessRight) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.accessID == other.getAccessID() &&
            ((this.terminalSN==null && other.getTerminalSN()==null) || 
             (this.terminalSN!=null &&
              this.terminalSN.equals(other.getTerminalSN()))) &&
            this.quickAccess == other.isQuickAccess() &&
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
        _hashCode += getAccessID();
        if (getTerminalSN() != null) {
            _hashCode += getTerminalSN().hashCode();
        }
        _hashCode += (isQuickAccess() ? Boolean.TRUE : Boolean.FALSE).hashCode();
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
        new org.apache.axis.description.TypeDesc(AccessRight.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "AccessRight"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accessID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalSN");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalSN"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quickAccess");
        elemField.setXmlName(new javax.xml.namespace.QName("", "quickAccess"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
