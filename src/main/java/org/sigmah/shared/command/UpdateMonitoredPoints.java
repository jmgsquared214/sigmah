package org.sigmah.shared.command;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Arrays;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateMonitoredPoints extends AbstractCommand<ListResult<MonitoredPointDTO>> {

	private List<MonitoredPointDTO> list;

	public UpdateMonitoredPoints() {
		// Serialization.
	}

	public UpdateMonitoredPoints(final MonitoredPointDTO... points) {
		this(points != null ? Arrays.asList(points) : null);
	}

	public UpdateMonitoredPoints(final List<MonitoredPointDTO> list) {
		this.list = list;
	}

	public List<MonitoredPointDTO> getList() {
		return list;
	}

}
