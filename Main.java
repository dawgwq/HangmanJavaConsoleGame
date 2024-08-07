import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.




public class Main {
    private static final Map<Integer, String> TEMPLATES_HANGMAN = Map.of(
    6, " +---+ \n |   | \n     | \n     | \n     | \n     | \n=========",
    5, " +---+ \n |   | \n O   | \n     | \n     | \n     | \n=========",
    4, " +---+ \n |   | \n O   | \n |   | \n     | \n     | \n=========",
    3, " +---+ \n |   | \n O   | \n/|   | \n     | \n     | \n=========",
    2, " +---+ \n |   | \n O   | \n/|\\  | \n     | \n     | \n=========",
    1, " +---+ \n |   | \n O   | \n/|\\  | \n/    | \n     | \n=========",
    0, " +---+ \n |   | \n O   | \n/|\\  | \n/ \\  | \n     | \n========="
);
    private static final String MENU_TEXT = """
            Используйте русскую раскладку
            
            1.Начать раунд [у]
            
            2.Выйти [н]
            
            """;

    private static final String ROUND_START_TEXT = """
            ИГРОВОЙ РАУНД НАЧАЛСЯ
            
            У ВАС 6 ЖИЗНЕЙ
            """;


    private static final String FILE_PATH = "words.txt";
    public static void main(String[] args) {
        startMenu();
    }
    public static void startMenu(){
        while (true){
            System.out.println(MENU_TEXT);
            switch (userInput("cp866")){
                case 'у' -> startGameRound();
                case 'н' -> System.exit(0);
            }
        }
    }
    public static void clearConsole(){
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else{
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        } catch (IOException | InterruptedException ex) {
            System.err.println("Очистка консоли не удалась: " + ex.getMessage());
        }
    }
    public static char userInput(String charsetName){
        Scanner scanner = new Scanner(System.in, charsetName);
        String input = scanner.next().toLowerCase();
        return input.charAt(0);
    }

    public static void startGameRound(){
        clearConsole();
        System.out.println(ROUND_START_TEXT);

        List<Character> hiddenWord = selectWord();

        List<Integer> guessedIndices = new ArrayList<>();

        Set<Character> checkedCharacters = new HashSet<>();

        int lives = 6;

        if (hiddenWord.isEmpty()) {
            return;
        }


        while (!TestForLose(lives) && !TestForVictory(hiddenWord,guessedIndices)){
            clearConsole();

            System.out.println(TEMPLATES_HANGMAN.get(lives));
            System.out.println("ОСТАВШЕЕСЯ КОЛИЧЕСТВО ЖИЗНЕЙ " + lives);
            System.out.println(getMaskedString(hiddenWord,guessedIndices));
            
            Character entered = userInput("cp866");

            Set<Integer> indices = attempt(entered,hiddenWord);

            if(!checkedCharacters.contains(entered)){

                if(!indices.isEmpty()){
                    guessedIndices.addAll(indices);
                }
                else {
                    lives--;
                }
            
            }
            checkedCharacters.add(entered);

        }
    }

    public static List<Character> selectWord() {
        List<String> allWords = new ArrayList<>();
        try {
            allWords = Files.readAllLines(Paths.get(FILE_PATH));

        } catch (IOException ex) {
            clearConsole();
            System.err.println("Ошибка в при выборе слова из файла " + ex.getMessage());
            return new ArrayList<>();
        }
        
        
        if (allWords.isEmpty()) {
            System.out.println("Файл words.txt пуст ");
            return new ArrayList<>();
        }

        Random random = new Random();
        int randomIndex = random.nextInt(allWords.size());

        String randomWord = allWords.get(randomIndex).toLowerCase();
        List<Character> hiddenWord = new ArrayList<>();
        for (int i = 0; i < randomWord.length(); i++) {
            hiddenWord.add(randomWord.charAt(i));
        }
        return hiddenWord;
    }


    public static Set<Integer> attempt(Character userInput, List<Character> hiddenWord){
        Set<Integer> indices = new HashSet<>();

        for (int i = 0; i < hiddenWord.size(); i++) {
            if (hiddenWord.get(i).equals(userInput)) {
                indices.add(i);
            }
        }
        return indices;
    }

    public static String getMaskedString(List<Character> hiddenWord, List<Integer> guessedIndices) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hiddenWord.size(); i++) {
            if (guessedIndices.contains(i)) {
                result.append(hiddenWord.get(i));
            } else {
                result.append('#');
            }
        }

        return result.toString();
    }

    public static boolean TestForVictory(List<Character> hiddenWord, List<Integer> guessedIndices){
        if (guessedIndices.size() == hiddenWord.size()){
            System.out.println("Вы победили");
            return true;
        }
        else {
            return false;
        }
    }
    public static boolean TestForLose(Integer lives){
        if (lives > 0){
            return false;
        }
        else {
            System.out.println("Вы проиграли");
            return true;
        }
    }
}