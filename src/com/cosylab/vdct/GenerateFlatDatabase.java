/*
 * Copyright (c) 2003 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package com.cosylab.vdct;

import com.cosylab.vdct.graphics.DrawingSurface;
import com.cosylab.vdct.graphics.objects.Group;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @author ilist
 */
public class GenerateFlatDatabase
{
	private static File dbdFile = null;
	private static File vdbFile = null;
	private static File dbFile = null;

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args)
	{
		if (!parseCommandLine(args)) {
			return;
		}

		if (!semanticCheck()) {
			return;
		}

		Console.setInstance(new StdoutConsole());
		flatDatabase();
	}

	/**
	 * DOCUMENT ME!
	 */
	public static void flatDatabase()
	{
		try {
			DrawingSurface drawingSurface = new DrawingSurface();

			if (dbdFile != null) {
				drawingSurface.openDBD(dbdFile,
				    com.cosylab.vdct.DataProvider.getInstance().getDbdDB() != null);
			}

			System.out.println("Loading VDB files.\n");
			drawingSurface.open(vdbFile);
			System.out.println("Generating flat database.\n");
			Group.save(Group.getRoot(), dbFile, true);
			System.out.println("Done.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static boolean parseCommandLine(String[] args)
	{
		/*
		 * 0 - options
		 * 1 - first file
		 * 2 - second file
		 * 3 - eof
		 * */
		int state = 0;

		for (int i = 0; i < args.length; i++) {
			switch (state) {
			case 0:

				if (args[i].equals("--")) {
					state = 1;

					break;
				}

				if (args[i].startsWith("-")) {
					if (args[i].equals("-d") || args[i].equals("--dbd-file")) { //dbd file
						i++;

						if (i >= args.length) {
							System.err.println("Missing .dbd file name.");

							return false;
						}

						dbdFile = new File(args[i]);
						continue;
					} else if (args[i].equals("-h") || args[i].equals("--help")) {
						printHelp();

						return false;
					} else if (args[i].equals("-v")
					    || args[i].equals("--version")) {
						printVersion();

						return false;
					} else {
						System.err.println("Invalid parameter: " + args[i]);

						return false;
					}
				}

			// no break
			case 1:
				vdbFile = new File(args[i]);
				state = 2;

				break;

			case 2:
				dbFile = new File(args[i]);
				state = 3;

				break;

			default:
				System.err.println("Too many parameters.");

				return false;
			}
		}

		if (state != 3) {
			System.err.println("Missing parameters.");

			return false;
		}

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static boolean semanticCheck()
	{
		final String notExist = "File does not exist: ";
		final String notWrite = "File cannot be written: ";
		final String notRead = "File cannot be read: ";

		if (dbdFile != null) {
			if (!dbdFile.exists()) {
				System.err.println(notExist + dbdFile);

				return false;
			}

			if (!dbdFile.canRead()) {
				System.err.println(notRead + dbdFile);

				return false;
			}
		}

		if (!vdbFile.exists()) {
			System.err.println(notExist + vdbFile);

			return false;
		}

		if (!vdbFile.canRead()) {
			System.err.println(notRead + vdbFile);

			return false;
		}

		if (dbFile.exists()) {
			if (!dbFile.canWrite()) {
				System.err.println(notWrite + dbFile);

				return false;
			} else {
				System.out.println("File will be overwritten: " + dbFile + "\n");
			}
		}

		return true;
	}

	/**
	 * DOCUMENT ME!
	 */
	public static void printHelp()
	{
		System.out.println("Usage: flatdb [OPTION]... input.vdb output.db\n");
		System.out.println("Options:");
		System.out.println(" -d or --dbd-file input.dbd");
		System.out.println(
		    "   loads a specific .dbd file before generating flat database\n");
		System.out.println(" -h or --help");
		System.out.println("   displays these lines here\n");
		System.out.println(" -v or --version");
		System.out.println("   display version of this tool");
	}

	/**
	 * DOCUMENT ME!
	 */
	public static void printVersion()
	{
		System.out.println("Visual Database Configuration Tool v"
		    + Version.VERSION);
		System.out.println("build " + Version.BUILD);
		System.out.println("\nCopyright (c) 2003 by Cosylab d.o.o.");
	}
}

/* __oOo__ */
