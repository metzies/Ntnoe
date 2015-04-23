/*
 * Ce fichier fait partie du projet NTNOE sur Android.
 * 
 * Les développeurs mettent ce projet à disposition de tous sous les conditions suivantes:
 * 
 * Vous avez le droit d'examiner et tester le code.
 * 
 * Vous avez le droit de copier et modifier le code à condition de citer clairement et publiquement vos sources.
 * 
 * Vous n'avez pas le droit de désigner ce code (modifié ou non) comme entièrement le votre.
 * 
 * Vous n'avez pas le droit de commercialiser ce code sans le consentement écrit des développeurs originaux.
 * 
 * Les développeurs gardent le droit de regard sur toute oeuvre dérivée de ce code là.
 * 
 * Les développeurs originaux fournissent ce code tel quel, sans garantie.
 * 
 * Pour plus d'information sur les termes de la licence, contacter pierre-yves.bigourdan@supelec.fr et kayssar.daher@supelec.fr
 * 
 * Le non-respect de ces termes vous expose à des poursuites judiciaires.
 * 
 */
package com.metzies.ntnoe;

public class Option implements Comparable<Option> {
	/*
	 * module de navigation de fichiers
	 */
	private String name;
	private String data;
	private String path;

	public Option(String n, String d, String p) {
		name = n;
		data = d;
		path = p;
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public String getPath() {
		return path;
	}

	@Override
	public int compareTo(Option o) {
		if (this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
		else
			throw new IllegalArgumentException();
	}
}
