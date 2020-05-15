package prj.cognitive;

/**
 *  Copyright (C)2019 Che-Hung Lin.
 * 
 *  This file is part of Trip Builder.
 *
 *  Trip Builder is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Trip Builder is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Trip Builder.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Site mesh filter
 *
 * @author Che-Hung Lin
 * @version 0.1
 * @License GNU General Public License, version 3 (GPL-3.0)
 */

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;

public class TripSiteMeshFilter extends ConfigurableSiteMeshFilter {

	@Override
	protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
		builder.addDecoratorPath("/service", "/WEB-INF/jsp/webui/service_decorator.jsp");
		builder.addDecoratorPath("/table*", "/WEB-INF/jsp/webui/table_decorator.jsp");
	}

}
