package io.sealights.plugins.sealightsjenkins.buildsteps.commands.executors;

import io.sealights.plugins.sealightsjenkins.buildsteps.commands.CommandMode;
import io.sealights.plugins.sealightsjenkins.buildsteps.commands.entities.*;
import io.sealights.plugins.sealightsjenkins.utils.Logger;

/**
 * A factory to create command executors.
 */
public class CommandExecutorsFactory {

    public ICommandExecutor createExecutor(Logger logger, BaseCommandArguments baseArgs) {
        ICommandExecutor executor;

        if (baseArgs == null || baseArgs.getMode() == null) {
            logger.error("BaseCommandArguments or mode is 'null'");
            executor = new NullCommandExecutor();
        } else {
            CommandMode mode = baseArgs.getMode();

            if (CommandModes.Start.equals(mode.getCurrentMode())) {
                StartCommandArguments startCommandArguments = getStartCommandArguments(baseArgs);
                executor = new StartCommandExecutor(logger, startCommandArguments);
            } else if (CommandModes.End.equals(mode.getCurrentMode())) {
                EndCommandArguments endCommandArguments = getEndCommandArguments(baseArgs);
                executor = new EndCommandExecutor(logger, endCommandArguments);
            } else if (CommandModes.UploadReports.equals(mode.getCurrentMode())) {
                UploadReportsCommandArguments uploadReportsCommandArguments = getUploadReportsCommandArguments(baseArgs);
                executor = new UploadReportsCommandExecutor(logger, uploadReportsCommandArguments);
            } else {
                logger.error("Something's wrong!");
                executor = new NullCommandExecutor();
            }
        }
        return executor;
    }

    private StartCommandArguments getStartCommandArguments(BaseCommandArguments baseArgs) {
        CommandMode.StartView startView = (CommandMode.StartView) baseArgs.getMode();
        return new StartCommandArguments(baseArgs, startView.getNewEnvironment());
    }

    private EndCommandArguments getEndCommandArguments(BaseCommandArguments baseArgs) {
        return new EndCommandArguments(baseArgs);
    }

    private UploadReportsCommandArguments getUploadReportsCommandArguments(BaseCommandArguments baseArgs) {
        CommandMode.UploadReportsView uploadReportsView = (CommandMode.UploadReportsView) baseArgs.getMode();
        return new UploadReportsCommandArguments(
                baseArgs,
                uploadReportsView.getReportFiles(),
                uploadReportsView.getReportsFolders(),
                uploadReportsView.getHasMoreRequests(),
                uploadReportsView.getSource());
    }

}
