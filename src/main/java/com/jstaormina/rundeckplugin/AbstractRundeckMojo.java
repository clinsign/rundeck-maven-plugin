package com.jstaormina.rundeckplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.rundeck.api.RundeckClient;

public abstract class AbstractRundeckMojo extends AbstractMojo {

    @Parameter()
    private String url;
    @Parameter
    private String token;
    private RundeckClient rundeckClient;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RundeckClient getRundeckClient() {
        if (rundeckClient == null) {
            rundeckClient = RundeckClient.builder().url(url).token(token).build();
        }

        return rundeckClient;
    }
}
