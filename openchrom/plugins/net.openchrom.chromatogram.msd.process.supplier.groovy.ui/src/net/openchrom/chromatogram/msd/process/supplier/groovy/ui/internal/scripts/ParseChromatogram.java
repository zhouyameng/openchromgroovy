/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.process.supplier.groovy.ui.internal.scripts;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.chromatogram.IChromatogramMSDImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public class ParseChromatogram {

	public ParseChromatogram() {
		File file = new File("Chromatogram1.CDF");
		IProgressMonitor monitor = new NullProgressMonitor();
		IChromatogramMSDImportConverterProcessingInfo processingInfo = null;
		try {
			/*
			 * Try to process the chromatogram.
			 */
			System.out.println(file);
			processingInfo = ChromatogramConverterMSD.convert(file, monitor);
			IChromatogramMSD chromatogram = processingInfo.getChromatogram();
			int numberOfScans = chromatogram.getNumberOfScans();
			for(int i = 1; i <= numberOfScans; i++) {
				IScan scan = chromatogram.getScan(i);
				System.out.println("SCAN [" + i + "] - TIC: " + scan.getTotalSignal());
			}
		} catch(TypeCastException e) {
			/*
			 * Print error messages.
			 */
			if(processingInfo != null) {
				List<IProcessingMessage> messages = processingInfo.getMessages();
				for(int i = 0; i < messages.size(); i++) {
					IProcessingMessage message = messages.get(i);
					System.out.println(message.getDescription() + "\t" + message.getMessage());
				}
			}
		}
	}
}
