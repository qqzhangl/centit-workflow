package com.centit.workflow.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.*;
import com.centit.workflow.po.*;
import com.centit.workflow.po.ActionLog;
import com.centit.workflow.po.ActionTask;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.ManageActionLog;
import com.centit.workflow.po.NodeInstance;
import com.centit.workflow.po.StageInstance;
import com.centit.workflow.po.UserTask;

/**
 * 流程管理业务接口类
 *   <流程终止，暂停，唤醒，回退等操作>
 * @author codefan@sina.com
 * @version 2.0 <br>
 *
 */
public interface FlowManager{
    //------------查看流程运行状态图---------------------------------
    /**
     *  获取系统中所有的流程实例
     * @param filterMap 过滤条件
     * @param pageDesc 分页描述
     * @return
   */
   JSONArray listFlowInstance(Map<String, Object> filterMap, PageDesc pageDesc);

    /**
     * 根据 示例flowOptTag获得实例
     * @param flowOptTag
     * @return
     */
    //FlowInstance getFlowInstance(String flowOptTag );


    /**
     * 根据 示例ID获得实例
     * @param flowInstId
     * @return
     */
    FlowInstance getFlowInstance(long flowInstId );

    /**
     * 查看工作流程实例状态或进度
     * @param flowInstId
     * @return XML 描述的流程流转状态图
     */
    String viewFlowInstance(long flowInstId);
    /**
     * 查看工作流程节点示例图
     * @param flowInstId
     * @return
     */
    String viewFlowNodeInstance(long flowInstId);
    //流程实例管理-------------------------------
    //-----------流程状态变更-----N 正常  C 完成   P 暂停 挂起     F 强行结束---------------------------
    /**
     * 终止一个流程  F 强行结束
     */
    int stopInstance(long flowInstId,String mangerUserCode,String admindesc);


    /**
     * 终止一个流程
     * 修改其流程id为负数 ? FIXME : 为什么要更新为负数？什么目的？
     * 更新所有节点状态为F
     * F 强行结束
     */
    int stopAndChangeInstance(long flowInstId,String mangerUserCode,String admindesc);

    /**
     * 暂停一个流程    P 暂停 挂起
     */
    int suspendInstance(long flowInstId,String mangerUserCode,String admindesc);

    /**
     * 激活一个 挂起的或者无效的流程  N 正常
     */
    int activizeInstance(long flowInstId,String mangerUserCode,String admindesc);

    /**
     * 查询某人暂停计时的流程
     * @param userCode
     * @param pageDesc
     * @return
     */
    List<FlowInstance> listPauseTimerFlowInst(String userCode,PageDesc pageDesc);

    /**
     * 暂停流程计时
     * @param flowInstId
     * @param mangerUserCode
     */
    int suspendFlowInstTimer(long flowInstId,String mangerUserCode);


    /**
     * 唤醒流程计时
     * @param flowInstId
     * @param mangerUserCode
     */
    int activizeFlowInstTimer(long flowInstId,String mangerUserCode);

    /**
     * 设置流程期限
     * @param flowInstId  流程实例编号
     * @param timeLimit   新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @param admindesc  流程期限更改原因说明
     * @return
     */
    long resetFlowTimelimt(long flowInstId,String timeLimit,String mangerUserCode,String admindesc);

    //------------流程属性修改----------------------------------
     /**
     * 更改流程所属机构
     * @param flowInstId 流程实例ID
     * @param unitCode 机构代码
     */
    void updateFlowInstUnit(long flowInstId,String unitCode,String optUserCode);


    //------日志信息查看-----------------
    /**
     * 查询流程实例管理日志
     * @param flowInstId
     * @return
     */
    List<ManageActionLog> listManageActionLog(long flowInstId, PageDesc pageDesc);


    //节点实例管理-------------------------------

    /**
     * 获取流程实例下的节点实例列表
     * @param wfinstid 流程实例编号
     * @return List<NodeInstance>
     */
    List<NodeInstance> listFlowInstNodes(long wfinstid);
    //-----------节点状态变更-------------------------------
    /*
     * N 正常  B 已回退    C 完成   F被强制结束
     * P 暂停   W 等待子流程返回   S 等等前置节点（可能是多个）完成
     */
    /**
     * 暂停流程的一个节点  P 暂停
     */
    long suspendNodeInstance(long nodeInstId,String mangerUserCode);

      /**
     * 使流程的 挂起和失效的节点 正常运行 N 正常
     */
    long activizeNodeInstance(long nodeInstId,String mangerUserCode);

    /**
     * 强制流转到下一结点，这个好像不好搞，主要是无法获得业务数据，只能提交没有分支的节点
     */
    long forceCommit(long nodeInstId,String mangerUserCode);


   /**
    * 查询某人暂定计时的节点
    * @param userCode
    * @param pageDesc
    * @return
    */
   List<NodeInstance> listPauseTimerNodeInst(String userCode,PageDesc pageDesc);

    /**
     * 暂停节点定时
     * @param nodeInstId
     * @param mangerUserCode
     */
    int suspendNodeInstTimer(long nodeInstId,String mangerUserCode);


    /**
     * 唤醒节点定时
     * @param nodeInstId
     * @param mangerUserCode
     */
    int activizeNodeInstTimer(long nodeInstId,String mangerUserCode);


    //----------流程分支管理-------------------------------
    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     */
    long resetFlowToThisNode(long nodeInstId,String mangerUserCode);
    /**
     * 强制一个并行分支的节点为游离状态，在提交其他并行分支前调用
     */
    long forceDissociateRuning(long nodeInstId,String mangerUserCode);


