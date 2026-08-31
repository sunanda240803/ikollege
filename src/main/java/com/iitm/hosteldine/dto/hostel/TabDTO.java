package com.iitm.hosteldine.dto.hostel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
public class TabDTO {
    private Long l1Id;
    private Long l2Id;
    private String l1Name;
    private String l2Name;
    private String property;
    private String action;
    private Boolean l1show;
    private Boolean l2show;
    private Boolean sort;
    private Boolean mandatory;
    private List<TabDTO> subList;

    public TabDTO(Long l1Id, Long l2Id, String l1Name, String l2Name, String property, String action, Boolean show, Boolean sort, Boolean mandatory) {
        this.l1Id = l1Id;
        this.l2Id = l2Id;
        this.l1Name = l1Name;
        this.l2Name = l2Name;
        this.property = property;
        this.action = action;
        this.l1show = true;
        this.l2show = show;
        this.sort = sort;
        this.mandatory = mandatory;
    }
}
