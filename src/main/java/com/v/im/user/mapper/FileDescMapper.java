package com.v.im.user.mapper;

import com.v.im.user.entity.FileDesc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileDescMapper {

    int insert(FileDesc fileDesc);
    FileDesc FileDesc(Integer id);
}
