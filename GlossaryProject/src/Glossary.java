import java.util.Iterator;

import components.map.Map;
import components.map.Map1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * We read in a .txt file that has a bunch of terms, and definitions and then
 * uses that to create a html index page, that links to a seperate page
 * containing the term & the definition.
 *
 *
 * @author Ashim Dhakal
 *
 */
public final class Glossary {
    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Glossary() {
    }

    /**
     * The method creatingWholePage creates an HTML index page and links to
     * separate pages containing terms and their definitions. It takes three
     * parameters: fileData of type SimpleReader for input file data and out of
     * type SimpleWriter for outputting the created HTML page and also
     * nameOfIndexFile to help create a return to index, later.
     *
     * @param fileData
     *            the SimpleReader object containing the glossary data
     * @param out
     *            the SimpleWriter object to output the index page
     * @param nameOfIndexFile
     *            the name of the index file that will be used for returnToIndex
     */
    private static void creatingWholePage(SimpleReader fileData,
            SimpleWriter out, String nameOfIndexFile) {
        assert fileData != null : "fileData is null";
        assert out != null : "out is null";
        assert fileData.isOpen() : "fileData is closed";
        assert out.isOpen() : "out is closed";
        /*
         * assertions created above to ensure file data is valid, and the output
         * is valid, and that filedata isnt closed and output isnt closed.
         */

        // creating a map of terms and their definitions
        Map<String, String> glossary = new Map1L<>();
        String term = "";
        String definition = "";
        while (!fileData.atEOS()) {
            String line = fileData.nextLine().trim();
            if (line.isEmpty()) {
                /*
                 * blank line, end of definition as the terms r seperated by an
                 * empty line in the input file.
                 */

                if (!term.isEmpty()) {
                    glossary.add(term, definition);
                    term = "";
                    definition = "";
                }
            } else if (term.isEmpty()) { // start of new term
                term = line;
            } else { // continuation of current definition
                definition += line + " ";
            }
        }
        if (!term.isEmpty()) { // add last term and definition
            glossary.add(term, definition);
        }

        fileData.close();
        // file is being closed.

        // outputting the index page with links to separate pages for each term
        out.println("<html>");
        out.println("<head>");
        out.println("<h1>Sample Glossary <h1>");
        out.println("<title>Sample Glossary</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Index</h2>");
        out.println("<ul>");
        /*
         * ^^ html tags that are being outputted so file comes out as HTML
         * properly formatted
         */

        Iterator<Map.Pair<String, String>> iterating = glossary.iterator();
        // creating an iterator for the glossary
        // refer to Map slides in cse 2221 slide #: 59 for more details.
        /*
         * http://web.cse.ohio-state.edu/software/common/doc/components/map/Map.
         * html
         */

        while (iterating.hasNext()) {
            // looping through each entry in the glossary
            Map.Pair<String, String> entry = iterating.next();
            // current glossary entry retrieved ^
            String term1 = entry.key();
            String fileName = term1.replaceAll("\\s+", "") + ".html";
            // creating the file name that is then output
            out.println(
                    String.format(
                            "<li><a href=\"%s\"><span style=\"font-size:18pt;"
                                    + "\">%s</span></a></li>",
                            fileName, term1));

            // Create a separate page for the term and its definition
            SimpleWriter outputWebpage = new SimpleWriter1L(fileName);
            outputWebpage.println("<html>");
            outputWebpage.println("<head>");
            outputWebpage.println(String.format("<title>%s</title>", term1));
            outputWebpage.println("</head>");
            outputWebpage.println("<body>");
            outputWebpage.println(String.format(
                    "<h1 style=\"color:red; font-style:italic;\">%s</h1>",
                    term1));
            outputWebpage.println(String.format("<p>%s</p>", entry.value()));
            outputWebpage.println("</body>");
            outputWebpage.println("</html>");
            outputWebpage.println("<p><a href=\"" + nameOfIndexFile
                    + "\">Return to Index</a></p>");
            outputWebpage.close();
        }
        out.println("</ul>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        // get the index file name
        out.println("please enter the name of your input file: ");
        String inputFileName = in.nextLine();
        SimpleReader fileData = new SimpleReader1L(inputFileName);
        // name of output file
        out.println("enter name of output file: ");
        String fileName = in.nextLine();

        SimpleWriter output = new SimpleWriter1L(fileName);
        creatingWholePage(fileData, output, fileName);
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
    }
}
