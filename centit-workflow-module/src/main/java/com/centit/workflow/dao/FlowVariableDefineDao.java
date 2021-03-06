package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.FlowVariableDefine;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017-10-9.
 */
@Repository
public class FlowVariableDefineDao extends BaseDaoImpl<FlowVariableDefine,Long> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public Long getNextFlowVariableId(){
        return DatabaseOptUtils.getSequenceNextValue(this,"S_OPTVARIABLE");
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public void saveNewObject(FlowVariableDefine o) {
        if(o.getFlowVariableId() == null || "".equals(o.getFlowVariableId())){
            o.setFlowVariableId(getNextFlowVariableId());
        }
        super.saveNewObject(o);
    }

    @Transactional(propagation= Propagation.MANDATORY)
    public List<FlowVariableDefine> getFlowVariableByFlowCode(String flowCode){
        return this.listObjectsByFilter("where flow_code = ?",new Object[]{flowCode});
    }
}
