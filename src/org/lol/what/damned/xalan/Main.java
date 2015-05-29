package org.lol.what.damned.xalan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jd.core.Decompiler;
import jd.core.loader.Loader;
import jd.core.loader.LoaderException;
import jd.core.process.*;
import jd.core.preferences.*;
import jd.core.printer.Printer;
import jd.common.loader.*;
import jd.common.preferences.CommonPreferences;
import jd.common.printer.text.PlainTextPrinter;

public class Main {

	private static String TMP_FILE_NAME = "tmpFile.txt";
	private static String INPUT_FILE_NAME = "file.list";
	private static String OUTPUT_FILE_NAME = "result.txt";

	private static ArrayList<String> getFilesFromList()
			throws FileNotFoundException {
		File inputFile = new File(INPUT_FILE_NAME);
		FileInputStream fis = new FileInputStream(inputFile);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

		ArrayList<String> result = new ArrayList<String>();

		try {
			String nextStr = br.readLine();
			while (nextStr != null) {
				result.add(nextStr);
				nextStr = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static CharSequence readFile(InputStreamReader isr) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(isr);
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null) {
				buffer.append(line).append('\n');
			}
			// Chomp the last newline
			buffer.deleteCharAt(buffer.length() - 1);
			return buffer;
		} catch (IOException e) {
			return "";
		} finally {

		}
	}

	private static void doWorkWithTmpFile(BufferedWriter bw) throws IOException {

		File inputFile = new File(TMP_FILE_NAME);
		FileInputStream fis = new FileInputStream(inputFile);
		InputStreamReader isr = new InputStreamReader(fis);
		CharSequence charSeq = readFile(isr);
		Pattern importPattern = Pattern
				.compile("(import (((\\w+\\.)+\\w+)|((\\w+\\.)+\\w+\\.\\*)));*");
		Pattern packagePattern = Pattern
				.compile("(package ((\\w+\\.)+\\w+));*");
		Matcher importMatch = importPattern.matcher(charSeq);
		Matcher packageMatch = packagePattern.matcher(charSeq);
		if (packageMatch.find()) {
			String packageStr = packageMatch.group();
			// System.out
			// .println(packageStr.substring(8, packageStr.length() - 1));
			bw.write(packageStr.substring(8, packageStr.length() - 1) + "\n");
		}
		int count = 0;
		while (importMatch.find()) {
			count++;
			String importStr = importMatch.group();
			// System.out.println("\t"
			// + importStr.substring(7, importStr.length() - 1));
			bw.write(importStr.substring(7, importStr.length() - 1)
					+ ",");
		}
		bw.write("\n\n");
	}

	public static void main(String[] args) throws LoaderException, IOException {
		File inputDir = new File("/");
		if (!inputDir.exists()) {
			System.out
					.println("File " + inputDir.toString() + " do not exists");
		}
		File outputFile = new File(OUTPUT_FILE_NAME);
		outputFile.delete();
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);

		ArrayList<String> files = getFilesFromList();

		Loader loader = new DirectoryLoader(inputDir);
		File tmpDile = new File(TMP_FILE_NAME);
		Decompiler decompiler = new DecompilerImpl();
		for (int i = 0; i < files.size(); i++) {
			tmpDile.delete();
			PrintStream stream = new PrintStream(tmpDile);
			Printer printer = new PlainTextPrinter(new CommonPreferences(),
					stream);
			decompiler.decompile(new Preferences(), loader, printer,
					files.get(i));
			doWorkWithTmpFile(bw);
			//
		}
		System.out.println("Finished");
	}

}
