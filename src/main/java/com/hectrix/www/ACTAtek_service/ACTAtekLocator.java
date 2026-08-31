/**
 * ACTAtekLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_service;

public class ACTAtekLocator extends org.apache.axis.client.Service implements ACTAtek {

/**
 * WebService API for ACTAtek
 */

    public ACTAtekLocator() {
    }


    public ACTAtekLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ACTAtekLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ACTAtek
    private String ACTAtek_address = "http://www.hectrix.com/ACTAtek.wsdl";

    public String getACTAtekAddress() {
        return ACTAtek_address;
    }

    // The WSDD service name defaults to the port name.
    private String ACTAtekWSDDServiceName = "ACTAtek";

    public String getACTAtekWSDDServiceName() {
        return ACTAtekWSDDServiceName;
    }

    public void setACTAtekWSDDServiceName(String name) {
        ACTAtekWSDDServiceName = name;
    }

    public ACTAtekPortType getACTAtek() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ACTAtek_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getACTAtek(endpoint);
    }

    public ACTAtekPortType getACTAtek(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ACTAtekStub _stub = new ACTAtekStub(portAddress, this);
            _stub.setPortName(getACTAtekWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setACTAtekEndpointAddress(String address) {
        ACTAtek_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ACTAtekPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ACTAtekStub _stub = new ACTAtekStub(new java.net.URL(ACTAtek_address), this);
                _stub.setPortName(getACTAtekWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("ACTAtek".equals(inputPortName)) {
            return getACTAtek();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.service", "ACTAtek");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.hectrix.com/ACTAtek.service", "ACTAtek"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("ACTAtek".equals(portName)) {
            setACTAtekEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
