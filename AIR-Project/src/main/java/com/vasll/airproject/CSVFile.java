package com.vasll.airproject;

import java.io.*;
import java.util.ArrayList;

/**
 * Simple class for reading/writing to simple CSV files
 * @author <a href="https://github.com/vasll">Vasll</a>
 */
public class CSVFile {
    String filePath;                // Path of file we want to read
    String delimiter;               // Delimiter of the CSV file
    ArrayList<String> fileContent;  // ArrayList containing the file content

    /**
     * @param path The path of the CSV file
     * @param delimiter The delimiter of the CSV file. For example: "," "." ";"...
     */
    public CSVFile(String path, String delimiter){
        this.filePath = path;
        this.delimiter = delimiter;
        readFile();
    }

    /**
     * Reads the file and stores it into the "fileContent" ArrayList.
     * If the file is not found it will raise an exception.
     */
    private void readFile(){
        BufferedReader bufferedReader;
        this.fileContent = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                fileContent.add(line);
            }
        } catch (IOException e) {
            System.err.println(getClass()+": Couldn't read from file. Exception:");
            e. printStackTrace();
        }
    }

    /**
     * Updates the file content of the CSV file with the content of the "fileContent" ArrayList
     */
    private void updateFileContent(){
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            for (String line : fileContent) {
                fileWriter.write(line+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Formats the String[] elements into a CSV line
     */
    private String formatLineElements(String[] lineElements){
        StringBuilder line = new StringBuilder();
        for(String element : lineElements){
            if(element.equals(lineElements[lineElements.length-1])){
                line.append(element);
                break;
            }
            line.append(element).append(delimiter);
        }
        return line.toString();
    }

    /**
     * Gets a line from the CSV file
     * @param lineIndex The index of the line that we want to read from the file
     * @return A string containing the line from the CSV file
     */
    public String getLine(int lineIndex){
        return fileContent.get(lineIndex);
    }

    /**
     * Gets all the lines from the CSV file
     * @return A String[] containing all the lines from the CSV file
     */
    public String[] getLines(){
        String[] lines = new String[fileContent.size()]; //fileContent.size() is equal to the number of lines
        for(int i = 0; i<fileContent.size(); i++){
            lines[i] = getLine(i);
        }
        return lines;
    }

    /**
     * Gets the element from a line of the CSV file
     * @param lineIndex The index of the line that we want to read from the file
     * @param elementIndex The index of the element that we want to read from the line
     * @return A String containing the element from the line of the CSV file
     */
    public String getElement(int lineIndex, int elementIndex){
        String[] elements = getLine(lineIndex).split(delimiter);
        String element = null;
        try{
            element = elements[elementIndex];
        }catch(ArrayIndexOutOfBoundsException ex){
            System.err.println(getClass()+": elementIndex is too big. Exception:");
            ex.printStackTrace();
        }
        return element;
    }

    /**
     * Gets all the elements of index "elementIndex" from all the lines of the CSV file
     * @param elementIndex The index of the element that we want to read from all the lines
     * @return A String[] containing the elements from that index
     */
    public String[] getElements(int elementIndex){
        String[] elements = new String[fileContent.size()]; //fileContent.size() is equal to the number of lines
        for(int i = 0; i<fileContent.size(); i++){
            elements[i] = getElement(i, elementIndex);
        }
        return elements;
    }

    /**
     * Gets all the elements of index "elementIndex" from all the lines between startingLine and end of file
     * @param elementIndex The index of the element that we want to read from all the lines
     * @param startingLine The line where we want to start reading the elements from, included in the range
     * @return A String[] containing the elements from that index
     */
    public String[] getElements(int elementIndex, int startingLine){
        String[] elements = new String[fileContent.size()-startingLine]; //fileContent.size() is equal to the number of lines
        for(int i = 0; i<fileContent.size()-startingLine; i++){
            elements[i] = getElement(i+startingLine, elementIndex);
        }
        return elements;
    }

    public String[] getElementsMatricola(int elementIndex, int startingLine){
        String[] elements = new String[fileContent.size()-startingLine]; //fileContent.size() is equal to the number of lines
        for(int i = 0; i<fileContent.size()-startingLine; i++){
            elements[i] = getElement(i+startingLine, elementIndex);
        }
        return elements;
    }

    /**
     * Gets all the elements of index "elementIndex" from all the lines between startingLine and endingLine.
     * @param elementIndex The index of the element that we want to read from all the lines
     * @param startingLine The line where we want to start reading the elements from, included in the range
     * @param endingLine The line where we want to stop reading the elements from, not included in the range
     * @return A String[] containing the elements from that index
     */
    public String[] getElements(int elementIndex, int startingLine, int endingLine){
        String[] elements = new String[(endingLine-startingLine)]; //fileContent.size() is equal to the number of lines
        for(int i = 0; i<endingLine-startingLine; i++){
            elements[i] = getElement(i+startingLine, elementIndex);
        }
        return elements;
    }

    /**
     * Appends a line at the end of the CSV file
     * @param lineElements Array containing the elements that we want to append into a line at the end of the file
     */
    public void appendLine(String[] lineElements){
        fileContent.add(formatLineElements(lineElements));
        updateFileContent();
    }
    /**
     * Appends a line at the end of the CSV file
     * @param line Line that we want to append at the end of the file
     */
    public void appendLine(String line){
        fileContent.add(line);
        updateFileContent();
    }

    /**
     * Writes line into the CSV file
     * @param lineElements String array containing the elements that we want to write into the file
     * @param lineIndex Index of the line that we want to write to inside the file.
     *                  This index should be smaller than the lines that are present in the file.
     */
    public void writeLine(String[] lineElements, int lineIndex){
        fileContent.set(lineIndex, formatLineElements(lineElements));
        updateFileContent();
    }

    /**
     * Writes line into the CSV file
     * @param line String that we want to write into the file
     * @param lineIndex Index of the line that we want to write to inside the file.
     *                  This index should be smaller than the lines that are present in the file.
     */
    public void writeLine(String line, int lineIndex){
        fileContent.set(lineIndex, line);
        updateFileContent();
    }

    public int getLength(){
        return fileContent.size();
    }

    /**
     * @return A string containing the file content formatted as a single string
     */
    @Override
    public String toString(){
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i<fileContent.size(); i++)
            buffer.append(getLine(i)).append("\n");
        return buffer.toString();
    }
}
