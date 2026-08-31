package com.iitm.hosteldine.model.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "\"GUEST_FILES_INFORMATION\"", schema = ModelConstants.SCHEMA) 
public class GuestFilesInformationEntity  {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id", nullable = false)
    private Long fileId;
	
	@Column(name = "request_id", nullable = false)
    private Long requestId;
	
    @Column(name = "filename", length = 256)
    private String filename;

    @Column(name = "description", length = 256)
    private String description;
    
    @Column(name = "active_flag", nullable = false, length = 1)
    protected String activeFlag;

}
