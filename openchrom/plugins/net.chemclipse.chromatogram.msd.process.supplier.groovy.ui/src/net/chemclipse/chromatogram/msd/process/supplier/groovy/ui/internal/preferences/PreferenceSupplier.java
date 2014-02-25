/*******************************************************************************
 * Copyright (c) 2012, 2014 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package net.chemclipse.chromatogram.msd.process.supplier.groovy.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import net.chemclipse.chromatogram.msd.process.supplier.groovy.preferences.ProcessPreferences;
import net.chemclipse.chromatogram.msd.process.supplier.groovy.ui.Activator;

public class PreferenceSupplier {

	/*
	 * Use only static methods.
	 */
	private PreferenceSupplier() {

	}

	/**
	 * Returns whether to use the file dialog or not.
	 * 
	 * @return boolean
	 */
	public static boolean useFileDialog() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		return preferenceStore.getBoolean(ProcessPreferences.P_USE_FILE_DIALOG);
	}
}
