/*******************************************************************************
 * Copyright (c) 2012, 2014 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * chemclipse - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.msd.process.supplier.groovy.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import net.chemclipse.support.preferences.IPreferenceSupplier;
import net.openchrom.chromatogram.msd.process.supplier.groovy.Activator;

public class PreferenceSupplier implements IPreferenceSupplier {

	public static final String P_USE_FILE_DIALOG = "useFileDialog";
	public static final boolean DEF_USE_FILE_DIALOG = false;
	//
	private static IPreferenceSupplier preferenceSupplier;

	public static IPreferenceSupplier INSTANCE() {

		if(preferenceSupplier == null) {
			preferenceSupplier = new PreferenceSupplier();
		}
		return preferenceSupplier;
	}

	@Override
	public IScopeContext getScopeContext() {

		return InstanceScope.INSTANCE;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
	}

	@Override
	public Map<String, String> getDefaultValues() {

		Map<String, String> defaultValues = new HashMap<String, String>();
		defaultValues.put(P_USE_FILE_DIALOG, Boolean.toString(DEF_USE_FILE_DIALOG));
		return defaultValues;
	}

	@Override
	public IEclipsePreferences getPreferences() {

		return getScopeContext().getNode(getPreferenceNode());
	}
	
	/**
	 * Returns whether to use the file dialog or not.
	 * 
	 * @return boolean
	 */
	public static boolean useFileDialog() {

		IEclipsePreferences preferences = INSTANCE().getPreferences();
		return preferences.getBoolean(P_USE_FILE_DIALOG, DEF_USE_FILE_DIALOG);
	}
}
