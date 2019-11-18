import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {

    private static int directoryCount;
    private static int fileCount;
    public static int vmCounter;
    public static int invalidFileCounter;
    /*
     * This method utilizes recursion to find all the files in specified
     * directories, as well as its sub directories.
     */
    public static ArrayList<String> fetchFiles(File dir, ArrayList<String> result, ArrayList<File> subDirectories) {
        ++directoryCount;
        if (dir.isDirectory()) {
            File[] lookForDir = dir.listFiles();
            for (File file : lookForDir) {
                if (file.isDirectory()) {
                    System.out.println("Subdirectory found: " + file.getName());
                    subDirectories.add(file);
                }
                else {
                    System.out.println("File found: " + file.getName());
                    result.add(file.toString());
                    ++fileCount;
                }
            }
        }
        if (!subDirectories.isEmpty()) {
            dir = subDirectories.get(0);
            subDirectories.remove(0);
            fetchFiles(dir, result, subDirectories);
        }
        return result;
    }
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> asmInstructions = new ArrayList<String>();


        return asmInstructions;
    }

    public static String createNewFile(String file) {
        if (file.contains(".vm")) {
            file = file.replace(".vm", ".asm");
            try {
                File asmFile = new File(file);
                if (asmFile.createNewFile())
                    System.out.println("Asm file was created successfully!");
                else
                    System.out.println("Error, file already exists. Instructions have been RE-WRITTEN in the file!");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        else {
            file = file.concat(".asm");
            try {
                File asmFile = new File(file);
                if (asmFile.createNewFile())
                    System.out.println("Asm file was created successfully!");
                else
                    System.out.println("Error, file already exists. Instructions have been RE-WRITTEN in the file!");
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return file;
    }
    public static void main(String[] args) throws IOException {
        // Prompt user to supply either a directory or file
        System.out.println("Please specify the path to a file or directory containing .vm files to be translated");
        
        // Snag that bad boi from the user
        Scanner input = new Scanner(System.in);
        String awaitingInput = input.nextLine();
        File userInput = new File(awaitingInput);
        // See if we have a file or a directory
        if (userInput.isFile()) {
            // If it is a file, we still need to check if its a .vm file
            // Then we proceed to write an asm file filled with translated
            // instructions
            if (awaitingInput.contains(".vm")) {
                System.out.println("Identified vm file...");

                // Create asm and a writer to put instructions into it
                String asmFile = createNewFile(awaitingInput);
                FileWriter writer = new FileWriter(asmFile);

                ArrayList<String> asmInstructions = readFile(awaitingInput);
                for (String instruct : asmInstructions) {
                    writer.write(instruct);
                }
                System.out.println("Operations Completed");
                File asm = new File(asmFile);
                System.out.println("Vm file: " + userInput.getName() + " ==> Asm File: " + asm.getName() + " | Created/Updated Successfully");
                writer.close();
            }
            // If not a .vm file throw an exception
            else {
                input.close();
                System.out.println("File was entered however the file was not of the type '.vm'");
                System.out.println("Throwing IO Exception");
                throw new IOException();
            }
        }
        // If its a directory we need to iterate throughout all the files in the directory
        else if (userInput.isDirectory()) {
            System.out.println("Identified directory...");
            System.out.println("Iterating through directory...");

            // Use recursion to gather all the files
            ArrayList<String> files = new ArrayList<String>();
            ArrayList<File> tempFiles = new ArrayList<File>();
            files = fetchFiles(userInput, files, tempFiles);
 
            System.out.println("Iterated through a total of " + directoryCount + " directories");
            System.out.println("Found a total of " + fileCount + " files");

            // Check if files are vm files or not and removes invalid files
            ArrayList<String> vmFiles = new ArrayList<String>();
            for (String iterFiles : files) {
                if (iterFiles.contains(".vm")) {
                    vmCounter++;
                    vmFiles.add(iterFiles);
                }
                else {
                    invalidFileCounter++;
                }
            }

            System.out.println(vmCounter + " .vm files confirmed");
            System.out.println(invalidFileCounter + " invalid files");

            // Create asm file and a writer to put instructions into it
            String asmFile = createNewFile(awaitingInput);
            FileWriter writer = new FileWriter(asmFile);
            for (String vmFile : vmFiles) {
                ArrayList<String> asmInstructionsHolder = readFile(vmFile);
                for (String instruct : asmInstructionsHolder) {
                    writer.write(instruct);
                    writer.write(System.getProperty("line.separator"));
                }
            }
            System.out.println("Operations Completed");
            File asm = new File(asmFile);
            System.out.println("Directory: " + userInput.getName() + " ==> Asm File: " + asm.getName() + " | Created/Updated Successfully");
            writer.close();
        }
        else {
            input.close();
            System.out.println("Please enter a valid input. Throwing IOException");
            throw new IOException();
        }
        // Make scanner happy, let it rest
        input.close();
    }
}