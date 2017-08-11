package org.activiti.services.connector.hub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableDiscoveryClient
@EnableBindings(IntegrationChannels.class)
@SpringBootApplication
public class ConnectorHubApplication {

    @Autowired
    private DiscoveryClient discoveryClient;

    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class,
                              args);
    }

    @StreamListener(IntegrationChannels.INTEGRATION_CONSUMER)
    public void consumeIntegrationEvents(){
        // look discoveryClient for connector apps
        

    }
}
