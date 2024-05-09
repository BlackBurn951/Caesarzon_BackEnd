package org.caesar.gatewayservice;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import reactor.netty.http.client.HttpClient;

public class DnsResolutionFixer implements HttpClientCustomizer {
    @Override
    public HttpClient customize(HttpClient httpClient) {
        return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
    }
}
