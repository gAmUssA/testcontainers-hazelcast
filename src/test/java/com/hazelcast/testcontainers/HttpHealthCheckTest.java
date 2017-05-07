package com.hazelcast.testcontainers;

import com.hazelcast.testcontainers.cluster.ClusterHealthCheckResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class HttpHealthCheckTest {

    // enable http health check
    private static String javaOpts = "-Dhazelcast.http.healthcheck.enabled=true";

    private OkHttpClient client;

    @ClassRule
    public static HazelcastContainer hazelcast = HazelcastContainer
            // http health was added in 3.8.x
            .createHazelcastOSSContainer("3.8.1")
            .withEnv("JAVA_OPTS", javaOpts)
            .withLogConsumer(new Slf4jLogConsumer(log))
            .withExposedPorts(5701);

    @Before
    public void setUp() throws Exception {
        client = new OkHttpClient();
    }

    @Test
    public void testHealthUrl() throws Exception {
        final String containerIpAddress = hazelcast.getContainerIpAddress();
        final Integer mappedPort = hazelcast.getMappedPort(5701);

        final String url = "http://" + containerIpAddress + ":" + mappedPort + "/hazelcast/health";
        System.out.println("url = " + url);
        final Request request = new Request.Builder()
                .url(url)
                .build();

        final Response execute = client.newCall(request).execute();
        final ResponseBody body = execute.body();
        final String stringBody = body.string();

        log.debug(stringBody);
        final ClusterHealthCheckResponse clusterHealthCheckResponse = new ClusterHealthCheckResponse(stringBody.split("[\\r\\n]+"));

        assertThat(clusterHealthCheckResponse.getNodeState(), is("ACTIVE"));
        assertThat(clusterHealthCheckResponse.getClusterState(), is("ACTIVE"));
        assertThat(clusterHealthCheckResponse.isClusterSafe(), is(true));
        assertThat(clusterHealthCheckResponse.getMigrationQueueSize(), is(0));
        assertThat(clusterHealthCheckResponse.getClusterSize(), is(1));


    }
}
