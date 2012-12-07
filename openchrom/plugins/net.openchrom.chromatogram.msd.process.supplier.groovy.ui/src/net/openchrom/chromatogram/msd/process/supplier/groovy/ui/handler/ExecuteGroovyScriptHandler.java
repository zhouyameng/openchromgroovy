/*******************************************************************************
 * Copyright (c) 2012 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * openchrom - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.process.supplier.groovy.ui.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.inject.Named;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import net.openchrom.chromatogram.msd.process.supplier.groovy.ui.internal.preferences.IConstants;
import net.openchrom.chromatogram.msd.process.supplier.groovy.ui.internal.preferences.PreferenceSupplier;
import net.openchrom.logging.core.Logger;

@SuppressWarnings("restriction")
public class ExecuteGroovyScriptHandler {

	private static final Logger logger = Logger.getLogger(ExecuteGroovyScriptHandler.class);

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {

		/*
		 * Get the default streams.
		 */
		PrintStream out = System.out;
		PrintStream err = System.err;
		/*
		 * Try to select and show the perspective and view.
		 */
		// PerspectiveChooserSupport.focusPerspectiveAndView(ChromatogramMSPerspective.ID, IConsoleConstants.ID_CONSOLE_VIEW);
		/*
		 * Execute script
		 */
		try {
			/*
			 * Set the out and error stream to the console.
			 */
			MessageConsole messageConsole = getMessageConsole();
			MessageConsoleStream messageConsoleStream = messageConsole.newMessageStream();
			System.setOut(new PrintStream(messageConsoleStream));
			System.setErr(new PrintStream(messageConsoleStream));
			//
			File file = getGroovyScriptFile();
			executeGroovyScript(file);
		} catch(Exception e) {
			catchError(e);
		} finally {
			/*
			 * Reset the output streams.
			 */
			System.setOut(out);
			System.setErr(err);
		}
	}

	private void catchError(Exception e) {

		System.out.println(e); // display in the console what's happens
		logger.warn(e);
	}

	private MessageConsole getMessageConsole() {

		MessageConsole messageConsole;
		String consoleName = "OpenChrom Groovy-Script Output";
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		/*
		 * Is there an existing console?
		 */
		for(IConsole console : consoleManager.getConsoles()) {
			if(console.getName().equals(consoleName)) {
				if(console instanceof MessageConsole) {
					messageConsole = (MessageConsole)console;
					messageConsole.activate();
					return messageConsole;
				}
			}
		}
		/*
		 * No existing console was detected, hence create one.
		 */
		messageConsole = new MessageConsole(consoleName, null);
		consoleManager.addConsoles(new IConsole[]{messageConsole});
		messageConsole.activate();
		return messageConsole;
	}

	private void executeGroovyScript(File file) throws CompilationFailedException, IOException {

		GroovyShell groovyShell = new GroovyShell();
		if(file != null) {
			Script script = groovyShell.parse(file);
			script.run();
		}
	}

	private File getGroovyScriptFile() throws FileNotFoundException, MalformedURLException, URISyntaxException {

		File file;
		if(PreferenceSupplier.useFileDialog()) {
			file = getFileFromFileDialog();
		} else {
			file = getFileFromActiveEditor();
		}
		return file;
	}

	private File getFileFromFileDialog() throws FileNotFoundException {

		Shell shell = Display.getCurrent().getActiveShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setText("Groovy Script");
		fileDialog.setFilterExtensions(new String[]{IConstants.FILTER_EXTENSION});
		fileDialog.setFilterNames(new String[]{IConstants.FILTER_NAME});
		String fileName = fileDialog.open();
		if(fileName == null || fileName.equals("")) {
			throw new FileNotFoundException();
		}
		return new File(fileName);
	}

	private File getFileFromActiveEditor() throws FileNotFoundException, MalformedURLException, URISyntaxException {

		System.out.println("OpenChrom Groovy get file from open editor");
		return new File("");
		// if(editorInput instanceof IFileEditorInput) {
		// IFile iFile = ((IFileEditorInput)editorInput).getFile();
		// URL url = iFile.getRawLocationURI().toURL();
		// File file = new File(url.toURI());
		// if(file.getName().endsWith(IConstants.FILE_EXTENSION)) {
		// return file;
		// } else {
		// throw new FileNotFoundException();
		// }
		// } else {
		// throw new FileNotFoundException();
		// }
	}
}
