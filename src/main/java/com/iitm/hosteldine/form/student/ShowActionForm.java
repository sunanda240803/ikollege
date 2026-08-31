package com.iitm.hosteldine.form.student;

import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.dto.hostel.ShowMasterDto;
import lombok.Data;

import java.util.List;

@Data
public class ShowActionForm {
    private Long eventId;
    private ShowEventMasterDto showEventMasterDto;
    private List<ShowMasterDto> showMasterDtoList;
    private double totalCartAmount;
    private String name;
    private String contactNumber;
    private String doorNumber;
    private String street;
    private String city;
    private String district;
    private String state;
    private String pinCode;
    private String deliveryType;
    private double creditLimit;
    private double totalPurchasedAmount;
}
