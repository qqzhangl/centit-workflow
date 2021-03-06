package com.centit.workflow.external;

import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.impl.AbstractUserUnitFilterCalcContext;
import com.centit.framework.core.dao.ExtendedQueryPool;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.common.CachedMap;
import com.centit.support.common.CachedObject;
import com.centit.support.database.utils.DataSourceDescription;
import com.centit.support.database.utils.DatabaseAccess;
import com.centit.support.database.utils.DbcpConnectPools;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by codefan on 17-9-11.
 * 在这个过滤器中 用户信息只需要用户代码、用户默认机构
 * 机构信息中只需要机构代码、
 * 机构人员信息中需要 用户代码、机构代码、 用户位和职务
 * Rank 获取用户职务等级信息，有一个职务类表
 */
public class JdbcUserUnitFilterCalcContext extends AbstractUserUnitFilterCalcContext {

    @Value("${wf.external.system.jdbc.url:}")
    protected String externalJdbcUrl;
    @Value("${wf.external.system.jdbc.user:}")
    protected String externalJdbcUser;
    @Value("${wf.external.system.jdbc.password:}")
    protected String externalJdbcPassword;

    protected static final Logger logger = LoggerFactory.getLogger(JdbcUserUnitFilterCalcContext.class);

    private CachedObject<List<ExtSysUserInfo>> allUserInfoCache =
        new CachedObject<>( this::reloadUserInfo,
            CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);

    private CachedObject<Map<String,ExtSysUserInfo>> codeToUserMapCache=
        new CachedObject<>( ()-> {
                List<ExtSysUserInfo> userInfos = allUserInfoCache.getCachedTarget();
                if(userInfos==null){
                    return null;
                }
                Map<String, ExtSysUserInfo> userCodeMap = new HashMap<>(userInfos.size()+1);
                for(ExtSysUserInfo userInfo : userInfos ){
                    userCodeMap.put(userInfo.getUserCode(), userInfo);
                }
                return userCodeMap;
            },
            allUserInfoCache);

    private CachedObject<List<ExtSysUnitInfo>> allunitInfoCache=
        new CachedObject<>( this::reloadUnitInfo,
            CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);

    private CachedMap<String, List<ExtSysUnitInfo>> subUnitMapCache=
        new CachedMap<>( this::fetchSubUnit,
            allunitInfoCache);

    private CachedObject<Map<String,ExtSysUnitInfo>> codeToUnitMapCache=
        new CachedObject<>( ()-> {
                List<ExtSysUnitInfo> unitInfos = allunitInfoCache.getCachedTarget();
                if(unitInfos==null){
                    return null;
                }
                Map<String, ExtSysUnitInfo> userCodeMap = new HashMap<>(unitInfos.size()+1);
                for(ExtSysUnitInfo unitInfo : unitInfos ){
                    userCodeMap.put(unitInfo.getUnitCode(), unitInfo);
                }
                return userCodeMap;
            },
            allunitInfoCache);


    private CachedObject<List<ExtSysUserUnit>> allUserUnitCache =
        new CachedObject<>( this::reloadUserUnit,
            CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);

    private CachedMap<String, List<ExtSysUserUnit>> userUnitMapCache=
        new CachedMap<>( (userCode) ->{
                List<ExtSysUserUnit> allUserUnits = allUserUnitCache.getCachedTarget();
                if(allUserUnits==null)
                    return null;
                List<ExtSysUserUnit> userUnits = new ArrayList<>(4);
                for(ExtSysUserUnit uu : allUserUnits){
                    if(StringUtils.equals(userCode, uu.getUserCode())){
                        userUnits.add(uu);
                    }
                }
                return userUnits;
            },
            allUserUnitCache);

    private CachedMap<String, List<ExtSysUserUnit>> unitUserMapCache=
        new CachedMap<>( (unitCode) ->{
                List<ExtSysUserUnit> allUserUnits = allUserUnitCache.getCachedTarget();
                if(allUserUnits==null)
                    return null;
                List<ExtSysUserUnit> unitUsers = new ArrayList<>(20);
                for(ExtSysUserUnit uu : allUserUnits){
                    if(StringUtils.equals(unitCode, uu.getUnitCode())){
                        unitUsers.add(uu);
                    }
                }
                return unitUsers;
            },
            allUserUnitCache);

