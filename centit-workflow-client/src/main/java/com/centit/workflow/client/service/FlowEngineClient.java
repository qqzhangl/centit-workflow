package com.centit.workflow.client.service;


import com.centit.support.database.utils.PageDesc;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.FlowWorkTeam;
import com.centit.workflow.po.UserTask;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程运行接口
 */
public interface FlowEngineClient {

    CloseableHttpClient getHttpClient() throws Exception ;

    void releaseHttpClient(CloseableHttpClient httpClient);

    void setWorkFlowServerUrl(String workFlowServerUrl) ;

    //--------------------创建流程实例接口-----------------------------------
    /**
     * 创建流程实例  返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return  //FIXME ：这个地方应该返回结构化数据
     */
    String createInstance(String flowCode, String flowOptName,
                          String flowOptTag, String userCode, String unitCode) throws Exception;

    /**
     * 创建流程实例  返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @param timeLimitStr 流程计时 默认单位为天，也可以手动设定为d\h\m
     * @return
     */
    String createInstance(String flowCode, String flowOptName, String flowOptTag, String userCode, String unitCode,String timeLimitStr) throws Exception;

    /**
     * 创建流程实例 返回流程实例
     * @param flowCode 流程编码
     * @param version 指定版本号
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return //FIXME ：这个地方应该返回结构化数据
     */
    String createInstance(String flowCode, long version, String flowOptName,
                          String flowOptTag, String userCode, String unitCode) throws Exception;


    /**
     * 创建流程实例并锁定首节点，首节点只能有创建人操作 返回流程实例
     * @param flowCode 流程编码
     * @param flowOptName 这个名称用户 查找流程信息，用来显示业务办件名称，
     * @param flowOptTag  这个标记用户 查找流程信息，比如办件代码，由业务系统自己解释可以用于反向关联
     * @param userCode 创建用户
     * @param unitCode 将流程指定一个所属机构
     * @return FlowInstance 流程实例
     */
    String createInstanceLockFirstNode(String flowCode, String flowOptName,
                                             String flowOptTag, String userCode, String unitCode) throws Exception;

//   //--------------------提交流程业务节点-----------------------------------
    /**
     * 返回下一步节点的节点实例ID
     * @param nodeInstId 当前节点实例编号
     * @param userCode 操作用户编号 对应用户表达式 O operator
     * @param unitCode 用户机构，如果为空系统会自动负责为 操作用户的主机构，机构表达式要为 U
     * @param varTrans 变量转换器
     * @return  节点实例编号列表
     */
    void submitOpt(long nodeInstId, String userCode,
                        String unitCode, String varTrans,
                        ServletContext application) throws  Exception;
    /**
     * 查看某一个用户所有的待办，并且分页
     * @param userCode
     * @param pageDesc
     * @return
     */
    List<UserTask> listUserTasks(String userCode, PageDesc pageDesc);
    /**
     * 根据业务id获取所有该业务下的流程
     * @param optTag
     * @return
     */
    List<FlowInstance> listAllFlowInstByOptTag(String optTag);


    void updateFlowInstOptInfo(long flowInstId, String flowOptName,String flowOptTag);
    /**
     * 分配办件角色，兼容老业务模块，支持新增多个办件角色
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 办件角色 不能为空
     * @param userCodeSet 用户代码列表，添加
     * @return
     */
    void assignFlowWorkTeam(long flowInstId, String roleCode,
                            List<String> userCodeSet) throws Exception;

    /**
     * 新增单个办件角色
     * @param flowInstId
     * @param roleCode
     * @param userCode
     * @throws Exception
     */
    void addFlowWorkTeam(long flowInstId, String roleCode, String userCode) throws Exception ;

    /**
     * 分配流程机构，兼容老业务模块，支持新增多个流程机构
     * @param flowInstId
     * @param roleCode
     * @param orgCodeSet
     * @throws Exception
     */
    void assignFlowOrganize(long flowInstId, String roleCode,
                             List<String> orgCodeSet) throws Exception;

    /**
     * 分配单个流程机构
     * @param flowInstId
     * @param roleCode
     * @param unitCode
     */
    void addFlowOrganize(long flowInstId, String roleCode,
                                String unitCode);


        /**
         * 删除工作小组--办件角色
         * @param flowInstId  流程实例号 不能为空
         * @param roleCode 办件角色 不能为空
         */
    void deleteFlowWorkTeam(long flowInstId, String roleCode) throws Exception;
    /**
     * 查看工作小组中某个角色的成员
     * @param flowInstId 工作流实例号
     * @param roleCode 办件角色代码
     * @return Set<userCode>
     */
    List<String> viewFlowWorkTeam(long flowInstId, String roleCode);
    /**
     * 删除流程机构
     * @param flowInstId  流程实例号 不能为空
     * @param roleCode 机构角色 不能为空
     */
    void deleteFlowOrganize(long flowInstId, String roleCode) throws Exception;
    List<String> viewFlowOrganize(long flowInstId, String roleCode);
    /**
     * 设置流程全局变量
     * @param flowInstId
     * @param sVar
     * @param sValue
     */
    void saveFlowVariable(long flowInstId, String sVar, String sValue) throws Exception;
    /**
     * 查询某个流程变量
     * @param flowInstId
     * @return
     */
    List<FlowVariable> viewFlowVariablesByVarname(long flowInstId, String varname) throws Exception;

}
