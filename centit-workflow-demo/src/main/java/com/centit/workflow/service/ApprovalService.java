package com.centit.workflow.service;

import com.centit.workflow.po.ApprovalAuditor;
import com.centit.workflow.po.ApprovalEvent;
import com.centit.workflow.po.ApprovalProcess;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_rj on 2017/8/3.
 */
public interface ApprovalService {
    /**
     * 发起申请 需要保存申请的内容，设置所有审批人
     * @param approvalEvent
     * @param approvalAuditors
     */
    public Long startProcess(HttpServletRequest request,ApprovalEvent approvalEvent,
                             List<ApprovalAuditor> approvalAuditors, int phaseNO, String userCode);

    /**
     * 审批通过或者不通过  需要保存审批意见，审批结果，如果通过还要设置下一步的审批人
     * @param approvalEvent
     * @param userCodes
     * @param approvalProcess
     */
    public void doApproval(ApprovalEvent approvalEvent, List<String> userCodes, ApprovalProcess approvalProcess,
                           long flowInstId, long nodeInstId,String userCode, ServletContext ctx);
}