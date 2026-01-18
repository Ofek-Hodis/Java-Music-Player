import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) throws LineUnavailableException, IOException {

        Scanner scanner = new Scanner(System.in);
        String filePath = getSongPath(scanner);
        File file = new File(filePath);

        // Resources opened in try's parentheses will be automatically closed
        AudioInputStream audioStream = null; // Creating outside try block, in order to close it in finally block
        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            float decibels = -20; // Variable to keep track of initial (and all other) decibel change
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decibels); // Reduce volume initially to avoid overly loud sound

            String response = "";

            while (!response.equals("Q")) {
                System.out.println("**********");
                System.out.println("P = Play");
                System.out.println("S = Stop");
                System.out.println("R = Restart");
                System.out.println("C = Change song");
                System.out.println("V = Volume control");
                System.out.println("F = Forward time (10s)");
                System.out.println("B = Backtrack time (10s)");
                System.out.println("Q = Quit");
                System.out.println("**********");
                response = scanner.next().toUpperCase();

                switch (response) {
                    case "P" -> {
                        remainingTime(clip);
                        clip.start();
                    }
                    case "S" -> clip.stop();
                    case "R" -> {
                        clip.setMicrosecondPosition(0);
                        clip.start();
                        remainingTime(clip);
                    }
                    case "C" -> {
                        clip.stop();
                        filePath = getSongPath(scanner); // Using the method to get a file path according to input
                        file = new File(filePath);
                        audioStream = AudioSystem.getAudioInputStream(file);
                        clip.close(); // Closing the clip before re-opening to allow rechoosing the same song
                        clip.open(audioStream);
                    }
                    case "V" -> {
                        decibels = setVolume(decibels, scanner, clip);
                    }
                    case "F" -> {
                        System.out.println("Fast forwarding 10 seconds");
                        timeControl(10, clip);
                        remainingTime(clip);
                    }
                    case "B" -> {
                        System.out.println("Back tracking 10 seconds");
                        timeControl(-10, clip);
                        remainingTime(clip);
                    }
                    case "Q" -> clip.close();
                    default -> System.out.println("Invalid choice");
                }
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported");
        }
        // Exception where the file is being accessed by another resource/it's unavailable
        catch (LineUnavailableException e) {
            System.out.println("Unable to access audio resource");
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate file");
        }
        // A safety net for input-output exceptions (in case specific exceptions did not catch the error)
        catch (IOException e) {
            System.out.println("Something went wrong!");
        } finally {
            System.out.println("Program terminated :)");
            scanner.close();
            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (IOException ignored) {}
            }
        }
    }

    // A method to be used every time the user wants to choose a different song
    static String getSongPath(Scanner scanner){
        int choice;
        String filePath;

        System.out.print("""
                **********
                What music would you like to listen to ?
                1 - Chinese themed background music
                2 - Cocktail jazz
                3 - Modern jazz
                4 - Latino pop
                5 - Classical piano
                6 - Classical piano 2
                7 - House music
                **********
                """);
        choice = scanner.nextInt();

        switch (choice) {
            case 1 -> filePath = "music\\chinese-lunar-new-year.wav";
            case 2 -> filePath = "music\\jazz-music.wav";
            case 3 -> filePath = "music\\modern-jazz.wav";
            case 4 -> filePath = "music\\latino-pop.wav";
            case 5 -> filePath = "music\\piano-classical-music-1.wav";
            case 6 -> filePath = "music\\piano-classical-music-2.wav";
            case 7 -> filePath = "music\\relax-chill-house-music.wav";
            default -> {
                filePath = "music\\chinese-lunar-new-year.wav"; // Defaulting to song number 1
                System.out.println("Invalid choice, 1 chosen by default");
            }
        }
        return filePath;
    }

    static float setVolume(float decibels ,Scanner scanner, Clip clip){
        System.out.println("Current change in decibels is " + decibels + ". Possible range is -80.0 6.0 DB.");
        System.out.print("Please enter the decibel change you would like to implement(float): ");
        try{
            float change = scanner.nextFloat();
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(decibels + change);
            decibels += change; // Saving the changes to decibels only after verifying the change was possible
        }
        catch(InputMismatchException e) {
            System.out.println("The value you entered is invalid, it must be a float");
        }
        // Catching decibels going out of their limit
        catch (IllegalArgumentException e) {
            System.out.println("Gain value out of range (-80.0 to 6.0 dB)");
        }
        // A safety net catch clause that gets the error name and prints it to user
        catch (Exception e){
            String errorName = e.getClass().getSimpleName();
            System.out.println("Unexpected error '" + errorName + "': " + e.getMessage());
        }
        finally{
            scanner.nextLine(); //Clearing up the input buffer to avoid future input mistakes
        }

        return decibels;
    }

    static void remainingTime(Clip clip){
        // Calculating remaining time and displaying it in seconds
        long remainingMicroseconds = clip.getMicrosecondLength() - clip.getMicrosecondPosition();
        double remainingSeconds = (double) remainingMicroseconds / 1000000;
        String remainingDuration = String.format("%.0f", remainingSeconds);
        System.out.println("Remaining run time: " + remainingDuration + " seconds");
    }

    static void timeControl(int changeSeconds, Clip clip){

        long changeMicroseconds = (long) changeSeconds * 1000000; //Converting seconds input to microseconds
        long currentTime = clip.getMicrosecondPosition();
        clip.setMicrosecondPosition(currentTime+changeMicroseconds);
    }
}
