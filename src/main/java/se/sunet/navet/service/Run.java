package se.sunet.navet.service;

import org.apache.commons.cli.*;
import se.sunet.navet.service.server.EmbeddedServer;

import java.io.*;

/**
 * Created by lundberg on 2015-09-01.
 */
public class Run extends EmbeddedServer {

    private static String configFile = "/opt/eduid/etc/navet-service.properties";

    public static void main(String[] args) {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();
        options.addOption("h", "help", false, "This usage information");
        options.addOption("c", "config", true,
                "Path to configuration file, default /opt/eduid/etc/navet-service.properties");

        try {
            CommandLine commandLine = parser.parse(options, args);

            if (commandLine.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }

            if (commandLine.hasOption("c")) {
                configFile = commandLine.getOptionValue("c");
                File config = new File(configFile);
                if (!config.exists()) {
                    System.err.println("ERROR: Config file not found!");
                    System.exit(1);
                }
            }

        } catch (ParseException e) {
            System.err.println("Bad command arguments.");
            printUsage(options);
            System.exit(1);
        }
        try {
            EmbeddedServer server = new Run();
            server.setup(configFile);

            /* DEBUG
            PersonPostService ps = new PersonPostService(
                    System.getProperty("se.sunet.navet.service.wsBaseEndpoint"),
                    System.getProperty("se.sunet.navet.service.organisationNumber"),
                    System.getProperty("se.sunet.navet.service.orderIdentity")
            );
            Gson gson = new Gson();
            ResponseXMLTYPE data = ps.getData("197609272393");
            List<FolkbokforingspostTYPE> fbp = data.getFolkbokforingsposter().getFolkbokforingspost();
            System.out.println("Something goes right! For once!!");
            FolkbokforingspostTYPE post = fbp.get(0);
            NavetNotification.Response resp = new NavetNotification.Response(data);
            System.out.println(gson.toJson(resp));
            */

            server.start();
            server.join();
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.err.println("Missing configuration in " + configFile + ".");
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("Missing file:");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.err.println("Failed to start server.");
            System.exit(1);
        }
    }

    public static void printUsage(Options options) {
        HelpFormatter help = new HelpFormatter();
        help.setWidth(80);
        help.printHelp("[-c <config>] [OPTION]...", "", options, "");
    }
}
