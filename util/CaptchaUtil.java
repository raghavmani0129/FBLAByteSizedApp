package util;

import java.util.Random;


public class CaptchaUtil {


    //make random math eqn for user to do for captcha
    private static final Random random = new Random();
    private static int correctAnswer;


    public static String generateCaptchaQuestion() {
        int a = random.nextInt(10) + 1; 
        int b = random.nextInt(10) + 1; 

        correctAnswer = a + b; 
        return a + " + " + b; 
    }

    //take in user input and check against stored answer
    public static boolean validateCaptcha(String userInput) {
        try {
            int answer = Integer.parseInt(userInput.trim());
            return answer == correctAnswer;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}