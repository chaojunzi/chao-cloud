package com.chao.cloud.admin.sys.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.sys.dal.xcmapper.GeneratorMapper;
import com.chao.cloud.admin.sys.service.GeneratorService;

@Service
public class GeneratorServiceImpl implements GeneratorService {

	@Autowired
	private GeneratorMapper generatorMapper;

	@Override
	public List<Map<String, Object>> list() {
		List<Map<String, Object>> list = generatorMapper.list();
		return list;
	}

}
