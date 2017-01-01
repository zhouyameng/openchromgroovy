/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.process.supplier.groovy.ui.wizards;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class GroovyScriptWizard extends AbstractFileWizard {

	private static final Logger logger = Logger.getLogger(GroovyScriptWizard.class);

	public GroovyScriptWizard() {
		super("GroovyScript", ".groovy");
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Groovy Script", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		/*
		 * Initialize a simple batch process job.
		 */
		try {
			/*
			 * Try to get the demo script.
			 */
			BufferedInputStream bufferedInputStream = new BufferedInputStream(GroovyScriptWizard.class.getResourceAsStream("parse-chromatogram.groovy"));
			if(file.exists()) {
				file.setContents(bufferedInputStream, true, true, monitor);
			} else {
				file.create(bufferedInputStream, true, monitor);
			}
			bufferedInputStream.close();
		} catch(IOException e) {
			logger.warn(e);
		}
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}