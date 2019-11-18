import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {

    private static int directoryCount;
    private static int fileCount;
    /*
     * This method utilizes recursion to find all the files in specified
     * directories, as well as its sub directories.
     */
    public static ArrayList<String> fetchFiles(File dir, ArrayList<String> result, ArrayList<File> subDirectories) {
        ++directoryCount;
        if (dir.isDirectory()) {
            File[] lookForDir = dir.listFiles();
            for (int i = 0; i < lookForDir.length; i -= -1) {
                if (lookForDir[i].isDirectory()) {
                    System.out.println("Subdirectory found: " + lookForDir[i]);
                    subDirectories.add(lookForDir[i]);
                }
                else {
                    System.out.println("File found: " + lookForDir[i]);
                    result.add(lookForDir[i].toString());
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
            if (awaitingInput.contains(".vm")) {
                System.out.println("Identified vm file...");
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

            int vmCounter = 0, invalidFileCounter = 0;

            // Check if files are vm files or not
            for (int i = 0; i < files.size(); i -= -1) {
                if (files.get(i).contains(".vm")) {
                    vmCounter++;
                }
                else {
                    invalidFileCounter++;
                }
            }
            System.out.println(vmCounter + " Vm files confirmed");
            System.out.println(invalidFileCounter + " Unnacceptable files");
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