package com.iitm.hosteldine.service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SimsConfigDataService {

    public static final String TEMP_FILE_LOCATION = "TEMP_FILE_LOCATION";
    public static final String UPLOAD_FILE_LOCATION = "UPLOAD_FILE_LOCATION";
    public static final String STUDENT_COMPLAINT_PATH = "STUDENT_COMPLAINT_PATH";
    public static final String WATERMARK_IMAGE_PATH = "WATERMARK_IMAGE_PATH";
    public static final String SICK_FOOD_REQUEST_MEDICAL_FILES_PATH="SICK_FOOD_REQUEST_MEDICAL_FILES_PATH";
    public static final String SICK_FOOD_THRESHOLD_TIME= "SICK_FOOD_THRESHOLD_TIME";
    public static final String SICK_FOOD_REQUEST_ADMIN_EMAIL= "SICK_FOOD_REQUEST_ADMIN_EMAIL";
    public static final String CCW_OFFICE_NO = "CCW_OFFICE_NO";
    public static final String MESS_REBATE_DOCUMENT= "MESS_REBATE_DOCUMENT";
    public static final String MESS_REBATE_MINIMUM_FROM_DATE= "MESS_REBATE_MINIMUM_FROM_DATE";
    public static final String MESS_REBATE_MAXIMUM_FROM_DATE= "MESS_REBATE_MAXIMUM_FROM_DATE";
    public static final String MESS_REBATE_MINIMUM_PERIOD= "MESS_REBATE_MINIMUM_PERIOD";
    public static final String MESS_REBATE_MAXIMUM_PERIOD= "MESS_REBATE_MAXIMUM_PERIOD";
    public static final String SICK_FOOD_REQ_MESS_CONTACTS= "SICK_FOOD_REQ_MESS_CONTACTS";
    public static final String REASON_FOR_VACATION= "REASON_FOR_VACATION";
    public static final String HOSTEL_ALLOTMENT_GUI_STATUS= "HOSTEL_ALLOTMENT_GUI_STATUS";
    public static final String ALL_HOSTEL_VIEW_ROLES= "ALL_HOSTEL_VIEW_ROLES";
    public static final String APPROVAL_STATUS= "APPROVAL_STATUS";
    public static final String DONATION_TYPE= "DONATION_TYPE";
    public static final String RESEND_MAIL = "RESEND_MAIL";
    public static final String NATURE_OF_APPOINTMENT = "NATURE_OF_APPOINTMENT";
    public static final String GUEST_FILE_LOCATION = "GUEST_FILE_LOCATION";
    public static final String BIO_DATA_PARENT_PROOF = "bioDataParentProof";
    public static final String BIO_DATA_PDF_ENABLE = "BIO_DATA_PDF_ENABLE";
    public static final String GUEST_REQUEST_RULES = "GUEST_REQUEST_RULES";
    public static final String GUEST_ACCOMODATION_STAY_MAX_TO_DATE = "GUEST_ACCOMODATION_STAY_MAX_TO_DATE";
	public static final String DEFAULT_FILE_SIZE = "DEFAULT_FILE_SIZE";
	public static final String HOSTEL_ACCOMMODATION = "HOSTEL_ACCOMMODATION";
	public static final String LOGO= "LOGO";
	public static final String WORKFLOW_MASTER_CATEGORY= "WORKFLOW_MASTER_CATEGORY";
	public static final String WORKFLOW_MASTER_AUTHORITY_TYPE= "WORKFLOW_MASTER_AUTHORITY_TYPE";
    public static final String STUDENT_MAIL_SUFFIX= "STUDENT_EMAIL_SUFFIX";
	public static final String COMMON_SEARCH_FIELD= "COMMON_SEARCH_FIELD";
    public static final String CANDIDATE_FILE = "CANDIDATE_FILE";
    public static final String ACCOMMODATION_REQUEST_MAIL_TEMPLATE = "ACCOMMODATION_REQUEST_MAIL_TEMPLATE";
	public static final String MAIL_FROM = "MAIL_FROM";
    public static final String COUPON_STATUS = "COUPON_STATUS";
    public static final String TIME_DIFF = "1"; //hr
    public static final String GUIDE_EMAIL = "GUIDE_EMAIL";
    public static final String GUEST_MAILVIEW_MAX_TODATE = "GUEST_MAILVIEW_MAX_TODATE";
    public static final String MESS_TO_CARD_REQUEST_MAIL_TEMPLATE = "MESS_TO_CARD_REQUEST_MAIL_TEMPLATE";
    public static final String IKOLLEGE_FA_INTEGRATION = "iKollege_fa_integration";
    public static final String FA_TRANSACTION_URL = "fa_transaction_api_url";
    public static final String WELLNESS_ROLES = "WELLNESS_ROLES";
	public static final String REFERRAL_TYPE = "REFERRAL_TYPE";
	public static final String TYPE_OF_CONCERN = "TYPE_OF_CONCERN";
	public static final String WELLNESS_EXCEL_CREDENTIALS = "WELLNESS_EXCEL_CREDENTIALS";
	public static final String SELF_HARM_TYPE = "SELF_HARM_TYPE";
	public static final String INTERACTION_MODE = "INTERACTION_MODE";
	public static final String VISIT_STATUS = "VISIT_STATUS";
    public static final String NEED_ID_PROOF = "NEED_ID_PROOF";
    public static final String VALIDATION_STATUS = "VALIDATION_STATUS";
    public static final String VALIDATION_STATUS_CCW_OFFICE = "VALIDATION_STATUS_CCW_OFFICE";
    public static final String VALIDATION_STATUS_HOSTEL_CHECK_IN = "VALIDATION_STATUS_HOSTEL_CHECK_IN";
    public static final String DEAN_HOSTEL_ENROLLMENT_MAIL_TEMPLATE = "DEAN_HOSTEL_ENROLLMENT_MAIL_TEMPLATE";

    public static final String COUPON_CATEGORY = "COUPON_CATEGORY";
    public static final String COUPON_PAYMENT_STATUS = "COUPON_PAYMENT_STATUS";
    public static final String CONVOCATION_PAYMENT_STATUS = "CONVOCATION_PAYMENT_STATUS";
	public static final String COUPON_MAIL_ATTACHEMENTS = "COUPON_MAIL_ATTACHEMENTS";
	public static final String STUDENT_MAIL_ID = "STUDENT_MAIL_ID";
	public static final String SOFTWARE_ADMIN = "SOFTWARE_ADMIN";
	public static final String ONLINE_COUPON_RULES = "ONLINE_COUPON_RULES";
	public static final String ONLINE_MESSS_COUPON_VEG_MESSIDS = "ONLINE_MESSS_COUPON_VEG_MESSIDS";
	public static final String ONLINE_MESSS_COUPON_NONVEG_MESSIDS = "ONLINE_MESSS_COUPON_NONVEG_MESSIDS";
	public static final String ONLINE_COUPON_VEG_DISCOUNTED_AMOUNT = "ONLINE_COUPON_VEG_DISCOUNTED_AMOUNT";
	public static final String ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT = "ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT";
	public static final String ONLINE_PENDING_EXPIRED_DURATION = "ONLINE_PENDING_EXPIRED_DURATION";
	public static final String ONLINE_MESSS_COUPON_RETRY_COUNT = "ONLINE_MESSS_COUPON_RETRY_COUNT";
	public static final String ONLINE_MESSS_COUPON_PENDING_STATUS = "ONLINE_MESSS_COUPON_PENDING_STATUS";
	public static final String ONLINE_COUPON_VEG_MAX_DAYS = "ONLINE_COUPON_VEG_MAX_DAYS";
	public static final String ONLINE_COUPON_NONVEG_MAX_DAYS = "ONLINE_COUPON_NONVEG_MAX_DAYS";
	public static final String ONLINE_COUPON_CUTOFF_TIME = "ONLINE_COUPON_CUTOFF_TIME";
    public static final String HOSTEL_ALLOTMENT_MAIL = "Hostel_allotment_mail";
	public static final String PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final String PUBLIC_API = "PUBLIC_API";
    public static final String FACULTY_APPROVAL_MAIL_TO="FACULTY_APPROVAL_MAIL_TO";
    public static final String APPROVAL_MAIL_TO = "APPROVAL_MAIL_TO";
	public static final String PROOF_TYPES = "PROOF_TYPES";
	public static final String PAINTING_TYPE= "PAINTING_TYPE";
	public static final String ALLOCATION_TYPE= "ALLOCATION_TYPE";
	public static final String REGULAR_CHECKIN_USER_LIST= "REGULAR_CHECKIN_USER_LIST";
	public static final String REGULAR_CHECKIN_ALLOWED_ROLES = "REGULAR_CHECKIN_ALLOWED_ROLES";
    public static final String CANDIDATE_POST = "CANDIDATE_POST";
    public static final String CARD_CHARGES = "Card_Charge";
    public static final String ADMIN_CONTROL_ROLE_NAME = "ADMIN_CONTROL_ROLE_NAME";
    
    public static final String FA_OHM_CREDENTIALS = "fa_ohm_credential";
    public static final String FILE_PATH_WARDEN_IMAGE = "FILE_PATH_WARDEN_IMAGE";
    public static final String FOOD_COURT = "FOOD_COURT";
    
    public static final String LATE_NIGHT_TIMINGS = "LATE_NIGHT_TIMINGS";
    public static final String MESS_TOKEN_TIME_LIMIT = "MESS_TOKEN_TIME_LIMIT";
    public static final String FOOD_COURT_MESS_IDS = "FOOD_COURT_MESS_IDS";

    public static final String APPLICATION_TYPE = "APPLICATION_TYPE";

    public static final String HOSTEL_NIGHT_MAX_COUPON = "HOSTEL_NIGHT_MAX_COUPON";
    public static final String HOSTEL_NIGHT_MAIL_CC = "HOSTEL_NIGHT_MAIL_CC";

    public static final String CATERER_STATUS= "CATERER_STATUS";
    public static final String MESS_SESSION= "MESS_SESSION";
    public static final String REPORT_TYPE = "REPORT_TYPE";
	public static final String CONVOCATION_DATES = "CONVOCATION_DATES";
	public static final String CONVOCATION_ACCOMMODATION_RATE = "CONVOCATION_ACCOMMODATION_RATE";
	public static final String CONVOCATION_MESS_COUPON_RATES = "CONVOCATION_MESS_COUPON_RATES";
	public static final String CONVOCATION_NOTES = "CONVOCATION_NOTES";
	public static final String CONVOCATION_ACCOMMODATION_QUESTION = "CONVOCATION_ACCOMMODATION_QUESTION";
	public static final String CONVOCATION_ACCOMMODATION_NOTE = "CONVOCATION_ACCOMMODATION_NOTE";
	public static final String COMPLIMENTARY_COUPONS_DATE = "COMPLIMENTARY_COUPONS_DATE";
	public static final String MENU_TYPE = "MENU_TYPE";
	public static final String PRIORITY_MESS_BAL_CHECK = "PRIORITY_MESS_BAL_CHECK";
    public static final String STUDENT_COMPLAINT_CONFIG = "STUDENT_COMPLAINT_CONFIG";
	public static final String IIT_STUDENT_MAIL_ADD_DOMAIN	 = "IIT_STUDENT_MAIL_ADD_DOMAIN";
	public static final String HDC_COMPLAINT_FILE_PATH	 = "HDC_COMPLAINT_FILE_PATH";
	public static final String BIOMETRIC_ADMIN_USERNAME	 = "BIOMETRIC_ADMIN_USERNAME";
	public static final String BIOMETRIC_ADMIN_PASSWORD	 = "BIOMETRIC_ADMIN_PASSWORD";
	public static final String MESS_INSPECTION_FILE_PATH = "MESS_INSPECTION_FILE_PATH";
    public static final String WARDEN_AWAY_MAIL= "WARDEN_AWAY_MAIL";
    public static final String ACCOMMODATION_CHARGES= "ACCOMMODATION_CHARGES";
    public static final String OTHER_LOGIN_RETRY_COUNT= "OTHER_LOGIN_RETRY_COUNT";
    public static final String IS_IFPP_BUTTON_NEEDED = "IS_IFPP_BUTTON_NEEDED";
    public static final String CONVOCATION_OPEN_LINK = "CONVOCATION_OPEN_LINK";
    public static final String CONVOCATION_APPLIED_VALIDATION_NEEDED = "CONVOCATION_APPLIED_VALIDATION_NEEDED";
    public static final String ONLINE_CRON_JOB_VERSION = "ONLINE_CRON_JOB_VERSION";
    public static final String CONVOCATION_OPEN_LINK_DATE = "CONVOCATION_OPEN_LINK_DATE";
    public static final String CONVOCATION_MAIL_BCC = "CONVOCATION_MAIL_BCC";


    SimsConfigDataRepository simsConfigDataRepository;

    public String getSimConfigValue(String key) {
        return getSimConfigValue(key, null);
    }

    public String getSimConfigValue(String key, String defaultValue) {
        String result = defaultValue;
        Optional<SimsConfigDataEntity> configDataResult = simsConfigDataRepository.findByConfigKeyIgnoreCaseAndActiveFlag(key, ModelConstants.STATUS_ACTIVE);
        if (configDataResult.isPresent()) {
            SimsConfigDataDto simsConfigDataDto = configDataResult.map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity).orElse(null);
            result = simsConfigDataDto.getConfigValue();
        }
        //log.info("{}:{}", key, result);
        return result;
    }

    public ArrayList<String> getSimConfigValueArrayList(String key) {
        ArrayList<String> displayList = null;
        String propertyDisplaysList = getSimConfigValue(key);
        if (propertyDisplaysList != null && !propertyDisplaysList.isEmpty()) {
            displayList = new ArrayList<>(Arrays.stream(propertyDisplaysList.split(",")).toList());
        }
        return displayList;
    }

    public ArrayList<SimsConfigDataJsonArrayDto> getSimConfigValueFromJsonArray(String key) {
        ArrayList<SimsConfigDataJsonArrayDto> resultList = null;
        String jsonConfig = getSimConfigValue(key);
        if (jsonConfig != null) {
            resultList = new ArrayList<>();
            JSONArray jsonConfigArray = new JSONArray(jsonConfig);
            for (int i = 0; i < jsonConfigArray.length(); i++) {
                JSONObject jsonConfigObject = jsonConfigArray.getJSONObject(i);
                // Iterate over each key in the JSON object
                Iterator<String> configKeys = jsonConfigObject.keys();
                while (configKeys.hasNext()) {
                    SimsConfigDataJsonArrayDto dto = new SimsConfigDataJsonArrayDto();
                    String jsonKey = configKeys.next();
                    dto.setId(jsonKey);
                    dto.setValue(jsonConfigObject.getString(jsonKey));
                    resultList.add(dto);
                }
            }
        }
        return resultList;
    }

    @Autowired
    public void setSimsConfigDataRepository(SimsConfigDataRepository simsConfigDataRepository) {
        this.simsConfigDataRepository = simsConfigDataRepository;
    }
    
    public Map<String,String> getSimConfigValueAsMap(String key){
    	Map<String, String> map = new HashMap<>();
    	String propertyDisplaysList = getSimConfigValue(key);
        if (propertyDisplaysList != null && !propertyDisplaysList.isEmpty()) {
        	propertyDisplaysList = propertyDisplaysList.substring(1, propertyDisplaysList.length() - 1); 
            String[] entries = propertyDisplaysList.split(", "); 


            // Populate the map
            for (String entry : entries) {
                String[] keyValue = entry.split("=", 2); // Split by "=" into key and value
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }

    public List<Map<String,String>> getSimConfigValueAsTable(String key){
        List<Map<String,String>> rows = new ArrayList<>();
        ArrayList<SimsConfigDataJsonArrayDto> list = getSimConfigValueFromJsonArray(key);
        if(list == null) return rows;
        Map<String,String> row = new LinkedHashMap<>();
        int columnCount = 0;
        for(SimsConfigDataJsonArrayDto dto : list){
            row.put(dto.getId(), dto.getValue());
            columnCount++;
            if(columnCount == 4){
                rows.add(row);
                row = new LinkedHashMap<>();
                columnCount = 0;
            }
        }
        return rows;
    }

}
