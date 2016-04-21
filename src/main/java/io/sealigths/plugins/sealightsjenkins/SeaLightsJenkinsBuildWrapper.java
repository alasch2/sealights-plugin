package io.sealigths.plugins.sealightsjenkins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration.MavenIntegration;
import io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration.MavenIntegrationInfo;
import io.sealigths.plugins.sealightsjenkins.io.sealigths.plugins.sealightsjenkins.integration.SeaLightsPluginInfo;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Installs tools selected by the user. Exports configured paths and a home variable for each tool.
 *
 * @author rcampbell
 * @author Oleg Nenashev
 *
 */
public class SeaLightsJenkinsBuildWrapper extends BuildWrapper {


    private final boolean enable;
    private final String projectName;
    private final String moduleName;
    private final String branch;
    private final String projectType;
    private final String pomPath;
    private final String packagesincluded;
    private final String packagesexcluded;
    private final String filesincluded;
    private final String filesexcluded;
    private final String testingFramework;
    private final String relativePathToEffectivePom;
    private final boolean recursive;
    private final String workspacepath;
    private final String buildScannerJar;
    private final String testListenerJar;
    private final String testListenerConfigFile;
    private final boolean inheritedBuild;
    private final boolean logEnabled;
    private final String logLevel;
    private final boolean logToFile;
    private final String logFolder;


