package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.FlowTeamRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class FlowTeamRoleDao extends BaseDaoImpl<FlowTeamRole,Long>{
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public Long getNextTeamRoleId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_OPTTEAMROLE");
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(FlowTeamRole o) {
        if(o.getFlowTeamRoleId() == null || "".equals(o.getFlowTeamRoleId())){
            o.setFlowTeamRoleId(getNextTeamRoleId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public Map<String,String> getRoleByFlowCode(String flowCode,Long version){
        Map<String,String> roleMap = new HashMap<>();
        List<FlowTeamRole> flowTeamRoles = this.listObjectsByFilter("where flow_code = ? and version = ?",new Object[]{flowCode,version});
        if(flowTeamRoles == null || flowTeamRoles.size() == 0){
            return roleMap;
        }
        flowTeamRoles.forEach(role->{
            roleMap.put(role.getRoleCode(),role.getRoleName());
        });
        return roleMap;
    }
}
