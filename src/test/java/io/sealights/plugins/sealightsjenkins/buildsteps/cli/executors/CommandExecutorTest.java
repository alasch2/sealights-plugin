package io.sealights.plugins.sealightsjenkins.buildsteps.cli.executors;

import hudson.EnvVars;
import hudson.util.DescribableList;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.LogConfiguration;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.configurationtechnologies.JavaOptions;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.configurationtechnologies.TechnologyOptions;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.configurationtechnologies.TechnologyOptionsDescriptor;
import io.sealights.plugins.sealightsjenkins.buildsteps.cli.entities.*;
import io.sealights.plugins.sealightsjenkins.entities.TokenData;
import io.sealights.plugins.sealightsjenkins.utils.Logger;
import io.sealights.plugins.sealightsjenkins.utils.NullLogger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutorTest {

    private Logger nullLogger = new NullLogger();
    private String validToken = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1BOTkuYXV0aC5zZWFsaWdodHMuaW8vIiwiand0aWQiOiJERVYtQTk5LGktNTZiMDI0ZGQsdHhJZCwxNDc4NDUwNzUxODEyIiwic3ViamVjdCI6ImFnZW50c0BDdXN0b21lcklkIiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50Iiwic2xfaW1wZXJfc3ViamVjdCI6IiIsIngtc2wtc2VydmVyIjoiaHR0cDovL3d3dy5nb29nbGUuY29tL3NlYWxpZ2h0cyIsImlhdCI6MTQ3ODQ1MDc1M30.awtipSFsTcRCT6sUBWaFv2GKaXXZ7gCSBRXorar1nOpOkzUPQqPB9xz0rOOHY7Kb7vFnZUjsOOTob_ui2OZe430O7MJmdFkxrbpXQcUndvWHfi63STsGepI1q61tOejjrs7WiyInsUCMV00Tr25F2NRdf70PGloK8BBs9BdOhldJcEvTYnF8LPw5trAnE8YA-TuIxgjocR0a0QdF_JOibD2mpNQwIfvOsmNrrfArTOoZS2W1XZ_pXa-n1VuWDSgRZF9yVaPMwmqcLoNsydEURgtzuQj8cP5sUg6XjSLoAyfA6guTfZ4rIdJwxJ4GdC8k24yqzhV6X0c_mJ5yrlB9HNTBdIc651SrcMyd4UIM_-zMMEL-1ItKE-txdFijv9jeyr6mQxhbvkCeh6BRRJZqNti4dRrLeztAUbfsAayBEeTnAuMXXsMzSccS-pO0aU2zQMuZaVIzCHqIV9ex7vjwXNKGw4TspFkxw2w85QssHYvIUpPoQ7bzu8sFCKJY-phTRr7i6KCPBCez-Zlu_zL0txsZgwIcXE5rNZvRRC2imjrWVzGFb6IAGVHU3lbJuGocnl4Z-td1tf1mDZqZN9_NL8mltddUugeo7emJNU1UZiHN4lHEKxcayj4LFIgeTyE1R_d8EOi9WMieuEwpRB7r_qXMUDKci07su9UR6XpKS2I";

    @Test
    public void createExecutionCommand_startCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        String testStage = "Integration";
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken();
        StartCommandArguments startArguments = new StartCommandArguments(testStage);
        AbstractCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, baseArguments, startArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"start", "-customerid", "fake-customer", "-server", "https://fake-url/api",
        "-labid", "fake-env", "-testStage", "Integration"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_endCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken();
        EndCommandArguments endArguments = new EndCommandArguments();
        AbstractCommandExecutor commandExecutor = new EndCommandExecutor(nullLogger, baseArguments, endArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"end", "-customerid", "fake-customer", "-server", "https://fake-url/api",
        "-labid", "fake-env"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_uploadReportsCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken();
        UploadReportsCommandArguments uploadReportsArguments = new UploadReportsCommandArguments(null, null, false, "theSource");
        AbstractCommandExecutor commandExecutor = createUploadReportsCommandExecutor(nullLogger, baseArguments, uploadReportsArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"uploadReports", "-customerid", "fake-customer", "-server", "https://fake-url/api",
                "-labid", "fake-env", "-reportFile", "file1", "-reportFile", "file2", "-reportFilesFolder", "folder1"
                , "-reportFilesFolder", "folder2", "-hasMoreRequests", "false", "-source", "theSource"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }


    @Test
    public void createExecutionCommand_configCommandExecutor_withoutToken_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken();
        ConfigCommandArguments configCommandArguments = createConfigArguments();
        AbstractCommandExecutor commandExecutor = new ConfigCommandExecutor(nullLogger, baseArguments, configCommandArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"-config", "-customerid", "fake-customer", "-server", "https://fake-url/api", "-appname", "fake-app", "-buildname", "fake-build", "-branchname", "fake-branch", "-packagesincluded", "io.included.package", "-packagesexcluded", "io.excluded.package", "-enableNoneZeroErrorCode"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_setJavaPath_withoutToken_shouldUseGivenJavaPath() {
        //Arrange
        String testStage = "Integration";
        String javaPath = "override/path/to/java";
        BaseCommandArguments baseArguments = createBaseCommandArgumentsWithoutToken(javaPath);
        StartCommandArguments startArguments = new StartCommandArguments(testStage);
        AbstractCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, baseArguments, startArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"start", "-customerid", "fake-customer", "-server", "https://fake-url/api",
         "-labid", "fake-env", "-testStage", "Integration"};
        List<String> expectedCommand = createExpectedCommand(javaPath, commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }


    @Test
    public void createExecutionCommand_startCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        String testStage = "Integration";
        BaseCommandArguments baseArguments = createBaseCommandArguments();
        StartCommandArguments startArguments = new StartCommandArguments(testStage);
        AbstractCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, baseArguments, startArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"start", "-token", validToken,  "-labid", "fake-env", "-testStage", "Integration"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_endCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArguments();
        EndCommandArguments endArguments = new EndCommandArguments();
        AbstractCommandExecutor commandExecutor = new EndCommandExecutor(nullLogger, baseArguments, endArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"end", "-token", validToken,  "-labid", "fake-env"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    @Test
    public void createExecutionCommand_uploadReportsCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArguments();
        UploadReportsCommandArguments uploadReportsArguments = new UploadReportsCommandArguments(null, null, false, "theSource");
        AbstractCommandExecutor commandExecutor = createUploadReportsCommandExecutor(nullLogger, baseArguments, uploadReportsArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"uploadReports", "-token", validToken,  "-labid", "fake-env", "-reportFile", "file1", "-reportFile", "file2", "-reportFilesFolder", "folder1", "-reportFilesFolder", "folder2", "-hasMoreRequests", "false", "-source", "theSource"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }


    @Test
    public void createExecutionCommand_configCommandExecutor_shouldCreateGoodExecutionLine() {
        //Arrange
        BaseCommandArguments baseArguments = createBaseCommandArguments();
        ConfigCommandArguments configCommandArguments = createConfigArguments();
        AbstractCommandExecutor commandExecutor = new ConfigCommandExecutor(nullLogger, baseArguments, configCommandArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {
                "-config",
                "-token",
                validToken,
                "-appname", "fake-app", "-buildname", "fake-build", "-branchname", "fake-branch",
                "-packagesincluded", "io.included.package", "-packagesexcluded", "io.excluded.package",
                "-enableNoneZeroErrorCode"};
        List<String> expectedCommand = createExpectedCommand(commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    private AbstractCommandExecutor createUploadReportsCommandExecutor(
            Logger nullLogger, BaseCommandArguments baseArguments, UploadReportsCommandArguments
            uploadReportsArguments) {
        List<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        List<String> folders = new ArrayList<>();
        folders.add("folder1");
        folders.add("folder2");
        UploadReportsCommandExecutor uploadReportsCommandExecutor
                = new UploadReportsCommandExecutor(nullLogger, baseArguments, uploadReportsArguments);
        uploadReportsCommandExecutor.setReportFiles(files);
        uploadReportsCommandExecutor.setReportFilesFolders(folders);

        return uploadReportsCommandExecutor;
    }

    @Test
    public void createExecutionCommand_setJavaPath_shouldUseGivenJavaPath() {
        //Arrange
        String testStage = "Integration";
        String javaPath = "override/path/to/java";
        BaseCommandArguments baseArguments = createBaseCommandArguments(javaPath);
        StartCommandArguments startArguments = new StartCommandArguments(testStage);
        AbstractCommandExecutor commandExecutor = new StartCommandExecutor(nullLogger, baseArguments, startArguments);
        //Act
        List<String> actualCommand = commandExecutor.createExecutionCommand();
        //Assert
        String[] commandAndParams = {"start", "-token", validToken,  "-labid", "fake-env", "-testStage", "Integration"};
        List<String> expectedCommand = createExpectedCommand(javaPath, commandAndParams);
        Assert.assertEquals("execution command is not as expected", expectedCommand, actualCommand);
    }

    private BaseCommandArguments createBaseCommandArguments(String javaPath) {
        BaseCommandArguments baseArgs = createBaseCommandArguments();
        baseArgs.setJavaPath(javaPath);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArgumentsWithoutToken(String javaPath) {
        BaseCommandArguments baseArgs = createBaseCommandArguments(javaPath);
        baseArgs.setTokenData(null);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArgumentsWithoutToken() {
        BaseCommandArguments baseArgs = createBaseCommandArguments();
        baseArgs.setTokenData(null);
        return baseArgs;
    }

    private BaseCommandArguments createBaseCommandArguments() {
        BaseCommandArguments baseArgs = new BaseCommandArguments();

        TokenData tokenData = new TokenData();
        tokenData.setToken(validToken);

        baseArgs.setTokenData(tokenData);
        baseArgs.setAgentPath("/fake/path/to/agent.jar");
        baseArgs.setJavaPath("path/to/java");
        baseArgs.setAppName("fake-app");
        baseArgs.setBuildName("fake-build");
        baseArgs.setBranchName("fake-branch");
        baseArgs.setLabId("fake-env");
        baseArgs.setCustomerId("fake-customer");
        baseArgs.setUrl("https://fake-url/api");
        baseArgs.setEnvVars(new EnvVars());
        baseArgs.setLogConfiguration(new LogConfiguration());
        return baseArgs;
    }

    private ConfigCommandArguments createConfigArguments() {
        DescribableList<TechnologyOptions, TechnologyOptionsDescriptor> technologyOptions = new DescribableList<>(JavaOptions.DescriptorImpl.NOOP);
        technologyOptions.add(new JavaOptions("io.included.package", "io.excluded.package"));
        ConfigCommandArguments configArguments = new ConfigCommandArguments(technologyOptions);

        return configArguments;
    }

    private List<String> createExpectedCommand(String[] commandAndParams) {
        return createExpectedCommand("path/to/java", commandAndParams);
    }

    private List<String> createExpectedCommand(String javaPath, String[] commandAndParams) {
        List<String> prefix = new ArrayList<>();
        prefix.add(javaPath);
        prefix.add(AbstractCommandExecutor.formatTagProp());
        prefix.addAll(new LogConfiguration().toSystemProperties());
        prefix.add("-jar");
        prefix.add("/fake/path/to/agent.jar");
        prefix.addAll(Arrays.asList(commandAndParams));
        return prefix;
    }
}
