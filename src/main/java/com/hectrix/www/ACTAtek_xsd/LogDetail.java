/**
 * LogDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_xsd;

public class LogDetail  implements java.io.Serializable {
    private long logID;

    private String userID;

    private String userName;

    private String departmentName;

    private java.util.Calendar timestamp;

    private EventType trigger;

    private String terminalSN;

    private String terminalName;

    private byte[] jpegPhoto;

    private String remark;

    private String accessMethod;

    public LogDetail() {
    }

    public LogDetail(
           long logID,
           String userID,
           String userName,
           String departmentName,
           java.util.Calendar timestamp,
           EventType trigger,
           String terminalSN,
           String terminalName,
           byte[] jpegPhoto,
           String remark,
           String accessMethod) {
           this.logID = logID;
           this.userID = userID;
           this.userName = userName;
           this.departmentName = departmentName;
           this.timestamp = timestamp;
           this.trigger = trigger;
           this.terminalSN = terminalSN;
           this.terminalName = terminalName;
           this.jpegPhoto = jpegPhoto;
           this.remark = remark;
           this.accessMethod = accessMethod;
    }


    /**
     * Gets the logID value for this LogDetail.
     * 
     * @return logID
     */
    public long getLogID() {
        return logID;
    }


    /**
     * Sets the logID value for this LogDetail.
     * 
     * @param logID
     */
    public void setLogID(long logID) {
        this.logID = logID;
    }


    /**
     * Gets the userID value for this LogDetail.
     * 
     * @return userID
     */
    public String getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this LogDetail.
     * 
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }


    /**
     * Gets the userName value for this LogDetail.
     * 
     * @return userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * Sets the userName value for this LogDetail.
     * 
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }


    /**
     * Gets the departmentName value for this LogDetail.
     * 
     * @return departmentName
     */
    public String getDepartmentName() {
        return departmentName;
    }


    /**
     * Sets the departmentName value for this LogDetail.
     * 
     * @param departmentName
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }


    /**
     * Gets the timestamp value for this LogDetail.
     * 
     * @return timestamp
     */
    public java.util.Calendar getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this LogDetail.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.util.Calendar timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the trigger value for this LogDetail.
     * 
     * @return trigger
     */
    public EventType getTrigger() {
        return trigger;
    }


    /**
     * Sets the trigger value for this LogDetail.
     * 
     * @param trigger
     */
    public void setTrigger(EventType trigger) {
        this.trigger = trigger;
    }


    /**
     * Gets the terminalSN value for this LogDetail.
     * 
     * @return terminalSN
     */
    public String getTerminalSN() {
        return terminalSN;
    }


    /**
     * Sets the terminalSN value for this LogDetail.
     * 
     * @param terminalSN
     */
    public void setTerminalSN(String terminalSN) {
        this.terminalSN = terminalSN;
    }


    /**
     * Gets the terminalName value for this LogDetail.
     * 
     * @return terminalName
     */
    public String getTerminalName() {
        return terminalName;
    }


    /**
     * Sets the terminalName value for this LogDetail.
     * 
     * @param terminalName
     */
    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }


    /**
     * Gets the jpegPhoto value for this LogDetail.
     * 
     * @return jpegPhoto
     */
    public byte[] getJpegPhoto() {
        return jpegPhoto;
    }


    /**
     * Sets the jpegPhoto value for this LogDetail.
     * 
     * @param jpegPhoto
     */
    public void setJpegPhoto(byte[] jpegPhoto) {
        this.jpegPhoto = jpegPhoto;
    }


    /**
     * Gets the remark value for this LogDetail.
     * 
     * @return remark
     */
    public String getRemark() {
        return remark;
    }


    /**
     * Sets the remark value for this LogDetail.
     * 
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }


    /**
     * Gets the accessMethod value for this LogDetail.
     * 
     * @return accessMethod
     */
    public String getAccessMethod() {
        return accessMethod;
    }


    /**
     * Sets the accessMethod value for this LogDetail.
     * 
     * @param accessMethod
     */
    public void setAccessMethod(String accessMethod) {
        this.accessMethod = accessMethod;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof LogDetail)) return false;
        LogDetail other = (LogDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.logID == other.getLogID() &&
            ((this.userID==null && other.getUserID()==null) || 
             (this.userID!=null &&
              this.userID.equals(other.getUserID()))) &&
            ((this.userName==null && other.getUserName()==null) || 
             (this.userName!=null &&
              this.userName.equals(other.getUserName()))) &&
            ((this.departmentName==null && other.getDepartmentName()==null) || 
             (this.departmentName!=null &&
              this.departmentName.equals(other.getDepartmentName()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            ((this.trigger==null && other.getTrigger()==null) || 
             (this.trigger!=null &&
              this.trigger.equals(other.getTrigger()))) &&
            ((this.terminalSN==null && other.getTerminalSN()==null) || 
             (this.terminalSN!=null &&
              this.terminalSN.equals(other.getTerminalSN()))) &&
            ((this.terminalName==null && other.getTerminalName()==null) || 
             (this.terminalName!=null &&
              this.terminalName.equals(other.getTerminalName()))) &&
            ((this.jpegPhoto==null && other.getJpegPhoto()==null) || 
             (this.jpegPhoto!=null &&
              java.util.Arrays.equals(this.jpegPhoto, other.getJpegPhoto()))) &&
            ((this.remark==null && other.getRemark()==null) || 
             (this.remark!=null &&
              this.remark.equals(other.getRemark()))) &&
            ((this.accessMethod==null && other.getAccessMethod()==null) || 
             (this.accessMethod!=null &&
              this.accessMethod.equals(other.getAccessMethod())));
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
        _hashCode += new Long(getLogID()).hashCode();
        if (getUserID() != null) {
            _hashCode += getUserID().hashCode();
        }
        if (getUserName() != null) {
            _hashCode += getUserName().hashCode();
        }
        if (getDepartmentName() != null) {
            _hashCode += getDepartmentName().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getTrigger() != null) {
            _hashCode += getTrigger().hashCode();
        }
        if (getTerminalSN() != null) {
            _hashCode += getTerminalSN().hashCode();
        }
        if (getTerminalName() != null) {
            _hashCode += getTerminalName().hashCode();
        }
        if (getJpegPhoto() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getJpegPhoto());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getJpegPhoto(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRemark() != null) {
            _hashCode += getRemark().hashCode();
        }
        if (getAccessMethod() != null) {
            _hashCode += getAccessMethod().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(LogDetail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "LogDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "logID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departmentName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "departmentName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trigger");
        elemField.setXmlName(new javax.xml.namespace.QName("", "trigger"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.xsd", "eventType"));
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
        elemField.setFieldName("terminalName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "terminalName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jpegPhoto");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jpegPhoto"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remark");
        elemField.setXmlName(new javax.xml.namespace.QName("", "remark"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accessMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accessMethod"));
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
