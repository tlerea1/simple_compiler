import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * SIMPLE compiler.
 * @author tuvialerea
 *
 */
public class sc {
	/**
	 * The SIMPLE compiler main.
	 * @param args The command line arguments. If run with none, a usage message will print
	 */
	public static void main(String[] args) {
		try {
			if (args.length > 3) {
				throw new RuntimeException("Usage - ./sc [\"-\" (\"s\"|\"c\"|\"t\"|\"a\"|\"i\")] [\"-g\"] [filename]");
			}
			if (args.length == 0) {
				throw new RuntimeException("Usage - ./sc [\"-\" (\"s\"|\"c\"|\"t\"|\"a\"|\"i\")] [\"-g\"] [filename]");
			}
			if (args.length == 1) { // Either an option and take from stdin, or a filename, run compiler
				if (args[0].startsWith("-")) { // If option
					parseOption(args[0], null, false);
				} else { // Get filename
					File input = new File(args[0]);
					if (! input.exists()) {
						fileDoesNotExistError(args[0]);
					}
					//TODO: compile input program
					throw new RuntimeException("No option is not yet implemented"); // TODO remove later
				}
			} else if (args.length == 2){ // If option and filename
				if (isOption(args[1])) {
					if (args[1].equals("-g")) {
						parseOption(args[0], null, true);
					}
				} else {
					File input = new File(args[1]);
					if (! input.exists()) {
						fileDoesNotExistError(args[1]);
					}
					parseOption(args[0], args[1], false);
				}
			} else {
				File input = new File(args[2]);
				if (! input.exists()) {
					fileDoesNotExistError(args[2]);
				}
				if (args[1].equals("-g")) {
					parseOption(args[0], args[2], true);
				} else {
					throw new RuntimeException("unrecognized option " + args[1]);
				}
			}
		} catch (Exception e) {
			System.err.println("error: " + e.getMessage());
		}
	}

	/**
	 * Function for throwing a file does not exist error
	 * @param filename the filename that doesnt exist
	 */
	private static void fileDoesNotExistError(String filename) {
		throw new RuntimeException("File " + filename + " does not exist");
	}

	/**
	 * Function to run different parts of the compiler for different options.
	 * @param option the option to run.
	 * @param filename the filename to get source code from, if null use stdin.
	 * @throws IOException If IO with file / stdin fails
	 */
	private static void parseOption(String option, String filename, boolean graphical) throws IOException {
		if (! option.startsWith("-") || option.length() != 2) { // sanity check of option
			throw new RuntimeException("invalid option");
		}
		if (graphical && option.charAt(1) != 'c') {
			throw new RuntimeException("cannot use -g without -c");
		}
		switch(option.charAt(1)) {
			case 's': // if -s
				Scanner sc;
				if (filename == null) {
					sc = new Scanner();
				} else {
					sc = new Scanner(filename);
				}
				Collection<Token> tokens = sc.all();
				for (Token t : tokens) {
					System.out.println(t);
				}
				break;
			case 'c': // -c
				Scanner s;
				if (filename == null) {
					sc = new Scanner();
				} else {
					sc = new Scanner(filename);
				}
				Parser p = new Parser(sc, graphical);
				p.parse();
				System.out.println(p);
				break;
				
			case 't': // -t
			case 'a': // -a
			case 'i': // -i
				throw new RuntimeException("Unimplemented Option"); // Will change
			default:
				throw new RuntimeException("invalid option"); // Not a preset option
		}
	}
	
	private static boolean isOption(String s) {
		return s.startsWith("-");
	}
}
