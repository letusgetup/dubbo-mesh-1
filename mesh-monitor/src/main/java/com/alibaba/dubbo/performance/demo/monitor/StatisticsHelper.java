package com.alibaba.dubbo.performance.demo.monitor;


import com.alibaba.dubbo.performance.demo.common.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 统计帮助类
 * <p>
 *
 */
public class StatisticsHelper {

    /** LOGGER */
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsHelper.class);

    /**
     * 调整节点的数目(限定节点个数) <br>
     * <br>
     * <b>注意：</b>删除的规则是从数值最小的开始删除，并且要求节点名称除前缀外是一个数值
     * <p>
     * 
     * @param zookeeper
     *            {@link CuratorFramework}
     * @param parentPath
     *            父节点路径
     * @param prefix
     *            节点名前缀
     * @param limit
     *            限制的数目
     */
    public static void adjustNodesByLimit(CuratorFramework zookeeper, String parentPath, String prefix, int limit) {
        int length = prefix.length();
        try {
            List<String> list = zookeeper.getChildren().forPath(parentPath);
            // 遍历子节点，找出最小值删除
            while (list.size() > limit) {
                Long min = Long.MAX_VALUE;
                for (String s : list) {
                    if (s.length() > length) {
                        Long tempValue = new Long(s.substring(length));
                        if (tempValue < min)
                            min = tempValue;
                    }
                }

                zookeeper.delete().forPath(parentPath + Constants.ZK_SEPARATOR_DEFAULT + prefix + min);
                list.remove(prefix + min);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 调整节点的数目(限定节点个数)
     * <p>
     * 
     * @param list
     * @param nodeCountLimit
     */
    public static void adjustNodesByLimit(List<StatisticsInfo> list, int nodeCountLimit) {
        while (list.size() > nodeCountLimit) {
            list.remove(0);
        }
    }
}
