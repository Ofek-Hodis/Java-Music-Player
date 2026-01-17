import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        String filePath = getSongPath(scanner);
        File file = new File(filePath);

        // Resources opened in try's parentheses will be automatically closed
        AudioInputStream audioStream = null; // Creating outside try block, in order to close it in finally block
        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            String response = "";

            while (!response.equals("Q")) {
                System.out.println("P = play");
                System.out.println("S = stop");
                System.out.println("R = Restart");
                System.out.println("C = Change song");
                System.out.println("Q = quit");
                response = scanner.next().toUpperCase();

                switch (response) {
                    case "P" -> clip.start();
                    case "S" -> clip.stop();
                    case "R" -> clip.setMicrosecondPosition(0);
                    case "C" -> {
                        clip.stop();
                        filePath = getSongPath(scanner); // Using the method to get a file path according to input
                        file = new File(filePath);
                        audioStream = AudioSystem.getAudioInputStream(file);
                        clip.close(); // Closing the clip before re-opening to allow rechoosing the same song
                        clip.open(audioStream);
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
                What music would you like to listen to ?
                1 - Chinese themed background music
                2 - Cocktail jazz
                3 - Modern jazz
                4 - Latino pop
                5 - Classical piano
                6 - Classical piano 2
                7 - House music
                """);
        choice = scanner.nextInt();

        switch (choice) {
            case 1 -> filePath = "music\\chinese-lunar-new-year.wav";
            case 2 -> filePath = "music\\jazz-music.wav";
            case 3 -> filePath = "music\\latino-pop.wav";
            case 4 -> filePath = "music\\modern-jazz.wav";
            case 5 -> filePath = "music\\piano-classical-music-1.wav";
            case 6 -> filePath = "music\\piano-classical-music-2.wav";
            case 7 -> filePath = "music\\relax-chill-house-music.wav";
            default -> {
                filePath = "music\\chinese-lunar-new-year.wav";
                System.out.println("Invalid choice, 1 chosen by default");
            }
        }
        return filePath;
    }
}
