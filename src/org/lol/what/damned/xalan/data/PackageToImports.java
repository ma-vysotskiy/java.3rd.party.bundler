package org.lol.what.damned.xalan.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PackageToImports {

	private Map<String, Set<String>> data;

	private void replacePackage(String newPackage, String oldPackage) {
		Set<String> oldData = data.get(oldPackage);
		data.remove(oldPackage);
		data.put(newPackage, oldData);
	}
	
	private void newPackage(String newPackage) {
		data.put(newPackage, new HashSet<String>());
	}

	public PackageToImports() {
		data = new HashMap<String, Set<String>>();
	}
	
	public String addPackage(String packagaStr) {
		boolean found = false;
		Set<Map.Entry<String, Set<String>>> set = data.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Set<String>> entry = (Entry<String, Set<String>>) it
					.next();
			String curPackage = entry.getKey();
			if (curPackage.startsWith(packagaStr) == true) {
				replacePackage(packagaStr, curPackage);
				return packagaStr;
			} else if (packagaStr.startsWith(curPackage) == true) {
				return curPackage;
			}
		}
		if(found == false) {
			newPackage(packagaStr);
		}
		return packagaStr;
	}
	
	public void addImport(String packageStr, String importStr) {
		Set<String> packageData = data.get(packageStr);
		packageData.add(importStr);
	}
	
	public Map<String, Set<String>> getResult() {
		return data;
	}
}
