/*******************************************************************************
 * Copyright (c) 2012, 2014 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.process.supplier.groovy.ui.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import net.chemclipse.logging.core.Logger;
import net.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import net.chemclipse.support.events.IPerspectiveAndViewIds;
import net.openchrom.chromatogram.msd.process.supplier.groovy.ui.internal.preferences.IConstants;
import net.openchrom.chromatogram.msd.process.supplier.groovy.preferences.PreferenceSupplier;

public class ExecuteGroovyScriptHandler {

	private static final Logger logger = Logger.getLogger(ExecuteGroovyScriptHandler.class);

	@Execute
	public void execute() {

		/*
		 * Get the default streams.
		 */
		PrintStream out = System.out;
		PrintStream err = System.err;
		/*
		 * Try to select and show the perspective and view.
		 */
		PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, IPerspectiveAndViewIds.VIEW_CONSOLE);
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

		/*
		 * Display in the console what's happens
		 */
		System.out.println(e);
		logger.warn(e);
	}

	private MessageConsole getMessageConsole() {

		MessageConsole messageConsole;
		String consoleName = "Groovy-Script Output";
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

		File groovyFile = null;
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		IEditorInput editorInput = editorPart.getEditorInput();
		/*
		 * Get the file.
		 */
		if(editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput)editorInput;
			IFile file = fileEditorInput.getFile();
			groovyFile = file.getLocation().toFile();
		}
		/*
		 * Check
		 */
		if(groovyFile == null || !groovyFile.exists()) {
			throw new FileNotFoundException();
		}
		return groovyFile;
	}
}
