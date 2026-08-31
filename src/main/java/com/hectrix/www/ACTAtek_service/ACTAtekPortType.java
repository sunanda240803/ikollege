/**
 * ACTAtekPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.hectrix.www.ACTAtek_service;

import com.hectrix.www.ACTAtek_xsd.ActatekUser;

public interface ACTAtekPortType extends java.rmi.Remote {

    /**
     * Return sessionID for subsequent calls
     */
    public long login(String username, String password) throws java.rmi.RemoteException;

    /**
     * Invalidate the given sessionID
     */
    public String logout(long sessionID) throws java.rmi.RemoteException;

    /**
     * Get Agent List
     */
    public com.hectrix.www.ACTAtek_xsd.Agent[] getRegisteredAgents(long sessionID) throws java.rmi.RemoteException;

    /**
     * Actively Send Log to Agent, return agent status when done
     */
    public com.hectrix.www.ACTAtek_xsd.Agent pollLog(long sessionID, com.hectrix.www.ACTAtek_xsd.Agent agent) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getLogs
     */
    public com.hectrix.www.ACTAtek_xsd.Log[] getLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria) throws java.rmi.RemoteException;

    /**
     * Add a Log Entry
     */
    public String addLog(long sessionID, java.util.Calendar timestamp, String employeeID, com.hectrix.www.ACTAtek_xsd.EventType event, String terminalSN) throws java.rmi.RemoteException;

    /**
     * Add a User
     */
    public String addUser(long sessionID, ActatekUser actatekUser) throws java.rmi.RemoteException;

    /**
     * Find Users
     */
    public ActatekUser[] getUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetUsersCriteria getUsersCriteria) throws java.rmi.RemoteException;

    /**
     * Change User Information
     */
    public String updateUser(long sessionID, String userID, ActatekUser actatekUser) throws java.rmi.RemoteException;

    /**
     * Delete a given User
     */
    public String deleteUser(long sessionID, String userID) throws java.rmi.RemoteException;

    /**
     * Get current login User
     */
    public ActatekUser getMyself(long sessionID) throws java.rmi.RemoteException;

    /**
     * Update own information
     */
    public String updateMyself(long sessionID, ActatekUser actatekUser) throws java.rmi.RemoteException;

    /**
     * Activate a User
     */
    public String activateUser(long sessionID, String userID) throws java.rmi.RemoteException;

    /**
     * Deactivate a User
     */
    public String deactivateUser(long sessionID, String userID) throws java.rmi.RemoteException;

    /**
     * Get User Message
     */
    public String getUserMessage(long sessionID, String userID) throws java.rmi.RemoteException;

    /**
     * Change User Message
     */
    public String updateUserMessage(long sessionID, String userID, String message) throws java.rmi.RemoteException;

    /**
     * Delete User Message
     */
    public String deleteUserMessage(long sessionID, String userID) throws java.rmi.RemoteException;

    /**
     * Add a Department
     */
    public int addDepartment(long sessionID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException;

    /**
     * Change Department Information
     */
    public String updateDepartment(long sessionID, int departmentID, com.hectrix.www.ACTAtek_xsd.Department department) throws java.rmi.RemoteException;

    /**
     * Get Departments by ID
     */
    public com.hectrix.www.ACTAtek_xsd.Department[] getDepartments(long sessionID, Integer departmentID) throws java.rmi.RemoteException;

    /**
     * Delete Department
     */
    public String deleteDepartment(long sessionID, int departmentID) throws java.rmi.RemoteException;

    /**
     * Add an Access Group
     */
    public int addGroup(long sessionID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getGroups
     */
    public com.hectrix.www.ACTAtek_xsd.Group[] getGroups(long sessionID, com.hectrix.www.ACTAtek_xsd.GetGroupsCriteria getGroupsCriteria) throws java.rmi.RemoteException;

    /**
     * Change Group Information
     */
    public String updateGroup(long sessionID, int groupID, com.hectrix.www.ACTAtek_xsd.Group group) throws java.rmi.RemoteException;

    /**
     * Delete a given Group
     */
    public String deleteGroup(long sessionID, int groupID) throws java.rmi.RemoteException;

    /**
     * Get Trigger by Type
     */
    public com.hectrix.www.ACTAtek_xsd.Trigger[] getTriggers(long sessionID, com.hectrix.www.ACTAtek_xsd.GetTriggersCriteria getTriggersCriteria) throws java.rmi.RemoteException;

    /**
     * Change Trigger Details
     */
    public String updateTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.Trigger trigger) throws java.rmi.RemoteException;

    /**
     * Clear All Trigger Time
     */
    public String clearTrigger(long sessionID, com.hectrix.www.ACTAtek_xsd.TriggerType triggerType) throws java.rmi.RemoteException;

    /**
     * Open the Door Now
     */
    public String openDoor(long sessionID, int doorID) throws java.rmi.RemoteException;

    /**
     * Open the Relay
     */
    public String openRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Close the Relay
     */
    public String closeRelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Register an Agent (IP/PORT)
     */
    public String registerAgent(long sessionID, String ip, int port, String agentVersion, String magic, String registrationID, boolean initSync) throws java.rmi.RemoteException;

    /**
     * Delete Agent (IP/PORT)
     */
    public String unregisterAgent(long sessionID, String ip, int port) throws java.rmi.RemoteException;

    /**
     * Get API Version Number
     */
    public String getAPIVersion() throws java.rmi.RemoteException;

    /**
     * Get Terminal Status
     */
    public com.hectrix.www.ACTAtek_xsd.TerminalStatus getTerminalStatus(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTerminalList
     */
    public com.hectrix.www.ACTAtek_xsd.Terminal[] getTerminalList(long sessionID) throws java.rmi.RemoteException;

    /**
     * Check if the session still valid and update timestamp of sessionID
     */
    public String nop(long sessionID) throws java.rmi.RemoteException;

    /**
     * Get Photo of the related log parameter
     */
    public byte[] getLogPhoto(long sessionID, String terminalSN, java.util.Calendar timestamp) throws java.rmi.RemoteException;

    /**
     * Immediately Register a Fingerprint User using ACTAtek
     */
    public void registerFPUser(long sessionID, String userID, com.hectrix.www.ACTAtek_xsd.holders.FingerprintHolder fingerprint, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getFullLogs
     */
    public com.hectrix.www.ACTAtek_xsd.LogDetail[] getFullLogs(long sessionID, com.hectrix.www.ACTAtek_xsd.GetLogsCriteria getLogsCriteria, Integer limit) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__resetLog
     */
    public String resetLog(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getFpSecurityLevel
     */
    public com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel getFpSecurityLevel(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setFpSecurityLevel
     */
    public String setFpSecurityLevel(long sessionID, com.hectrix.www.ACTAtek_xsd.FingerprintSecurityLevel fpSecurityLevel) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getRelayDelay
     */
    public int getRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setRelayDelay
     */
    public String setRelayDelay(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, int sec) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getAutoInOut
     */
    public com.hectrix.www.ACTAtek_xsd.AutoInOutOption getAutoInOut(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setAutoInOut
     */
    public String setAutoInOut(long sessionID, com.hectrix.www.ACTAtek_xsd.AutoInOutOption autoInOutOption) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getLogUnauthorizedEvent
     */
    public boolean getLogUnauthorizedEvent(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setLogUnauthorizedEvent
     */
    public String setLogUnauthorizedEvent(long sessionID, boolean enable) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setRelayOption
     */
    public String setRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID, com.hectrix.www.ACTAtek_xsd.RelayOption relayOpt) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getRelayOption
     */
    public com.hectrix.www.ACTAtek_xsd.RelayOption getRelayOption(long sessionID, com.hectrix.www.ACTAtek_xsd.RelayID relayID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setSNTP
     */
    public String setSNTP(long sessionID, boolean enable) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getSNTP
     */
    public Boolean getSNTP(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setSNTPServer
     */
    public String setSNTPServer(long sessionID, String serveraddr) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getSNTPServer
     */
    public String getSNTPServer(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTerminalDateTime
     */
    public java.util.Calendar getTerminalDateTime(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setTerminalDateTime
     */
    public String setTerminalDateTime(long sessionID, java.util.Calendar terminalDate) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTimeZone
     */
    public String getTimeZone(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setTimeZone
     */
    public String setTimeZone(long sessionID, String timezone) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getTimeZoneDSTInfo
     */
    public String getTimeZoneDSTInfo(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getCaptureFingerprint
     */
    public byte[] getCaptureFingerprint(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__initCaptureFP
     */
    public void initCaptureFP(long sessionID, javax.xml.rpc.holders.ByteArrayHolder jpegPhoto, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__lock
     */
    public String lock(long sessionID, String deviceNodeDbId) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__unlock
     */
    public String unlock(long sessionID, String deviceNodeDbId) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUsersDb
     */
    public String getUsersDb(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__deleteLogs
     */
    public String deleteLogs(long sessionID, int daysToKeep) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__addUsers
     */
    public com.hectrix.www.ACTAtek_xsd.UserID[] addUsers(long sessionID, ActatekUser[] actatekUsers) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateUsers
     */
    public String updateUsers(long sessionID, com.hectrix.www.ACTAtek_xsd.UserID[] uids, ActatekUser[] actatekUsers) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getJobCodeSettings
     */
    public com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] getJobCodeSettings(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateJobCodeSettings
     */
    public String updateJobCodeSettings(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCodeSettings[] jobcodesettings) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__addJobCodes
     */
    public String addJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__updateJobCodes
     */
    public String updateJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__deleteJobCodes
     */
    public String deleteJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.JobCode[] jobcodes) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getJobCodes
     */
    public com.hectrix.www.ACTAtek_xsd.JobCode[] getJobCodes(long sessionID, com.hectrix.www.ACTAtek_xsd.GetJobCodeCriteria getJobCodeCriteria) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUserCount
     */
    public int getUserCount(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUserBatch
     */
    public ActatekUser[] getUserBatch(long sessionID, int offset, int limit) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getHolidays
     */
    public com.hectrix.www.ACTAtek_xsd.Holiday[] getHolidays(long sessionID, com.hectrix.www.ACTAtek_xsd.Holiday holidayCriteria) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setHolidays
     */
    public String setHolidays(long sessionID, com.hectrix.www.ACTAtek_xsd.Holiday[] setHolidayArray) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__deleteHolidays
     */
    public String deleteHolidays(long sessionID, com.hectrix.www.ACTAtek_xsd.Holiday[] deleteHolidayArray) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getDoorOpenSchedule
     */
    public com.hectrix.www.ACTAtek_xsd.Timespec[] getDoorOpenSchedule(long sessionID) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__setDoorOpenSchedule
     */
    public String setDoorOpenSchedule(long sessionID, com.hectrix.www.ACTAtek_xsd.Timespec[] setDoorOpenScheduleArray) throws java.rmi.RemoteException;

    /**
     * Service definition of function ns__getUserAPITOTP
     */
    public void getUserAPITOTP(long sessionID, com.hectrix.www.ACTAtek_xsd.GetUserAPITOTPCriteria criteria, com.hectrix.www.ACTAtek_xsd.holders.TOTPformatHolder format, javax.xml.rpc.holders.CalendarHolder expiry, javax.xml.rpc.holders.ByteArrayHolder data, javax.xml.rpc.holders.StringHolder status) throws java.rmi.RemoteException;

    public void setFR(boolean isFR);
    public boolean isFR();
}
