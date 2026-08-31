package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"PAYMENT_GATEWAY_CONFIGURATION_CCAV\"", schema = ModelConstants.SCHEMA)
@Getter
@Setter
public class PaymentGatewayConfigCcavEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "url", nullable = false, length = 128)
    private String url;

    @Column(name = "access_code", nullable = false, length = 255)
    private String accessCode;

    @Column(name = "working_key", nullable = false, length = 255)
    private String workingKey;

    @Column(name = "instance", nullable = false, length = 32)
    private String instance;

    @Column(name = "action_url", nullable = false)
    private String actionUrl;


}
