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

import org.eclipse.che.api.core.model.machine.Machine;
import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.extension.machine.client.command.custom.CustomCommandConfiguration;
import org.eclipse.che.ide.extension.machine.client.command.custom.CustomCommandType;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AbstractArtikProducer}.
 *
 * @author Artem Zatsarynnyi
 */
public class AbstractArtikProducerTest extends BaseArtikProducerTest {

    private static final String NAME = "dummy";

    private AbstractArtikProducer producer;

    @Before
    public void setUp() {
        super.setUp();

        producer = new DummyArtikProducer(NAME, customCommandType, dtoFactory, appContext);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(NAME, producer.getName());
    }

    @Test
    public void shouldBeApplicableForC_ProjectType() throws Exception {
        when(currentProject.isTypeOf(eq("c"))).thenReturn(true);

        assertTrue(producer.isApplicable());
    }

    @Test
    public void shouldNotBeApplicableForNonC_ProjectType() throws Exception {
        when(currentProject.isTypeOf(eq("maven"))).thenReturn(true);

        assertFalse(producer.isApplicable());
    }

    @Test
    public void shouldCreateCommand() throws Exception {
        // given
        CommandDto commandDto = mock(CommandDto.class);
        when(commandDto.withType(anyString())).thenReturn(commandDto);
        when(commandDto.withName(anyString())).thenReturn(commandDto);
        when(commandDto.withCommandLine(anyString())).thenReturn(commandDto);

        when(dtoFactory.createDto(any())).thenReturn(commandDto);

        when(customCommandType.getConfigurationFactory()).thenReturn(commandConfigurationFactory);
        when(customCommandType.getId()).thenReturn("custom");

        when(commandConfigurationFactory.createFromDto(any(CommandDto.class))).thenReturn(mock(CustomCommandConfiguration.class));

        // when
        producer.createCommand(mock(Machine.class));

        // then
        verify(commandDto).withName(eq(NAME));
        verify(commandDto).withType(eq("custom"));
        verify(commandDto).withCommandLine(eq("cmd"));
        verify(customCommandType).getConfigurationFactory();
        verify(commandConfigurationFactory).createFromDto(eq(commandDto));
    }

    private class DummyArtikProducer extends AbstractArtikProducer {

        DummyArtikProducer(String name, CustomCommandType customCommandType, DtoFactory dtoFactory, AppContext appContext) {
            super(name, customCommandType, dtoFactory, appContext);
        }

        @Override
        protected String getCommandLine(Machine machine) {
            return "cmd";
        }

        @Override
        public Set<String> getMachineTypes() {
            return Collections.emptySet();
        }
    }
}
