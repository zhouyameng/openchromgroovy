/*******************************************************************************
 * Copyright (c) 2014 Dr. Philip Wenig.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import net.openchrom.chromatogram.msd.process.supplier.groovy.ui.PathResolver;
import net.openchrom.logging.core.Logger;
import net.openchrom.support.ui.wizards.AbstractFileWizard;

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
			String scriptPathname = PathResolver.getAbsolutePath("scripts/parse-chromatogram.groovy");
			File scriptFile = new File(scriptPathname);
			InputStream stream;
			if(scriptFile.exists()) {
				stream = new FileInputStream(scriptFile);
			} else {
				stream = new ByteArrayInputStream("Groovy Script".getBytes());
			}
			/*
			 * Write the stream to the selected file.
			 */
			if(file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
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