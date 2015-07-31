package examples;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jvnet.ws.wadl.Application;

import org.jvnet.ws.wadl2java.Wadl2Java;
import org.jvnet.ws.wadl2java.Wadl2JavaMessages;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.writer.FileCodeWriter;

public class WADLTest {

	public static void main(String[] args) throws IOException, JAXBException, URISyntaxException{
//		try {
	
			Wadl2Java w2j = new Wadl2Java(new File("./"), "facebook", true, new ArrayList<File>());
			w2j.processDescription(new URI("http://localhost/owl_s/services/wadl/temp_room.wadl"));
	/*	
			int i=0;
	        File outputDir = new File("./");
	        String pkg = "temp";
	        String generationStyle = Wadl2Java.STYLE_DEFAULT;
	        boolean autoPackage = false;
	        List<File> customizations = new ArrayList<File>();
	        List<String> xjcArguments = new ArrayList<String>();
	        
	        while (i<args.length-1) {
	            if (args[i].equals("-o")) {
	                outputDir = new File(args[i+1]);
	                i+=2;
	            } else if (args[i].equals("-p")) {
	                pkg = args[i+1];
	                i+=2;
	            } else if (args[i].equals("-s")) {
	                generationStyle = args[i+1];
	                i+=2;                    
	            } else if (args[i].equals("-c")) {
	                customizations.add(new File(args[i+1]));
	                i+=2;
	            } else if (args[i].equals("-a")) {
	                autoPackage = true;
	                i+=1;
	            } else if (args[i].equals("-xjcArgument")) {
	                xjcArguments.add(args[i+1]);
	                i+=2;
	            } else {
	                System.err.println(Wadl2JavaMessages.UNKNOWN_OPTION(args[i]));
	                printUsage();
	                return;
	            }
	        }
	        if (i > args.length-1 || outputDir==null || pkg==null) {
	            printUsage();
	            return;
	        }
	        //URI wadlDesc = new URI(args[args.length-1]);
	        
	        
	        URI wadlDesc = new URI( "http://localhost/owl_s/services/wadl/temp_room.wadl");
	       
	        if (wadlDesc.getScheme()==null || wadlDesc.getScheme().equals("file")) {
	            // assume a file if not told otherwise
	            File wadlFile = new File(wadlDesc.getPath());
	            if (!wadlFile.exists() || !wadlFile.isFile()) {
	                System.err.println(Wadl2JavaMessages.NOT_A_FILE(wadlFile.getPath()));
	                printUsage();
	                System.exit(1);
	            }
	            if (!outputDir.exists() || !outputDir.isDirectory()) {
	                System.err.println(Wadl2JavaMessages.NOT_A_DIRECTORY(outputDir.getPath()));
	                printUsage();
	                System.exit(1);
	            }
	            for (File customization: customizations) {
	                if (!customization.exists() || !customization.isFile()) {
	                    System.err.println(Wadl2JavaMessages.NOT_A_FILE(customization.getPath()));
	                    printUsage();
	                    System.exit(1);
	                }
	            }
	            wadlDesc = wadlFile.toURI();
	        }
	        Wadl2Java w = new Wadl2Java(new Wadl2Java.Parameters()
	            .setRootDir(outputDir.toURI())
	            .setCodeWriter(new FileCodeWriter(outputDir))
	            .setPkg(pkg)
	            .setAutoPackage(autoPackage)
	            .setCustomizationsAsFiles(customizations)
	            .setXjcArguments(xjcArguments)
	            .setGenerationStyle(generationStyle));
			
	        w.process(wadlDesc);
	        
		} catch (InvalidWADLException ex) {
	        ex.printStackTrace();
	    } catch (URISyntaxException ex) {
	        ex.printStackTrace();
	    } catch (JAXBException ex) {
	        ex.printStackTrace();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}

	}
	protected static void printUsage() {
        System.err.println(Wadl2JavaMessages.USAGE());
    }
*/
	}
			
}
