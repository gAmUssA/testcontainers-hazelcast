package com.hazelcast.testcontainers;

import okhttp3.*;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.wait.HttpWaitStrategy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A test of interaction with Hazelcast via REST client interface
 *
 * @author Viktor Gamov on 7/18/17.
 * Twitter: @gamussa
 * @since 0.0.1
 */
public class RestClientTest {

    @ClassRule
    public static HazelcastContainer hazelcast =
            HazelcastContainer
                    .createHazelcastOSSContainer()
                    // http://docs.hazelcast.org/docs/latest-development/manual/html/Other_Client_and_Language_Implementations/REST_Client.html - Checking the status of cluster over REST
                    .waitingFor(new HttpWaitStrategy().forPath("/hazelcast/rest/cluster"))
                    .withRESTClient()
                    .withExposedPorts(5701);

    private OkHttpClient client;

    @Before
    public void setUp() throws Exception {
        client = new OkHttpClient();
    }

    @Test
    public void shouldPost() throws Exception {
        final String restBaseUrl = hazelcast.getRestBaseUrl();
        Request request = new Request.Builder().url(restBaseUrl + "/maps/mymap/mykey")
                .post(RequestBody.create(MediaType.parse("text/plain"), "myvalue"))
                .build();

        final Response res1 = client.newCall(request).execute();
        assertThat(res1.isSuccessful()).isTrue();
    }

    @Test
    public void shouldReadAfterPost() throws Exception {
        final String restBaseUrl = hazelcast.getRestBaseUrl();

        Request getReq = new Request.Builder().url(restBaseUrl + "/maps/mymap/mykey").get().build();
        final Response res2 = client.newCall(getReq).execute();
        assertThat(res2.isSuccessful()).isTrue();
        assertThat(res2.body().string()).isEqualTo("myvalue");
    }
}