    @DataBoundConstructor
    public SeaLightsJenkinsBuildWrapper(boolean enable, String projectName, String moduleName,String branch, String projectType, String pomPath,
                                        String packagesincluded, String packagesexcluded, String filesincluded, String filesexcluded,
                                        String testingFramework, String relativePathToEffectivePom, boolean recursive,
                                        String workspacepath, String buildScannerJar, String testListenerJar,
                                        String testListenerConfigFile, boolean inheritedBuild, boolean logEnabled,
                                        String logLevel, boolean logToFile, String logFolder) {
        this.enable = enable;
        this.projectName = projectName;
        this.moduleName = moduleName;
        this.branch = branch;
        this.projectType = projectType;
        this.pomPath = pomPath;
        this.packagesincluded = packagesincluded;
        this.packagesexcluded = packagesexcluded;
        this.filesincluded = filesincluded;
        this.filesexcluded = filesexcluded;
        this.testingFramework = testingFramework;
        this.relativePathToEffectivePom = relativePathToEffectivePom;
        this.recursive = recursive;
        this.workspacepath = workspacepath;
        this.buildScannerJar = buildScannerJar;
        this.testListenerJar = testListenerJar;
        this.testListenerConfigFile = testListenerConfigFile;
        this.inheritedBuild = inheritedBuild;
        this.logEnabled = logEnabled;
        this.logLevel = logLevel;
        this.logToFile = logToFile;
        this.logFolder = logFolder;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher,
                             BuildListener listener) throws IOException, InterruptedException {

        listener.getLogger().println();
        listener.getLogger().println(testingFramework);
        listener.getLogger().println(enable);
        listener.getLogger().println(projectName);
        listener.getLogger().println(moduleName);
        listener.getLogger().println(projectType);
        listener.getLogger().println(pomPath);
        listener.getLogger().println(packagesincluded);
        listener.getLogger().println(packagesexcluded);
        listener.getLogger().println(filesincluded);
        listener.getLogger().println(filesexcluded);
        listener.getLogger().println(buildScannerJar);
        listener.getLogger().println(testListenerJar);
        listener.getLogger().println(logEnabled);
        listener.getLogger().println(logToFile);
        listener.getLogger().println(logLevel);
        listener.getLogger().println(logFolder);

        Environment env = new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
            }
        };

        FilePath ws = build.getWorkspace();
        if (ws == null) {
            return env;
        }

        String workingDir = ws.getRemote();
        String pomPath;
        if (relativePathToEffectivePom != null && !"".equals(relativePathToEffectivePom))
            pomPath = workingDir + "/" + relativePathToEffectivePom;
        else
            pomPath = workingDir + "/pom.xml";

        listener.getLogger().println("::::::::::::::::::::::::::::::");
        listener.getLogger().println(pomPath);
        listener.getLogger().println("::::::::::::::::::::::::::::::");

        SeaLightsPluginInfo slInfo = new SeaLightsPluginInfo();
        slInfo.setEnabled(enable);
        slInfo.setBuildName(String.valueOf(build.getNumber()));
        slInfo.setCustomerId(getDescriptor().getCustomerId());
        slInfo.setServerUrl(getDescriptor().getUrl());
        slInfo.setProxy(getDescriptor().getProxy());

        if (workspacepath != null && !"".equals(workspacepath))
            slInfo.setWorkspacepath(workspacepath);
        else
            slInfo.setWorkspacepath(workingDir);


        slInfo.setAppName(projectName);
        slInfo.setModuleName(moduleName);
        slInfo.setBranchName(branch);
        slInfo.setFilesIncluded(filesincluded);
        slInfo.setFilesExcluded(filesexcluded);
        slInfo.setRecursive(recursive);
        slInfo.setPackagesIncluded(packagesincluded);
        slInfo.setPackagesExcluded(packagesexcluded);

        slInfo.setListenerJar(testListenerJar);
        slInfo.setListenerConfigFile(testListenerConfigFile);
        slInfo.setScannerJar(buildScannerJar);
        slInfo.setInheritedBuild(inheritedBuild);

        slInfo.setLogEnabled(logEnabled);
        slInfo.setLogLevel(logLevel);
        slInfo.setLogToFile(logToFile);
        slInfo.setLogFolder(logFolder);


        MavenIntegrationInfo info = new MavenIntegrationInfo();
        info.setTestingFramework(testingFramework);
        info.setSeaLightsPluginInfo(slInfo);
        info.setPomFilePath(pomPath);

        MavenIntegration mavenIntegration = new MavenIntegration(listener.getLogger(), info);
        mavenIntegration.integrate();

        return env;
    }

    public DescriptorImpl getDescriptor() {
        Jenkins jenkinsInstance = Jenkins.getInstance();
        if (jenkinsInstance != null){
            Descriptor desc = jenkinsInstance.getDescriptorOrDie(getClass());
            if (desc != null){
                return (DescriptorImpl)desc;
            }
        }
        return new DescriptorImpl();
    }

    public boolean isEnable() {
        return enable;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getBranch() {
        return branch;
    }

    public String getTestingFramework()
    {
        return testingFramework;
    }

    public String getProjectType() {
        return projectType;
    }

    public String getPomPath() {
        return pomPath;
    }

    public String getPackagesincluded() {
        return packagesincluded;
    }

    public String getPackagesexcluded() {
        return packagesexcluded;
    }

    public String getFilesincluded() {
        return filesincluded;
    }

    public String getFilesexcluded() {
        return filesexcluded;
    }

    public String getRelativePathToEffectivePom() {
        return relativePathToEffectivePom;
    }

    public String getWorkspacepath() {
        return workspacepath;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public String getBuildScannerJar() {
        return buildScannerJar;
    }

    public String getTestListenerJar() {
        return testListenerJar;
    }

    public String getTestListenerConfigFile() {
        return testListenerConfigFile;
    }

    public boolean isInheritedBuild() {
        return inheritedBuild;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public boolean isLogToFile() {
        return logToFile;
    }

    public String getLogFolder() {
        return logFolder;
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        private String customerId;
        private String url;
        private String proxy;
        private boolean enable;

        public DescriptorImpl() {
            super(SeaLightsJenkinsBuildWrapper.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Sealights properties";
        }

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            enable = json.getBoolean("enable");
            customerId = json.getString("customerId");
            url = json.getString("url");
            proxy = json.getString("proxy");
            save();
            return super.configure(req, json);
        }


        public boolean isEnable(){
            return enable;
        }


        public String getUrl() {
            return url;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getProxy() {
            return proxy;
        }

        public void setProxy(String proxy) {
            this.proxy = proxy;
        }

        public ListBoxModel doFillProjectTypesItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Maven","");
            return items;
        }

//        public ListBoxModel doFillTestingFrameworkItems() {
//            ListBoxModel items = new ListBoxModel();
//            items.add("testng");
//            items.add("junit");
//            return items;
//        }


    }
}