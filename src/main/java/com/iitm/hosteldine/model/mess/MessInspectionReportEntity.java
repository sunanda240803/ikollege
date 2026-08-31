package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"mess_inspection_report\"", schema = ModelConstants.SCHEMA)
public class MessInspectionReportEntity extends CommonEntity {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Integer id;

		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "mess_master_id", referencedColumnName = "mess_master_id")
		private MessMasterEntity messMaster;

		@Column(name = "warden_id")
		private Integer wardenId;

		@Column(name = "cleanliness_kitchen")
		private String cleanlinessKitchen;

		@Column(name = "cleanliness_plate")
		private String cleanlinessPlate;

		@Column(name = "queue_maintainance")
		private String queueMaintainance;

		@Column(name = "hygiene_mess")
		private String hygieneMess;

		@Column(name = "availability_food")
		private String availabilityFood;

		@Column(name = "feedback_student")
		private String feedbackStudent;

		@Column(name = "other_item")
		private String otherItem;

		@Column(name = "date")
		private LocalDate date;

		@Column(name = "warden_name")
		private String wardenName;

		@Column(name = "file_name")
		private String fileName;
}