package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_COUPON_USER_MAPPING\"", schema = "schooldev")
public class MessCouponUserMappingEntity extends CommonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name ="mess_id",nullable = false)
  private MessMasterEntity messMaster;

  @Column(name = "user_name", nullable = false)
  private String userName;

}