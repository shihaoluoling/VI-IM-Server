package com.v.im.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.v.im.user.entity.ImChatGroup;
import com.v.im.user.mapper.ImChatGroupMapper;
import com.v.im.user.service.IImChatGroupService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群 服务实现类
 * </p>
 *
 * @author 皓天
 * @since 2020-7-07
 */
@Service
@Qualifier("imChatGroupServiceImpl")
public class ImChatGroupServiceImpl extends ServiceImpl<ImChatGroupMapper, ImChatGroup> implements IImChatGroupService {

}
