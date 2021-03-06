package org.dataagg.util;

import java.io.File;
import java.util.Iterator;

import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;

public class FindFileUtil {
	public static Iterator<File> search(boolean recursive, boolean includeDirs, String dir) {
		FindFile ff = create(recursive, includeDirs).searchPath(dir);
		Iterator<File> iterator = ff.iterator();
		return iterator;
	}

	public static Iterator<File> search(boolean recursive, boolean includeDirs, String dir, String fileName) {
		FindFile ff = create(recursive, includeDirs).include(fileName).searchPath(dir);
		return ff.iterator();
	}

	public static Iterator<File> search(boolean recursive, boolean includeDirs, File dir) {
		FindFile ff = create(recursive, includeDirs).searchPath(dir);
		Iterator<File> iterator = ff.iterator();
		return iterator;
	}

	public static Iterator<File> search(boolean recursive, boolean includeDirs, File dir, String fileName) {
		FindFile ff = create(recursive, includeDirs).include(fileName).searchPath(dir);
		return ff.iterator();
	}

	public static FindFile create(boolean recursive, boolean includeDirs) {
		return new WildcardFindFile().recursive(recursive).includeDirs(includeDirs).includeFiles(true);
	}
}
