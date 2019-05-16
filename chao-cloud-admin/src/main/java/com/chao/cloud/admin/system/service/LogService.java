package com.chao.cloud.admin.system.service;

import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.LogDTO;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;

@Service
public interface LogService {

    R queryList(Query query);

    int remove(Long id);

    int batchRemove(Long[] ids);

    void save(LogDTO sysLog);
}
