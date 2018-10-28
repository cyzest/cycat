package com.cyzest.cycat.config;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ServerConfigurerTest {

    @Test
    public void createServerConfigTest() throws Exception {

        File confFile = new File("conf/config.json");

        ServerConfig serverConfig = ServerConfigurer.createServerConfig(confFile);

        Assert.assertNotNull(serverConfig);
        Assert.assertEquals(serverConfig.getPort(), Integer.valueOf(8080));
        Assert.assertEquals(serverConfig.getThread(), Integer.valueOf(10));

        List<HostInfo> hostInfos = serverConfig.getHosts();

        Assert.assertNotNull(hostInfos);
        Assert.assertEquals(hostInfos.size(), 2);

        HostInfo hostInfo = hostInfos.get(0);

        Assert.assertNotNull(hostInfo);
        Assert.assertEquals(hostInfo.getHost(), "cyzest.com");

        HostInfo defaultHostInfo = hostInfos.get(1);

        Assert.assertNotNull(defaultHostInfo);
        Assert.assertEquals(defaultHostInfo.getHost(), HostInfo.DEFAULT_HOST);
    }

}
