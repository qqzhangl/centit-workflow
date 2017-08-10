package com.centit.workflow.controller;

import com.alibaba.fastjson.JSON;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.workflow.po.*;
import com.centit.workflow.service.ApprovalService;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by chen_rj on 2017/8/3.
 */
@Controller
@RequestMapping("/approval")
public class ApprovalController {
    @Resource
    private FlowEngine flowEngine;
    @Resource
    private ApprovalService approvalService;
    @Resource
    private PlatformEnvironment platformEnvironment;
    @Resource
    private FlowManager flowManager;
    @RequestMapping(value = "/listAllUser", method = RequestMethod.GET)
    public void listAllUser(HttpServletResponse response){
        Object userList = platformEnvironment.listAllUsers();
        JsonResultUtils.writeSingleDataJson(userList,response);
    }
    @RequestMapping("/startProcess")
    public void startProcess(HttpServletRequest request,HttpServletResponse response,String approvalTitle,
                             String approvalDesc,String approvalAuditors){
        //设置审核人  申请事项等业务数据
        List<ApprovalAuditor> auditors = JSON.parseArray(approvalAuditors,ApprovalAuditor.class);
        if(auditors == null || auditors.size() == 0){
            JsonResultUtils.writeBlankJson(response);
            return;
        }
        ApprovalEvent approvalEvent = new ApprovalEvent();
        approvalEvent.setEventTitle(approvalTitle);
        approvalEvent.setEventDesc(approvalDesc);
        approvalEvent.setRequestTime(new Date());
        approvalEvent.setApprovalState("A");
        approvalEvent.setCurrentPhase("0");
        //计算总 审批节点数
        Set<String> set = new HashSet<>();
        for(ApprovalAuditor approvalAuditor:auditors){
            set.add(approvalAuditor.getPhaseNo());
        }
        int phaseCount = set.size();
        //flowCode,userCode
        //开启工作流 提交申请节点
        Long flowInstId = approvalService.startProcess(request,approvalEvent,auditors,phaseCount,
                auditors.get(0).getUserCode());
        List<NodeInstance> nodeInstances = flowManager.listFlowInstNodes(flowInstId);
        if(nodeInstances != null && nodeInstances.size()>0){
            flowEngine.submitOpt(nodeInstances.get(0).getNodeInstId(),"u0000000","",null,
                    request.getServletContext());
        }
        JsonResultUtils.writeBlankJson(response);
    }
    @RequestMapping("/doApproval")
    public void doApproval(HttpServletRequest request, HttpServletResponse response,Long flowInstId,Long nodeInstId,
                           String userCodes,String eventTitle,String eventDesc, String pass,String optUserCode){
        ApprovalEvent approvalEvent = new ApprovalEvent();
        approvalEvent.setEventTitle(eventTitle);
        approvalEvent.setEventDesc(eventDesc);
        approvalEvent.setRequestTime(new Date());
        approvalEvent.setApprovalState("A");
        //将前台传过来的usercode 字符串 拆分
        List<String> userCodeList  = new ArrayList<>();
        if(userCodes != null && userCodes.trim().length() > 0){
            String[] arr = userCodes.split(";");
            for(int i=0;i<arr.length;i++){
                if(!"".equals(arr[i].trim())){
                    userCodeList.add(arr[i]);
                }
            }
        }
        ApprovalProcess approvalProcess = new ApprovalProcess();
        approvalProcess.setAuditResult("0".equals(pass)?"Y":"N");
        approvalProcess.setUserCode("u0000000");
        //flowEngine.saveFlowVariable(flowInstId,"pass","0");
        approvalService.doApproval(approvalEvent,userCodeList,approvalProcess,flowInstId,nodeInstId,optUserCode,request.getServletContext());
        JsonResultUtils.writeBlankJson(response);
    }
}