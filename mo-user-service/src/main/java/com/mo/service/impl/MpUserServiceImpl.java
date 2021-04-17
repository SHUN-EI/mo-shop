package com.mo.service.impl;

import com.mo.model.MpUserDO;
import com.mo.mapper.MpUserMapper;
import com.mo.service.MpUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
@Service
public class MpUserServiceImpl extends ServiceImpl<MpUserMapper, MpUserDO> implements MpUserService {

}
