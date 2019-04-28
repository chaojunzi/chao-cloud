package com.chao.cloud.ssh.service;

import java.io.File;
import java.util.List;

import com.hankcs.hanlp.suggest.Suggester;

/**
 * 词库
 * @功能：
 * @author： 薛超
 * @时间：2019年4月8日
 * @version 1.0.0
 */
public interface SshService {
    /**
     * 文本-词库-推荐
     */
    Suggester SUGGESTER = new Suggester();
    /**
     * 最大限制
     */
    int LIMIT = 5;// 限制匹配个数-5

    /**
     * 重置词典
     */
    void reloadLexicon();

    /**
     * 匹配查询
     * @param text
     * @return
     */
    List<String> suggest(String text);

    /**
     * 获取词库文件
     */
    File getFile();

}