    private CachedObject<Map<String, Integer>> rankMapCache =
        new CachedObject<>( this::reloadRankInfo,
            CodeRepositoryCache.CACHE_FRESH_PERIOD_SECONDS);



    private ExtSysUnitInfo searchUnitInfoByCode(String unitCode) {
        for(ExtSysUnitInfo unitInfo : allunitInfoCache.getCachedTarget()){
            if(unitInfo.getUnitCode().equals(unitCode))
                return unitInfo;
        }
        return null;
    }

    private ExtSysUserInfo searchUserInfoByCode(String userCode) {
        for(ExtSysUserInfo userInfo : allUserInfoCache.getCachedTarget()){
            if(userInfo.getUserCode().equals(userCode))
                return userInfo;
        }
        return null;
    }


    private Connection getExternalDataConnection() throws SQLException {
        DataSourceDescription dataSourceDesc = new DataSourceDescription();
        dataSourceDesc.setConnUrl(externalJdbcUrl);//SysParametersUtils.getStringValue("wf.external.system.jdbc.url"));
        dataSourceDesc.setUsername(externalJdbcUser);//SysParametersUtils.getStringValue("wf.external.system.jdbc.user"));
        dataSourceDesc.setPassword(externalJdbcPassword);//SysParametersUtils.getStringValue("wf.external.system.jdbc.password"));
        return DbcpConnectPools.getDbcpConnect(dataSourceDesc);
    }


