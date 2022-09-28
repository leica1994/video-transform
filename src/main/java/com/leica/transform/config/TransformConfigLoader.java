package com.leica.transform.config;

import com.leica.transform.common.util.PropertiesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.leica.transform.common.constant.BaseConstants.COMMAND_PARAM_PREFIX;
import static com.leica.transform.common.constant.BaseConstants.EQUAL_SEPARATOR;

/**
 * 配置文件加载器
 *
 * <p>
 * 加载顺序如下:
 * <ol>
 *     <li>运行参数</li>
 *     <li>jvm参数</li>
 *     <li>环境变量</li>
 *     <li>配置文件</li>
 *     <li>Default Config</li>
 * </ol>
 * </p>
 *
 * @author <a href="mailto:705463446@qq.com">ikun</a>
 * @date 2022/9/28 10:57
 */
public class TransformConfigLoader {

    private static final String CONFIG_PATH = "transform.properties";

    private final TransformConfig config = new TransformConfig();

    public TransformConfig load(String[] args) {
        // ==================== 配置文件 ====================
        {
            try (InputStream inputStream = TransformConfigLoader.class.getResourceAsStream(CONFIG_PATH)) {
                if (Objects.nonNull(inputStream)) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    PropertiesUtils.properties2Object(properties, config);
                }
            } catch (IOException ignore) {
                // ignore
            }
        }

        // ==================== 环境变量 ====================
        {
            Map<String, String> env = System.getenv();
            Properties properties = new Properties();
            properties.putAll(env);
            PropertiesUtils.properties2Object(properties, config);
        }

        // ==================== jvm参数 ====================
        {
            Properties properties = System.getProperties();
            PropertiesUtils.properties2Object(properties, config);
        }

        // ==================== 运行参数 ====================
        {
            if (Objects.nonNull(args) && args.length != 0) {
                Properties properties = new Properties();
                for (String arg : args) {
                    if (arg.startsWith(COMMAND_PARAM_PREFIX) && arg.contains(EQUAL_SEPARATOR)) {
                        properties.put(arg.substring(2, arg.indexOf(EQUAL_SEPARATOR)), arg.substring(arg.indexOf(EQUAL_SEPARATOR) + 1));
                    }
                }
                PropertiesUtils.properties2Object(properties, config);
            }
        }
        return config;
    }

    public static TransformConfigLoader getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final TransformConfigLoader INSTANCE = new TransformConfigLoader();
    }

    private TransformConfigLoader() {
    }
}
