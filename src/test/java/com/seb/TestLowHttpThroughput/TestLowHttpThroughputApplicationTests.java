package com.seb.TestLowHttpThroughput;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestLowHttpThroughputApplicationTests {
    
    private final static int TOXI_PROXY_PORT = 8474;
    
    private final static int TOXIFIED_WEB_PORT = 10000;
    
    @LocalServerPort
    private int localServerPort;
    
    private final ToxiproxyClient toxiproxyClient = new ToxiproxyClient("localhost", TOXI_PROXY_PORT);
    
    private Proxy webProxy;
    
    private RestTemplate restTemplate;
    
    @Before
    public void init() throws IOException {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =  new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setReadTimeout(1000);
        restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        webProxy = toxiproxyClient.createProxy("webProxy", "localhost:" + TOXIFIED_WEB_PORT, "localhost:" + localServerPort);
        webProxy.toxics().latency("web-latency", ToxicDirection.DOWNSTREAM, 10);
    }
    
    @After
    public void clean() throws IOException {
        webProxy.delete();
    }
    
    @Test
    public void test_endpoint() {
        System.out.println(restTemplate.getForEntity("http://localhost:" + TOXIFIED_WEB_PORT + "/message", String.class));
        
    }

}
