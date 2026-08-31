package com.iitm.hosteldine.model.mess;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class MessSessionEntityId implements Serializable {
    private static final long serialVersionUID = 6609077691806063107L;
   
    @Column(name = "mess_id", nullable = false)
    private Long messId;

    @Column(name = "session_name", nullable = false, length = 32)
    private String sessionName;
    
    
 /*   @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_name", nullable = false)
    private MessSessionsMasterEntity session;

    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mess_id", nullable = false)
    private MessMasterEntity messMaster;*/
    
    


}