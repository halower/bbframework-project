package com.quickstart.bbframework.modules.ums.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quickstart.bbframework.modules.ums.service.UmsRoleMenuRelationService;
import com.quickstart.bbframework.modules.ums.mapper.UmsRoleMenuRelationMapper;
import com.quickstart.bbframework.modules.ums.model.UmsRoleMenuRelation;
import org.springframework.stereotype.Service;

/**
 * 角色菜单关系管理Service实现类
 * Created by macro on 2020/8/21.
 */
@Service
public class UmsRoleMenuRelationServiceImpl extends ServiceImpl<UmsRoleMenuRelationMapper, UmsRoleMenuRelation> implements UmsRoleMenuRelationService {
}