    //------------节点属性修改----------------------------------
    /**
     * 更改节点所属机构
     * @param nodeInstId 节点实例ID
     * @param unitCode 机构代码
     */
    void updateNodeInstUnit(long nodeInstId,String unitCode,String optUserCode);


    /**
     * 更改节点的角色信息
     */
    void updateNodeRoleInfo(long nodeInstId, String roleType,String roleCode, String mangerUserCode);

    /**
     * 设置流程期限
     * @param nodeInstId  流程节点实例编号
     * @param timeLimit   新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @return
     */
    long resetNodeTimelimt(long nodeInstId,String timeLimit,String mangerUserCode);


    //----------节点任务管理--------------------------------
    /**
     * 根据节点ID查询能够操作该节点的所有人员，如果为空，则需要分配工作任务单
     * 这个是返回所有能够操作本节点的人员
     */
    List<UserTask> listNodeTasks(long nodeInstId);


    /**
     * 获取节点实例的任务列表，这个返回在actionTask中手动分配的工作人员
     * @param nodeinstid
     * @return List<ActionTask>
     */
    List<ActionTask> listNodeActionTasks(long nodeinstid);





    //------------流程阶段管理-------------------------

    /**
     * 获取节点所在阶段信息
     * @param flowInstId
     * @return
     */
    List<StageInstance> listStageInstByFlowInstId(long flowInstId);


    /**
     * 设置流程期限
     * @param flowInstId  流程实例编号
     * @param timeLimit   新的流程期限 5D3h
     * @param mangerUserCode 管理人员代码
     * @param admindesc  流程期限更改原因说明
     * @return
     */
    long resetStageTimelimt(long flowInstId,long stageId,
            String timeLimit,String mangerUserCode,String admindesc);

    //------------流程角色管理-----接口参见flowEngine--------------------
    //------------流程机构管理------接口参见flowEngine--------------------
    //------------流程变量管理------接口参见flowEngine---------------------
    //------------流程预警管理------接口参见flowEngine---------------------
    //------查看节点信息-------------------------

    /**
     * 获取节点实例的操作日志列表
     * @param nodeinstid
     * @return List<WfActionLog>
     */
    List<ActionLog> listNodeActionLogs(long nodeinstid);


    /**
     * 获取节点实例的操作日志列表
     * @param flowInstId
     * @return List<WfActionLog>
     */
    List<ActionLog> listFlowActionLogs(long flowInstId);
    /**
     * 获取用户所有的操作记录
     * @param userCode
     * @param pageDesc 和分页机制结合
     * @param lastTime if null return all
     * @return
     */
    List<ActionLog> listUserActionLogs(String userCode,PageDesc pageDesc ,Date lastTime );


    /**
     * 查询委托别人做的工作记录
     * @param userCode
     * @return
     */
    List<ActionLog> listGrantorActionLog(String userCode,PageDesc pageDesc);

    /**
     * 查询受委托的工作记录
     * @param userCode
     * @return
     */
    List<ActionLog> listGrantdedActionLog(String userCode,PageDesc pageDesc);

    /**
     *  获取用户参与 流程实例 按照时间倒序排列
     * @param userCode 用户代码
     * @param pageDesc 分页描述
     * @return
     */
    List<FlowInstance> listUserAttachFlowInstance(String userCode, String flowPhase, Map<String, Object> filterMap, PageDesc pageDesc);


    // ---------节点异常管理--------------------
    /**
     * 查找所有没有操作用户的节点
     * @return List<NodeInstance>
     */
    List<NodeInstance> listNodesWithoutOpt();


   /**
    * @param nodeInstId
    * @param userCode
    * @param mangerUserCode
    * @param expiretime
    * @param authDesc
    * @Author:chen_rj
    * @Description:分配任务
    * @Date:8:54 2017/7/14
    */
    public long assignTask(long nodeInstId, String userCode,
                          String mangerUserCode, Date expiretime, String authDesc);

   /**
    * @param nodeInstId
    * @Author:chen_rj
    * @Description:获取节点任务
    * @Date:8:51 2017/7/14
    */
    public List<ActionTask> listNodeInstTasks(Long nodeInstId);

   /**
    * @param taskInstId
    * @param mangerUserCode
    * @Author:chen_rj
    * @Description:任务失效
    * @Date:8:52 2017/7/14
    */
    public int disableTask(long taskInstId, String mangerUserCode);

 /**
  * 删除任务
  * @param taskInstId
  * @param mangerUserCode
  * @return
  */
 public int deleteTask(long taskInstId, String mangerUserCode);
    // ---------节点任务委托--------------------


   /**
    *
    * @param relegateno
    * @return
    */
     public RoleRelegate getRoleRelegateById(Long relegateno);
    /**
     *
     * @param roleRelegate
     */
    void saveRoleRelegate(RoleRelegate roleRelegate);

    /**
     * 查询别人委托给我的
     * @param userCode
     * @return
     */
    List<RoleRelegate> listRoleRelegateByUser(String userCode);

    /**
     * 查询我委托给别人的
     * @param grantor
     * @return
     */
    List<RoleRelegate> listRoleRelegateByGrantor(String grantor);

    /**
     *
     * @param relegateno
     */
    void deleteRoleRelegate(Long relegateno);
    // ---------节点任务迁移--------------------
    /**
     *
     * 迁移任务
     * @param fromUserCode
     * @return 返回迁移的任务数
     */
    int moveUserTaskTo(String fromUserCode,String toUserCode,
                        String optUserCode,String moveDesc);
  }
