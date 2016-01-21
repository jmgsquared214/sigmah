package org.sigmah.shared.command.result;

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

import org.sigmah.server.domain.report.ProjectReportModel;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ModelReference extends BaseModelData implements Result {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6681884453456869082L;

	public ModelReference() {
		// Serialization.
	}

	public ModelReference(ProjectReportModel model) {
		this.set("id", model.getId());
		this.set("name", model.getName());
	}

	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		this.set("id", id);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		this.set("name", name);
	}

}
