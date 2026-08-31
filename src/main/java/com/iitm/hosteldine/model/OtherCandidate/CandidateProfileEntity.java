package com.iitm.hosteldine.model.OtherCandidate;

import java.sql.Date;

import org.hibernate.annotations.ColumnDefault;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_PERSONAL_DETAILS\"", schema = ModelConstants.SCHEMA)
public class CandidateProfileEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id", nullable = false)
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "date_of_birth")
    private Date dob;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "address1")
    private String address;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city" , length = 50)
    private String city;
    
    @Column(name = "pin")
    private Integer pin;

    @Column(name = "state", nullable = false, length = 50)
    private String state;
    
    @Column(name = "phone_number", length = 16)
    private String phoneNumber;
    
    @Column(name = "mobile_number", nullable = false, length = 16)
    private String mobileNumber;
    
    @Column(name = "email", nullable = false, length = 256)
    private String email;
    
    @Column(name = "post_select", length = 20)
    private String postSelect;
    
    @Column(name = "post_others", length = 64)
    private String postOthers;

    @ColumnDefault("1")
    @Column(name = "school_id", nullable = false)
    private Integer schoolId;
    
    @Column(name = "employee_id", length = 32)
    private String employeeId;
    
    @Column(name = "designation", length = 32)
    private String designation;
    
    @Column(name = "image_name")
    private String imageName;

    @Transient
    private String candiateFullName;
    
    public String getCandiateFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) {
            fullName.append(firstName);
        }
        if (lastName != null) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName);
        }
        return fullName.toString();
    }
}
