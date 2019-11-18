import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class VMTranslator {
    private static int directoryCount;
    private static int fileCount;
    private static int vmCounter;
    private static int invalidFileCounter;
    private static int jmpNumber;
    private static int callCounter=0;
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

    /* Cleans up the file by getting rid of blank lines and any comments */
    public static ArrayList<String> cleanFile(String fileName) {
        ArrayList<String> vmInstructions = new ArrayList<String>();
        // Get rid of all comments and blank lines
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    if (line.startsWith("//") == false) {
                        if (line.contains("//")) {
                            int offset = line.indexOf("//");
                            line = line.substring(0, offset);
                            vmInstructions.add(line);
                        } else {
                            vmInstructions.add(line);
                        }
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vmInstructions;
    }

    /* Translates vm cmds to asm */
    public static ArrayList<String> readFile(String fileName) {
        // Array list containing vm instructions
        ArrayList<String> vmInstructions = cleanFile(fileName);
        // Array list containing asm instructions
        ArrayList<String> asmInstructions = new ArrayList<String>();
        File vmFileName = new File(fileName);
        for (String vmInstruct : vmInstructions) {
            String[] pieces = vmInstruct.split(" ");
            String instructThree = null, instructTwo = null;
            if (pieces.length > 1) {
                instructTwo = pieces[1];
            }
            if (pieces.length > 2) {
                instructThree = pieces[2];
            }


            if (vmInstruct.contains("add")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("M=M+D");
            } else if (vmInstruct.contains("sub")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("M=M-D");
            } else if (vmInstruct.contains("neg")) {
                asmInstructions.add("D=0");
                asmInstructions.add("@SP");
                asmInstructions.add("A=M-1");
                asmInstructions.add("A=D-M");
            } else if (vmInstruct.contains("eq")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("D=M-D");
                asmInstructions.add("@FALSE" + jmpNumber);
                asmInstructions.add("D;");
                asmInstructions.add("JNE");
                jmpNumber++;
            } else if (vmInstruct.contains("gt")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("D=M-D");
                asmInstructions.add("@FALSE" + jmpNumber);
                asmInstructions.add("D;");
                asmInstructions.add("JLE");
                jmpNumber++;
            } else if (vmInstruct.contains("lt")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("D=M-D");
                asmInstructions.add("@FALSE" + jmpNumber);
                asmInstructions.add("D;");
                asmInstructions.add("JGE");
                jmpNumber++;
            } else if (vmInstruct.contains("and")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("M=M&D");
            } else if (vmInstruct.contains("or")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("M=M|D");
            } else if (vmInstruct.contains("not")) {
                asmInstructions.add("@SP");
                asmInstructions.add("A=M-1");
                asmInstructions.add("M=!M");
            } else if (vmInstruct.contains("push")) {
                if (vmInstruct.contains("static")) {
                    asmInstructions.add("@" + vmFileName.getName() + instructThree);
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("this")) {
                    asmInstructions.add("@THIS");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("A=D+A");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("local")) {
                    asmInstructions.add("@LCL");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("A=D+A");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("argument")) {
                    asmInstructions.add("@ARG");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("A=D+A");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("that")) {
                    asmInstructions.add("@THAT");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("A=D+A");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("constant")) {
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D=A");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("pointer") && Integer.parseInt(instructThree) == 0) {
                    asmInstructions.add("@THIS");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("pointer") && Integer.parseInt(instructThree) == 1) {
                    asmInstructions.add("@THAT");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("temp")) {
                    asmInstructions.add("@R5");
                    asmInstructions.add("D=M");
                    int tempInt = Integer.parseInt(instructThree);
                    tempInt = tempInt + 5;
                    asmInstructions.add("@" + tempInt);
                    asmInstructions.add("A=D+A");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                }
            } else if (vmInstruct.contains("pop")) {
                if (vmInstruct.contains("static")) {
                    asmInstructions.add("@" + vmFileName.getName() + instructThree);
                    asmInstructions.add("D=A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@R13");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                }
                else if (vmInstruct.contains("this")) {
                    asmInstructions.add("@THIS");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D+D+A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add(("@SP"));
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add(("@R13"));
                    asmInstructions.add(("A=M"));
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("local")) {
                    asmInstructions.add("@LCL");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D+D+A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add(("@SP"));
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add(("@R13"));
                    asmInstructions.add(("A=M"));
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("argument")) {
                    asmInstructions.add("@ARG");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D+D+A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add(("@SP"));
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add(("@R13"));
                    asmInstructions.add(("A=M"));
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("that")) {
                    asmInstructions.add("@THAT");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D+D+A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add(("@SP"));
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add(("@R13"));
                    asmInstructions.add(("A=M"));
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("constant")) {
                    asmInstructions.add("@" + instructThree);
                    asmInstructions.add("D=A");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                } else if (vmInstruct.contains("pointer") && Integer.parseInt(instructThree) == 0) {
                    asmInstructions.add("@THIS");
                    asmInstructions.add("D=A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@R13");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("pointer") && Integer.parseInt(instructThree) == 1) {
                    asmInstructions.add("@THAT");
                    asmInstructions.add("D=A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add("@R13");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                } else if (vmInstruct.contains("temp")) {
                    asmInstructions.add("@THIS");
                    asmInstructions.add("D=M");
                    int tempInt = Integer.parseInt(instructThree);
                    tempInt = tempInt + 5;
                    asmInstructions.add("@" + tempInt);
                    asmInstructions.add("D+D+A");
                    asmInstructions.add("@R13");
                    asmInstructions.add("M=D");
                    asmInstructions.add(("@SP"));
                    asmInstructions.add("AM=M-1");
                    asmInstructions.add("D=M");
                    asmInstructions.add(("@R13"));
                    asmInstructions.add(("A=M"));
                    asmInstructions.add("M=D");
                }
            } else if (vmInstruct.contains("label")) {
                asmInstructions.add("(" + instructTwo + ")");
            } else if (vmInstruct.contains("goto")) {
                asmInstructions.add("@" + instructTwo);
                asmInstructions.add("0;JMP");
            } else if (vmInstruct.contains("if-goto")) {
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("A=A-1");
                asmInstructions.add("@" + instructTwo);
                asmInstructions.add("D;JNE");
            } else if (vmInstruct.contains("function")) {
                asmInstructions.add("(" + instructTwo + ")");
                int tempInt = Integer.parseInt(instructThree);
                for (int w = 0; w < tempInt; w++) {
                    asmInstructions.add("@" + 0);
                    asmInstructions.add("D=A");
                    asmInstructions.add("@SP");
                    asmInstructions.add("A=M");
                    asmInstructions.add("M=D");
                    asmInstructions.add("@SP");
                    asmInstructions.add("M=M+1");
                }
            } else if (vmInstruct.contains("return")) {
                asmInstructions.add("@LCL");
                asmInstructions.add("D=M");
                asmInstructions.add("@CONTAINER");
                asmInstructions.add("M=D");
                asmInstructions.add("@5");
                asmInstructions.add("A=D-A");
                asmInstructions.add("D=M");
                asmInstructions.add("@RETURN");
                asmInstructions.add("M=D");
                asmInstructions.add("@ARG");
                asmInstructions.add("D=M");
                asmInstructions.add("@" + 0);
                asmInstructions.add("D+D+A");
                asmInstructions.add("@R13");
                asmInstructions.add("M=D");
                asmInstructions.add(("@SP"));
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add(("@R13"));
                asmInstructions.add(("A=M"));
                asmInstructions.add("M=D");
                asmInstructions.add("@ARG");
                asmInstructions.add("D=M");
                asmInstructions.add("@SP");
                asmInstructions.add("M=D+1");
                asmInstructions.add("@CONTAINER");
                asmInstructions.add("D=M-1");
                asmInstructions.add("AM=D");
                asmInstructions.add("D=M");
                asmInstructions.add("@THAT");
                asmInstructions.add("M=D");
                asmInstructions.add("@CONTAINER");
                asmInstructions.add("D=M-1");
                asmInstructions.add("AM=D");
                asmInstructions.add("D=M");
                asmInstructions.add("@THIS");
                asmInstructions.add("M=D");
                asmInstructions.add("@CONTAINER");
                asmInstructions.add("D=M-1");
                asmInstructions.add("AM=D");
                asmInstructions.add("D=M");
                asmInstructions.add("@ARG");
                asmInstructions.add("M=D");
                asmInstructions.add("@CONTAINER");
                asmInstructions.add("D=M-1");
                asmInstructions.add("AM=D");
                asmInstructions.add("D=M");
                asmInstructions.add("@LCL");
                asmInstructions.add("M=D");
                asmInstructions.add("@RETURN");
                asmInstructions.add("A=D");
                asmInstructions.add("@LCL");
                asmInstructions.add("M=D");
                asmInstructions.add("@RETURN");
                asmInstructions.add("A=M");
                asmInstructions.add("0;JMP");
            } else if (vmInstruct.contains("call")) {
                String strLabel = "RETURN_LABEL" + callCounter;
                callCounter++;
                asmInstructions.add("@"+strLabel);
                asmInstructions.add("D=A");
                asmInstructions.add("@SP");
                asmInstructions.add("A=M");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("M=M+1");
                asmInstructions.add("@LCL");
                asmInstructions.add("D=A");
                asmInstructions.add("@R13");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("@R13");
                asmInstructions.add("A=M");
                asmInstructions.add("M=D");
                asmInstructions.add("@ARG");
                asmInstructions.add("D=A");
                asmInstructions.add("@R13");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("@R13");
                asmInstructions.add("A=M");
                asmInstructions.add("M=D");
                asmInstructions.add("@THIS");
                asmInstructions.add("D=A");
                asmInstructions.add("@R13");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("@R13");
                asmInstructions.add("A=M");
                asmInstructions.add("M=D");
                asmInstructions.add("@THAT");
                asmInstructions.add("D=A");
                asmInstructions.add("@R13");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("AM=M-1");
                asmInstructions.add("D=M");
                asmInstructions.add("@R13");
                asmInstructions.add("A=M");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("D=M");
                asmInstructions.add("@5");
                asmInstructions.add("D=D-A");
                asmInstructions.add("@" + instructThree);
                asmInstructions.add("D=D-A");
                asmInstructions.add("@ARG");
                asmInstructions.add("M=D");
                asmInstructions.add("@SP");
                asmInstructions.add("D=M");
                asmInstructions.add("@LCL");
                asmInstructions.add("M=D");
                asmInstructions.add("@" + instructTwo);
                asmInstructions.add("0;JMP");
                asmInstructions.add("(" + strLabel + ")");
            }
        }
        return asmInstructions;
    }

    /* Creates an asm file for the vm file or directory given */
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
                    writer.write(System.getProperty("line.separator"));
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