package com.chao.cloud.admin.system.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.LogDTO;
import com.chao.cloud.admin.system.mapper.LogMapper;
import com.chao.cloud.admin.system.service.LogService;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogMapper logMapper;

    @Override
    public R queryList(Query query) {
        int count = logMapper.count(query);
        List<LogDTO> logs = Collections.emptyList();
        if (count > 0) {
            logs = logMapper.list(query);
        }
        return R.page(logs, count);
    }

    @Override
    public int remove(Long id) {
        int count = logMapper.remove(id);
        return count;
    }

    @Override
    public int batchRemove(Long[] ids) {
        return logMapper.batchRemove(ids);
    }

    @Override
    public void save(LogDTO sysLog) {
        logMapper.save(sysLog);
    }

}
