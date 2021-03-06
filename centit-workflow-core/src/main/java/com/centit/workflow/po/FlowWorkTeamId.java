package com.centit.workflow.po;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * FAddressBook entity.
 * 
 * @author codefan@hotmail.com
 */ 
@Embeddable
public class FlowWorkTeamId implements java.io.Serializable {
    private static final long serialVersionUID =  1L;

    @Column(name = "FLOW_INST_ID")
    private Long flowInstId;

    @Column(name = "USER_CODE")
    private String userCode;

    @Column(name = "ROLE_CODE")
    private String roleCode;

    // Constructors
    /** default constructor */
    public FlowWorkTeamId() {
    }
    /** full constructor */
    public FlowWorkTeamId(Long wfinstid, String usercode, String rolecode) {

        this.flowInstId = wfinstid;
        this.userCode = usercode;
        this.roleCode = rolecode;
    }

  
    public Long getFlowInstId() {
        return this.flowInstId;
    }

    public void setFlowInstId(Long wfinstid) {
        this.flowInstId = wfinstid;
    }
  
    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String usercode) {
        this.userCode = usercode;
    }
  
    public String getRoleCode() {
        return this.roleCode;
    }

    public void setRoleCode(String rolecode) {
        this.roleCode = rolecode;
    }


    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof FlowWorkTeamId))
            return false;

        FlowWorkTeamId castOther = (FlowWorkTeamId) other;
        boolean ret = true;
  
        ret = ret && ( this.getFlowInstId() == castOther.getFlowInstId() ||
                       (this.getFlowInstId() != null && castOther.getFlowInstId() != null
                               && this.getFlowInstId().equals(castOther.getFlowInstId())));
  
        ret = ret && ( this.getUserCode() == castOther.getUserCode() ||
                       (this.getUserCode() != null && castOther.getUserCode() != null
                               && this.getUserCode().equals(castOther.getUserCode())));
  
        ret = ret && ( this.getRoleCode() == castOther.getRoleCode() ||
                       (this.getRoleCode() != null && castOther.getRoleCode() != null
                               && this.getRoleCode().equals(castOther.getRoleCode())));

        return ret;
    }

    public int hashCode() {
        int result = 17;
  
        result = 37 * result +
             (this.getFlowInstId() == null ? 0 :this.getFlowInstId().hashCode());
  
        result = 37 * result +
             (this.getUserCode() == null ? 0 :this.getUserCode().hashCode());
  
        result = 37 * result +
             (this.getRoleCode() == null ? 0 :this.getRoleCode().hashCode());

        return result;
    }
}
