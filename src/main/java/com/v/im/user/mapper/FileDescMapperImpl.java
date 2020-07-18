package com.v.im.user.mapper;

import com.v.im.user.entity.FileDesc;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class FileDescMapperImpl implements FileDescMapper{
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;
    @Override
    public int insert(FileDesc fileDesc) {
        int result = sqlSessionTemplate.insert("insert", fileDesc);
        return result;
    }

    @Override
    public FileDesc FileDesc(Integer id) {
        FileDesc result = sqlSessionTemplate.selectOne("FileDesc", id);
        return result;
    }
}
