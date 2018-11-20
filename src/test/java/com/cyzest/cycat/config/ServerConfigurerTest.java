package com.cyzest.cycat.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class ServerConfigurerTest {

    @Test
    public void createServerConfigTest() throws Exception {

        File confFile = new File("conf/config.json");

        ServerConfig serverConfig = ServerConfigurer.createServerConfig(confFile);

        Assertions.assertNotNull(serverConfig);
        Assertions.assertEquals(Integer.valueOf(8080), serverConfig.getPort());
        Assertions.assertEquals(Integer.valueOf(10), serverConfig.getThread());

        List<HostInfo> hostInfos = serverConfig.getHosts();

        Assertions.assertNotNull(hostInfos);
        Assertions.assertEquals(2, hostInfos.size());

        HostInfo hostInfo = hostInfos.get(0);

        Assertions.assertNotNull(hostInfo);
        Assertions.assertEquals("cyzest.com", hostInfo.getHost());

        HostInfo defaultHostInfo = hostInfos.get(1);

        Assertions.assertNotNull(defaultHostInfo);
        Assertions.assertEquals(HostInfo.DEFAULT_HOST, defaultHostInfo.getHost());
    }

}
