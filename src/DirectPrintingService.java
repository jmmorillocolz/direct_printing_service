
/**
 * Alternative to an applet to print tickets on any printer available on the client.
 * Run as Java Web Start application. Prints directly without displaying dialog box.
 *
 * In this version pass parameters like: printer name, print values, scale, and text to be printed.
 * In future versions will be added other features.
 *
 * This JAVA application only works when it's invoked from a .jnlp file
 *
 * Main requirements:
 * Customer Side:
 * - Mozilla Firefox browser (recommended)
 * - Java 1.7 or higher
 * - OS: Windows 7 or higher, and Linux
 *
 * Server Side:
 * - Apache Server 2
 * - jnlp file with the proper configuration
 *
 * Remarks: Compile and sign required to run properly.
 *
 * @author José Miguel Morillo jmmorillocolz@gmail.com
 * @since 2017-09-05
 * @version 1.0.0
 *
 * @bugs In browsers google chrome, Microsoft Edge and Opera show window download dialog
 *
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.*;
import javax.print.PrintService;
import javax.swing.JOptionPane;
import javax.swing.JEditorPane;
import java.net.URLDecoder;

public class DirectPrintingService {

    /**
     * HTML content of the page to be printed
     */
    static String HTML_CONTENT;

    /**
     * Map with the definition of message types
     */
    private Map<String, Integer> TYPE_MESSAGES = new HashMap<String, Integer>();

    /**
     * Contains printing services
     */
    Map<String, PrintService> PRINTING_SERVICES = null;
    
    /**
     * Name of printer
     */
    private String printerName = null; 
    
    /**
     * Scale of printing, format: 0.00;0.00
     */
    private String printerScale = null;
    
    /**
     * Print settings. Format: x; y; width; height.
     * where:
     * x = print point in the X coordinate
     * y = print point in the Y coordinate
     * width = wide print area
     * height = print area at the top
     * 
     */
    private String printerValues = null; 
    
    /**
     * Stores the name of the printer
     * @param name 
     */
    protected void setPrinterName(String name){
        this.printerName = URLDecoder.decode(name);
    }
    
    /**
     * Return Printer Name
     * @return printerName
     */
    public String getPrinterName(){
        return this.printerName;
    }
    
    /**
     * Stores print settings
     * @param values 
     */
    protected void setPrinterValues(String values){
        this.printerValues = URLDecoder.decode(values);
    }
    
    /**
     * Returns the print settings
     * @return printerValues
     */
    public String getPrinterValues(){
        return this.printerValues;
    }
    
    /**
     * Stores the printing scale
     * @param scale 
     */
    protected void setPrinterScale(String scale){
        this.printerScale = URLDecoder.decode(scale);
    }
    
    /**
     * Returns the value of the print scale
     * @return printerScale
     */
     public String getPrinterScale(){
        return this.printerScale;
    }

    /**
     * Class Construct 
     */
    public DirectPrintingService() {
        try {
            TYPE_MESSAGES = this.TypeMessages();
            PRINTING_SERVICES = this.getPrintServices();
        } catch (Exception e) {
            ShowMessage("Application error", e.getMessage(), "error");
        }
    }

    /**
     * Main class method. Arguments are obtained from property
     * <argument> of the .jnlp file that invokes this class. It's character
     * required to pass the arguments in the following order:
     *
     * @param args the command line arguments: 
     * 0 - HTML CONTENT (encoded with encode () or its equivalent in any language)
     * 1 - PRINTER NAME 
     * 2 - PRINTER VALUES (X;Y;WIDTH;HEIGHT) 
     * 3 - SCALE (1.00;1.00)
     */
    public static void main(String[] args) {
        DirectPrintingService printdirect = new DirectPrintingService();

        try {
            String content = args[0];
            printdirect.setPrinterName(args[1]);
            printdirect.setPrinterValues(args[2]);
            printdirect.setPrinterScale(args[3]);

            if (content.isEmpty()) {
                throw new Exception("A required argument has not been found");
            }

            HTML_CONTENT = URLDecoder.decode(content, "UTF-8");
            printdirect.doPrint();

        } catch (Exception e) {
            printdirect.ShowMessage("Application error", e.getMessage(), "error");
        }
    }

    /**
     * Displays a message to the user. This message can be of several types.
     *
     * @param title Title corresponding to the message
     * @param msg Content of the message to be displayed
     * @param typeMessage Message Type
     * (plain,information,question,error,warning)
     */
    private void ShowMessage(String title, String msg, String typeMessage) {
        JOptionPane.showMessageDialog(null, msg, title, TYPE_MESSAGES.get(typeMessage));
    }

    /**
     * Create a Map with the types of messages that can be displayed to the user
     *
     * @return Map<String, Integer>
     */
    private Map<String, Integer> TypeMessages() {
        Map<String, Integer> types = new HashMap<String, Integer>();

        types.put("plain", JOptionPane.PLAIN_MESSAGE);
        types.put("information", JOptionPane.INFORMATION_MESSAGE);
        types.put("question", JOptionPane.QUESTION_MESSAGE);
        types.put("error", JOptionPane.ERROR_MESSAGE);
        types.put("warning", JOptionPane.WARNING_MESSAGE);

        return types;
    }

    /**
     * Gets the set of available printers. The returned map associates
     * an object name representing the printer.
     *
     * @return Map<String, PrintService>
     */
    private Map<String, PrintService> getPrintServices() {
        Map<String, PrintService> servicesMap = null;

        try {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPrintJobAccess();
            }

            PrintService[] services = PrinterJob.lookupPrintServices();

            if (services.length == 0) {
                throw new Exception("Print service not available");
            }

            servicesMap = new HashMap<String, PrintService>();

            for (PrintService service : services) {
                servicesMap.put(service.getName(), service);
            }

        } catch (Exception e) {
            this.ShowMessage("Application error", e.getMessage(), "error");
            System.exit(0);
        }

        return servicesMap;
    }

    /**
     * Separates all the tickets contained in the HTML and places them in a list
     *
     * @return List <MatchInfo> List with ticket information and name
     * of the printer associated with them
     */
    private List<MatchInfo> setPrintSettings() {
        List<MatchInfo> matches = new ArrayList<MatchInfo>();

            MatchInfo match = new MatchInfo(
               0,
                this.getPrinterName(),
                parseMargins(this.getPrinterValues()),
                parseScale(this.getPrinterScale())
            );
        matches.add(match);
        
        return matches;
    }

    /**
     * Prepare the HTML for printing. Scroll through each ticket stored in the List
     * List<MatchInfo>
     *
     * @param List<MatchInfo> matches
     * @param Map<String, PrintService> printServices
     * @return Boolean
     * @throws PrinterException
     */
    private Boolean prepareToPrintHTML(List<MatchInfo> matches) {
        List<String> errores = new ArrayList<String>();
        Boolean printed = true;
        try {

            for (int i = 0; i < matches.size(); i++) {
                MatchInfo match = matches.get(i);

                boolean last = i == matches.size() - 1;
                int nextPos = last ? HTML_CONTENT.length() : matches.get(i + 1).fStart;
                String sub = HTML_CONTENT.substring(match.fStart, nextPos);

                printed = printContent(sub, match, PRINTING_SERVICES);
                if (!printed) {
                    if (!errores.contains("\"" + match.printerName + "\" " + "Printer not found")) {
                        errores.add("\"" + match.printerName + "\" " + "Printer not found");
                    }
                    continue;
                }
            }

            if (!errores.isEmpty()) {
                printed = false;
                throw new Exception("Failed to load exceptions list");
            }
        } catch (Exception e) {
            String nl = System.getProperty("line.separator");
            String messageError = "";
            for (String item : errores) {
                messageError += item + nl;
            }
            messageError = messageError.isEmpty() ? e.getMessage().toString() : messageError;
            this.ShowMessage("Could not print a document", messageError, "error");
        }
        return printed;
    }

    /**
     * Print a ticket on the printer that corresponds.
     *
     * @param sub string with ticket information to print.
     * @param match information with the print settings for the ticket.
     * @param printServices Map with print services.
     * @return Boolean
     * @throws PrinterException
     */
    private Boolean printContent(String sub, MatchInfo match, Map<String, PrintService> printServices) {

        try {
            PrintService service = getPrinter(printServices, match.printerName);

            if (service == null) {
                return false;
            }

            HTMLPrinter target = new HTMLPrinter(sub, match.fMargins, match.fScale.getX(), match.fScale.getY());
            PrinterJob printJob = PrinterJob.getPrinterJob();

            printJob.setPrintService(service);
            printJob.setPrintable(target);
            printJob.print();

        } catch (PrinterException e) {
            this.ShowMessage("Application error", "Aborted print service", "error");
        }

        return true;
    }

    /**
     * Executes the necessary tasks to be able to print the document
     */
    private void doPrint() {
        try {
            List<MatchInfo> matches = setPrintSettings();
            prepareToPrintHTML(matches);
        } catch (Exception e) {
            this.ShowMessage("Application error", e.getMessage(), "error");
        }
    }

    /**
     * Parser of the information of the margins to Rectangle2D format
     *
     * @param aMargins
     * @return Object Rectangle2D
     */
    private static Rectangle2D parseMargins(String aMargins) {
        if ("default".equals(aMargins)) {
            return null;
        } else {
            String[] parts = aMargins.split(";");
            return new Rectangle2D.Double(
                    Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]));
        }
    }

    /**
     * Parsea la información de la escala a formato Point2D
     *
     * @param aScale
     * @return Object Point2D
     */
    private static Point2D parseScale(String aScale) {
        String[] parts = aScale.split(";");
        return new Point2D.Double(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]));
    }

    /**
     * Contains the printing information
     */
    private static class MatchInfo {

        public final int fStart;
        public final String printerName;
        public final Rectangle2D fMargins;
        public final Point2D fScale;

        public MatchInfo(int aStart, String aprinterName, Rectangle2D aMargins, Point2D aScale) {
            fStart = aStart;
            printerName = aprinterName;
            fMargins = aMargins;
            fScale = aScale;
        }
    }

    /**
     * Search for a printer by name on the map of detected printers. If he
     * name is type "\\ Machine \ Printer" and can not find the printer, it does
     * another search with only the name of the printer.
     */
    private PrintService getPrinter(Map<String, PrintService> aServices, String aName) {
        PrintService service = aServices.get(aName);
        int index = aName.lastIndexOf('\\');
        String lastName = aName.substring(index + 1);
        try {

            if (service != null) {
                return service;
            }

            if (index == -1) {
                return null;
            }

            service = aServices.get(lastName);
        } catch (Exception e) {
             System.out.println("Eroor");
        }

        return service;
    }

    /**
     * This class represents a printable document. 
     * Use a JEditorPane to render HTML rendering
     */
    private class HTMLPrinter implements Printable {

        private JEditorPane printPane;
        private Rectangle2D fMargins;
        private double fScaleX;
        private double fScaleY;

        public HTMLPrinter(String aContent, Rectangle2D aMargins, double aScaleX, double aScaleY) {
            printPane = new JEditorPane();
            printPane.setContentType("text/html");
            printPane.setText(aContent);
            fMargins = aMargins;
            fScaleX = aScaleX;
            fScaleY = aScaleY;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex >= 1) {
                return Printable.NO_SUCH_PAGE;
            }

            double imgX;
            double imgY;
            double imgW;
            double imgH;

            if (fMargins == null) {
                imgX = pageFormat.getImageableX();
                imgY = pageFormat.getImageableY();
                imgW = pageFormat.getImageableWidth();
                imgH = pageFormat.getImageableHeight();
            } else {
                imgX = fMargins.getX();
                imgY = fMargins.getY();
                imgW = fMargins.getWidth();
                imgH = fMargins.getHeight();
            }
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(imgX, imgY);
            g2d.setClip(new Rectangle2D.Double(0, 0, imgW, imgH));
            g2d.scale(fScaleX, fScaleY);

            printPane.setSize((int) imgW, 1);
            printPane.print(g2d);
            return Printable.PAGE_EXISTS;
        }
    }
}
