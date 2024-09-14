import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Halstead {
    private final String inputPath;
    private final String outputPath;

    public static final Map<String, Integer> operators = new HashMap<>();
    public static final Map<String, Integer> operands = new HashMap<>();
    private final Set<String> ignore = new HashSet<>();

    public Halstead(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;

        String[] operatorList = {
                "puts", "[", "]", ".", " ", "\t", "\n", "\r", "+", "-", "*", "=", "/",
                ",", "(", ")", "for", "loop", "while", "do", "else", "elsif", "==",
                ">", "<", "%", "return", ">=", "<=", "end", "begin", "case", "when",
                "gets", "!", "break", "def", "if", ".."
        };
        for (String operator : operatorList) {
            operators.put(operator, 0);
        }

        String[] ignoreList = {"\r", "do", "end", "\n", "else", "elsif", "when", ")", "]", "\t", " ", "("};
        ignore.addAll(Arrays.asList(ignoreList));
    }

    public void calculate() throws IOException {
        String code = Files.readString(Paths.get(inputPath));

        Map<String, Boolean> functions = new HashMap<>();
        String prevToken = "";
        String currToken = "";
        boolean dot = false;

        for (char symbol : code.toCharArray()) {
            prevToken = currToken;
            currToken += symbol;

            if (operators.containsKey(currToken)) {
                if (!currToken.isEmpty() && !ignore.contains(currToken)) {
                    operators.put(currToken, operators.getOrDefault(currToken, 0) + 1);
                }
                currToken = "";
            } else if (operators.containsKey(prevToken)) {
                if (prevToken.equals(".")) {
                    dot = true;
                }
                if (functions.containsKey(prevToken)) {
                    operators.put("(", operators.getOrDefault("(", 0) - 1);
                }
                operators.put(prevToken, operators.getOrDefault(prevToken, 0) + 1);
                currToken = String.valueOf(symbol);
                if (!prevToken.equals(".")) {
                    dot = false;
                }
            } else if (operators.containsKey(String.valueOf(symbol))) {
                if (dot || symbol == '(') {
                    if (symbol == '(') {
                        operators.put("(", operators.getOrDefault("(", 0) - 1);
                        functions.put(prevToken, true);
                    }
                    operators.put(prevToken, operators.getOrDefault(prevToken, 0) + 1);
                } else {
                    if (!ignore.contains(prevToken) && !prevToken.equals("i") && !prevToken.equals("n")) {
                        if (!prevToken.equals("in")) {
                            operands.put(prevToken, operands.getOrDefault(prevToken, 0) + 1);
                        }
                    }
                }
                currToken = String.valueOf(symbol);
                dot = false;
            }
        }

        if (operators.containsKey(currToken)) {
            if (!currToken.isEmpty() && !ignore.contains(currToken)) {
                operators.put(currToken, operators.getOrDefault(currToken, 0) + 1);
            }
        } else if (dot) {
            operators.put(currToken, operators.getOrDefault(currToken, 0) + 1);
        } else {
            if (!ignore.contains(currToken) && !currToken.equals("i") && !currToken.equals("n")) {
                if (!currToken.equals("in")) {
                    operands.put(currToken, operands.getOrDefault(currToken, 0) + 1);
                }
            }
        }
        operators.remove("\t");
        operators.remove("\n");
        operators.remove("\r");
        operators.remove(" ");
        operators.remove(")");

        new WriterUtil().generateHtmlReport(outputPath);
    }
}
