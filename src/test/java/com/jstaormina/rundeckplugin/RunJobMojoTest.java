package com.jstaormina.rundeckplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rundeck.api.RunJob;
import org.rundeck.api.RunJobBuilder;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.RundeckClientBuilder;
import org.rundeck.api.domain.RundeckExecution;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RundeckClient.class, RunJobBuilder.class, Thread.class})
public class RunJobMojoTest {

    private static final String URL = "http://foo.com:8080";
    private static final String TOKEN = "123456789";
    private static final String JOB_UUID = "q1w2e3r4t5y6u7i8o9";
    private static final Properties NODE_FILTERS = new Properties();
    private static final Properties OPTIONS = new Properties();

    static {
        Map<String, String> nodes = new HashMap<String, String>();
        nodes.put("node1", "foo");
        NODE_FILTERS.putAll(nodes);

        Map<String, String> options = new HashMap<String, String>();
        options.put("option1", "bar");
        OPTIONS.putAll(options);
    }

    private RundeckClient mockRundeckClient;
    private RundeckClientBuilder mockRundeckClientBuilder;
    private RunJobBuilder mockRunJobBuilder;
    private RunJob mockRunJob;
    private RunJobMojo mojo;
    private RundeckExecution mockExecution;

    @Before
    public void setup() {
        mojo = new RunJobMojo();
        mojo.setUrl(URL);
        mojo.setToken(TOKEN);
        mojo.setJobUuid(JOB_UUID);
        mojo.setNodeFilters(NODE_FILTERS);
        mojo.setOptions(OPTIONS);

        mockRundeckClient = Mockito.mock(RundeckClient.class);
        mockRundeckClientBuilder = Mockito.mock(RundeckClientBuilder.class);
        mockRunJobBuilder = Mockito.mock(RunJobBuilder.class);
        mockRunJob = Mockito.mock(RunJob.class);
        mockExecution = Mockito.mock(RundeckExecution.class);

        PowerMockito.mockStatic(RundeckClient.class);
        PowerMockito.when(RundeckClient.builder()).thenReturn(mockRundeckClientBuilder);

        PowerMockito.mockStatic(RunJobBuilder.class);
        PowerMockito.when(RunJobBuilder.builder()).thenReturn(mockRunJobBuilder);

        Mockito.when(mockRundeckClientBuilder.url(Mockito.any(String.class))).thenReturn(mockRundeckClientBuilder);
        Mockito.when(mockRundeckClientBuilder.token(Mockito.any(String.class))).thenReturn(mockRundeckClientBuilder);
        Mockito.when(mockRundeckClientBuilder.build()).thenReturn(mockRundeckClient);

        Mockito.when(mockRunJobBuilder.setJobId(Mockito.any(String.class))).thenReturn(mockRunJobBuilder);
        Mockito.when(mockRunJobBuilder.setNodeFilters(Mockito.any(Properties.class))).thenReturn(mockRunJobBuilder);
        Mockito.when(mockRunJobBuilder.setOptions(Mockito.any(Properties.class))).thenReturn(mockRunJobBuilder);
        Mockito.when(mockRunJobBuilder.build()).thenReturn(mockRunJob);

        Mockito.when(mockRundeckClient.runJob(mockRunJob, 5L, TimeUnit.SECONDS)).thenReturn(mockExecution);

        Mockito.when(mockExecution.getStatus()).thenReturn(RundeckExecution.ExecutionStatus.RUNNING)
            .thenReturn(RundeckExecution.ExecutionStatus.SUCCEEDED);
    }

    @Test
    public void testWhenPluginIsExecutedRundeckClientIsBuiltWithCorrectProperties()
        throws MojoExecutionException, MojoFailureException {
        mojo.execute();

        Mockito.verify(mockRundeckClientBuilder).url(URL);
        Mockito.verify(mockRundeckClientBuilder).token(TOKEN);
        Mockito.verify(mockRundeckClientBuilder).build();
    }

    @Test
    public void testWhenPluginIsExecutedThenTheRundeckClientRunsTheJobWithTheNodesAndOptionsSet()
        throws MojoExecutionException, MojoFailureException {
        mojo.execute();

        Mockito.verify(mockRunJobBuilder).setJobId(JOB_UUID);
        Mockito.verify(mockRunJobBuilder).setNodeFilters(NODE_FILTERS);
        Mockito.verify(mockRunJobBuilder).setOptions(OPTIONS);
        Mockito.verify(mockRunJobBuilder).build();

        Mockito.verify(mockRundeckClient).runJob(mockRunJob, 5L, TimeUnit.SECONDS);
    }

    @Test(expected=MojoFailureException.class)
    public void testMojoFailsWithMojoFailureExceptionWhenRundeckJobFails() throws Exception {
        Mockito.when(mockExecution.getStatus()).thenReturn(RundeckExecution.ExecutionStatus.FAILED);
        mojo.execute();
    }

}
