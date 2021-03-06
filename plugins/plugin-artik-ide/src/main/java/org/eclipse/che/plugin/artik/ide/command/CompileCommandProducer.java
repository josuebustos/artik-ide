/*******************************************************************************
 * Copyright (c) 2016 Samsung Electronics Co., Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - Initial implementation
 *   Samsung Electronics Co., Ltd. - Initial implementation
 *******************************************************************************/
package org.eclipse.che.plugin.artik.ide.command;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.Resource;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.command.custom.CustomCommandType;

import java.util.Collections;
import java.util.Set;

/**
 * Produces command for compiling the current C-file.
 * Applicable when current project type is C and current file's extension is C.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class CompileCommandProducer extends AbstractArtikProducer {

    public static final String COMMAND_NAME     = "Compile";
    public static final String COMMAND_TEMPLATE =
            "cd ${explorer.current.file.parent.path} && ${compilation.options} -o ${binary.name} ${explorer.current.file.name}";

    @Inject
    public CompileCommandProducer(CustomCommandType customCommandType, DtoFactory dtoFactory, AppContext appContext) {
        super(COMMAND_NAME, customCommandType, dtoFactory, appContext);
    }

    @Override
    public boolean isApplicable() {
        if (!super.isApplicable()) {
            return false;
        }

        final Optional<Resource> selectedResourceOptional = getSelectedResource();
        if (!selectedResourceOptional.isPresent()) {
            return false;
        }

        final Resource selectedResource = selectedResourceOptional.get();
        if (selectedResource.isFile()) {
            String ext = ((File)selectedResource).getExtension();
            return "c".equals(ext);
        }

        return false;
    }

    @Override
    protected String getCommandLine(Machine machine) {
        return COMMAND_TEMPLATE;
    }

    @Override
    public Set<String> getMachineTypes() {
        return Collections.emptySet();
    }
}
