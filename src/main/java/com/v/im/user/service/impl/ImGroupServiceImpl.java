package com.v.im.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.v.im.user.entity.ImGroup;
import com.v.im.user.mapper.ImGroupMapper;
import com.v.im.user.service.IImGroupService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 皓天
 * @since 2020-7-07
 */
@Service
@Qualifier("imGroupService")
public class ImGroupServiceImpl extends ServiceImpl<ImGroupMapper, ImGroup> implements IImGroupService {

}
