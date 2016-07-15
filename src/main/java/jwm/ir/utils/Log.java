package jwm.ir.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    static {

        File documentDir = new File("logs");
        if (!documentDir.exists()) {
            documentDir.mkdir();
        }

    }

	public synchronized void LogMessage(String name, String message, boolean err)
    {

        // add to the file with list of changes
        try
        {
        	String logFileName = System.getProperty("user.dir") + "/logs/";

        	Date now = new Date();
            Format formatterYYYMM = new SimpleDateFormat("yyyy-MM-dd");
            Format formatterTime = new SimpleDateFormat("hh:mm:ss.SSS aa");
            String fileNameDatePortion = formatterYYYMM.format(now);
            
            String logType = "log_";
            logFileName += name + "_" + logType + fileNameDatePortion + ".txt";
            File fLog = new File(logFileName);
            fLog.createNewFile();
            
            if (err) message = "*** ERROR *** " + message;
            
            FileWriter fWriter = new FileWriter(fLog, true);
            String formattedMessage = formatterTime.format(now) + " " + name + ": " + message + "\n";
            fWriter.write(formattedMessage);
            fWriter.close();

            // send to console too
            System.out.print(formattedMessage);

           
        }
        catch(Exception e)
        {
        	System.out.println(e.toString());
        }
    }
}
