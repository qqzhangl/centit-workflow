package com.centit.workflow.dao;

import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.workflow.po.FlowOptInfo;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by chen_rj on 2018-5-8.
 */
@Repository
public class FlowOptInfoDao extends BaseDaoImpl<FlowOptInfo,String> {
    @Override
    public Map<String, String> getFilterField() {
        return null;
    }

    public String getOptInfoSequenceId() {
        Long optInfoSequenceId = DatabaseOptUtils.getSequenceNextValue(this, "S_WFOPTINFO");
        return String.valueOf(optInfoSequenceId);
    }
}
