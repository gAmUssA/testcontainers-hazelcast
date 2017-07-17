package support;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.testcontainers.HazelcastContainer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.Wait;

import java.io.IOException;

@Slf4j
public class HazelcastTestApp<SELF extends HazelcastTestApp<SELF>> extends HazelcastContainer {

    private final String clientConfigFile;

    @Getter(lazy = true)
    private final HazelcastInstance hazelcastClient = HazelcastClient.newHazelcastClient(initHazelcastClient());

    public HazelcastTestApp() {
        super(HAZELCAST_DOCKER_IMAGE_NAME);
        withLogConsumer(new Slf4jLogConsumer(log));
        withExposedPorts(5701);
        setWaitStrategy(Wait.forListeningPort());

        clientConfigFile = null;
    }

    public HazelcastTestApp(String dockerImage, String configFile, String clientConfigFile){
        super(dockerImage);
        withLogConsumer(new Slf4jLogConsumer(log));
        withClasspathResourceMapping(configFile, "/opt/hazelcast/hazelcast.xml", BindMode.READ_ONLY);
        withExposedPorts(5701);
        setWaitStrategy(Wait.forListeningPort());

        this.clientConfigFile = clientConfigFile;
    }

    private ClientConfig initHazelcastClient() {
        ClientConfig cc = null;
        final String containerIpAddress = getContainerIpAddress();
        final Integer mappedPort = getMappedPort(getExposedPorts().get(0));
        if (clientConfigFile != null){
            try {
                cc = new XmlClientConfigBuilder(this.clientConfigFile).build();
                cc.getNetworkConfig().addAddress(containerIpAddress + ":" + mappedPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            cc = new ClientConfig();
            cc.getNetworkConfig().addAddress(containerIpAddress + ":" + mappedPort);
        }

        return cc;
    }
}