    protected List<ExtSysUserInfo> reloadUserInfo() {

        try(Connection conn = getExternalDataConnection() ) {
            List<Object[]> users = DatabaseAccess.findObjectsBySql(conn,
                ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_USERINFO"));
            if (users == null)
                return null;
            List<ExtSysUserInfo> allUserInfo = new ArrayList<>(users.size() + 1);
            for (Object[] user : users) {
                ExtSysUserInfo userInfo = new ExtSysUserInfo();
                userInfo.setUserCode(StringBaseOpt.objectToString(user[0]));
                userInfo.setUserName(StringBaseOpt.objectToString(user[1]));
                userInfo.setPrimaryUnit(StringBaseOpt.objectToString(user[2]));
                userInfo.setUserOrder(NumberBaseOpt.castObjectToLong(user[3]));

                allUserInfo.add(userInfo);
            }
            return allUserInfo;
        }catch (SQLException |IOException  e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }


    protected List<ExtSysUnitInfo> reloadUnitInfo(){
        try(Connection conn = getExternalDataConnection() ) {
            List<Object[]> units =DatabaseAccess.findObjectsBySql(conn,
                ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_UNITINFO") );

            if(units == null)
                return null;
            List<ExtSysUnitInfo>  allunitInfo = new ArrayList<>(units.size()+1);
            for(Object[] unit : units){
                ExtSysUnitInfo unitInfo = new ExtSysUnitInfo();
                unitInfo.setUnitCode(StringBaseOpt.objectToString(unit[0]));
                unitInfo.setParentUnit(StringBaseOpt.objectToString(unit[1]));
                unitInfo.setUnitName(StringBaseOpt.objectToString(unit[2]));
                unitInfo.setUnitManager(StringBaseOpt.objectToString(unit[3]));
                unitInfo.setUnitOrder(NumberBaseOpt.castObjectToLong(unit[4]));
                unitInfo.setUnitPath(StringBaseOpt.objectToString(unit[5]));

                allunitInfo.add(unitInfo);
            }
            return allunitInfo;
        }catch (SQLException |IOException  e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    protected List<ExtSysUserUnit> reloadUserUnit(){
        try(Connection conn = getExternalDataConnection() ) {
            List<Object[]> userUnits = DatabaseAccess.findObjectsBySql(conn,
                ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_USERUNIT") );

            if(userUnits == null)
                return null;
            List<ExtSysUserUnit>  allUserUnits = new ArrayList<>(userUnits.size()+1);
            for(Object[] uu: userUnits){
                ExtSysUserUnit userUnit = new ExtSysUserUnit();
                userUnit.setUnitCode(StringBaseOpt.objectToString(uu[0]));
                userUnit.setUserCode(StringBaseOpt.objectToString(uu[1]));
                userUnit.setUserStation(StringBaseOpt.objectToString(uu[2]));
                userUnit.setUserRank(StringBaseOpt.objectToString(uu[3]));
                userUnit.setIsPrimary(StringBaseOpt.objectToString(uu[4]));
                userUnit.setUserOrder(NumberBaseOpt.castObjectToLong(uu[5]));
                allUserUnits.add(userUnit);
            }
            return allUserUnits;
        }catch (SQLException |IOException  e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    protected Map<String, Integer > reloadRankInfo(){
        try(Connection conn = getExternalDataConnection() ) {

            List<Object[]> ranks = DatabaseAccess.findObjectsBySql(conn,
                ExtendedQueryPool.getExtendedSql("WORKFLOW_EXTERNAL_RANKMAP") );

            if(ranks == null)
                return null;
            Map<String, Integer > rankMap = new HashMap<>(ranks.size()*2+1);
            for(Object[] rank: ranks){
                rankMap.put(StringBaseOpt.objectToString(rank[0]),
                    NumberBaseOpt.castObjectToInteger(rank[1]) );
            }
            return rankMap;
        }catch (SQLException |IOException  e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }


    @Override
    public List<ExtSysUserInfo> listAllUserInfo() {
        return allUserInfoCache.getCachedTarget();
    }

    @Override
    public List<ExtSysUnitInfo> listAllUnitInfo() {
        return allunitInfoCache.getCachedTarget();
    }


    private List<ExtSysUnitInfo> fetchSubUnit(String unitCode) {
        List<ExtSysUnitInfo> units = new ArrayList<>(10);
        for(ExtSysUnitInfo ui : allunitInfoCache.getCachedTarget() ){
            if(StringUtils.equals(unitCode,ui.getParentUnit())){
                units.add(ui);
            }
        }
        return units;
    }

    @Override
    public List<ExtSysUnitInfo> listSubUnit(String unitCode) {
        return subUnitMapCache.getCachedValue(unitCode);
    }

    @Override
    public List<ExtSysUnitInfo> listSubUnitAll(String unitCode) {

        if(StringUtils.isBlank(unitCode))
            return null;

        List<ExtSysUnitInfo> units = new ArrayList<>(50);
        List<ExtSysUnitInfo> subunits = listSubUnit(unitCode);
        while( subunits!=null && subunits.size()>0){
            units.addAll(subunits);
            List<ExtSysUnitInfo> subunits1 = new ArrayList<>();
            for(ExtSysUnitInfo u1: subunits){
                List<ExtSysUnitInfo> subunits2 = listSubUnit(u1.getUnitCode());
                if(subunits2!=null)
                    subunits1.addAll(subunits2);
            }
            subunits = subunits1;
        }
        CollectionsOpt.sortAsTree(units, (p, c) -> StringUtils.equals(p.getUnitCode(),c.getParentUnit()));
        return units;
    }

    @Override
    public ExtSysUnitInfo getUnitInfoByCode(String unitCode) {
        return codeToUnitMapCache.getCachedTarget().get(unitCode);
    }

    @Override
    public List<ExtSysUserUnit> listAllUserUnits() {
        return allUserUnitCache.getCachedTarget();
    }

    @Override
    public List<ExtSysUserUnit> listUnitUsers(String unitCode) {
        return unitUserMapCache.getCachedValue(unitCode);
    }

    @Override
    public List<ExtSysUserUnit> listUserUnits(String userCode) {
        return unitUserMapCache.getCachedValue(userCode);
    }

    @Override
    public ExtSysUserInfo getUserInfoByCode(String userCode) {
        return codeToUserMapCache.getCachedTarget().get(userCode);
    }

    /**
     * 从数据字典中获取 Rank 的等级
     * @param rankCode 行政角色代码
     * @return 行政角色等级
     */
    @Override
    public int getXzRank(String rankCode) {
        Integer rank = rankMapCache.getCachedTarget().get(rankCode);
        return rank == null ? CodeRepositoryUtil.MAXXZRANK : rank;
    }


    public void setExternalJdbcUrl(String externalJdbcUrl) {
        this.externalJdbcUrl = externalJdbcUrl;
    }

    public void setExternalJdbcUser(String externalJdbcUser) {
        this.externalJdbcUser = externalJdbcUser;
    }

    public void setExternalJdbcPassword(String externalJdbcPassword) {
        this.externalJdbcPassword = externalJdbcPassword;
    }
}
