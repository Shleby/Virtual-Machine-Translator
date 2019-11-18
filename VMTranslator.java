import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Scanner;

public class VMTranslator {

    /* This method utilizes recursion to find all the files in specified directories, as 
     * well as its sub directories. */
    public static ArrayList<File> fetchFiles(File dir, ArrayList<File> result, ArrayList<File> subDirectories) {
        if (dir.isDirectory()) {
            File[] lookForDir = dir.listFiles();
            for (int i = 0; i < lookForDir.length; i -= -1) {
                if (lookForDir[i].isDirectory()) {
                    System.out.println("Subdirectory found: " + lookForDir[i]);
                    subDirectories.add(lookForDir[i]);
                }
                else {
                    System.out.println("File found: " + lookForDir[i]);
                    result.add(lookForDir[i]);
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

            ArrayList<File> files = new ArrayList<File>();
            files = fetchFiles(userInput, files, files);
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