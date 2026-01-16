import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main (String[] args){
        String filePath = "music\\chinese-lunar-new-year-465871.wav";
        File file = new File(filePath);

        // Resources opened in try's parentheses will be automatically closed
        try (Scanner scanner = new Scanner(System.in);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)){

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            String response = "";
            while(!response.equals("Q")){
                System.out.println("P = play");
                System.out.println("S = stop");
                System.out.println("R = Restart");
                System.out.println("Q = quit");

                response = scanner.next().toUpperCase();

                switch (response){
                    case "P" -> clip.start();
                    case "S" -> clip.stop();
                    case "R" -> clip.setMicrosecondPosition(0);
                    case "Q" -> clip.close();
                    default -> System.out.println("Invalid choice");
                }
            }
        }
        catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file is not supported");
        }
        // Exception where the file is being accessed by another resource/it's unavailable
        catch (LineUnavailableException e){
            System.out.println("Unable to access audio resource");
        }
        catch (FileNotFoundException e){
            System.out.println("Could not locate file");
        }
        // A safety net of sorts (in case specific exceptions did not catch the error)
        catch(IOException e){
            System.out.println("Something went wrong!");
        }
        finally{
            System.out.println("Program terminated :)");

        }

    }
}
