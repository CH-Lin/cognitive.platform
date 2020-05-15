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
 * @author Che-Hung Lin
 * @version 0.1
 * @License GNU General Public License, version 3 (GPL-3.0)
 */

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ErrorHandleFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger(ErrorHandleFilter.class.getName());

	@Override
	public void destroy() {
		// ...
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			// TODO filter codes

			chain.doFilter(request, response);

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage(), ex);
		}

	}

}