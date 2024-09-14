import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class WriterUtil {
    private static int n1;
    private static int n2;
    private static int N1;
    private static int N2;
    void generateHtmlReport(String outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>")
                .append("body {background: white; color: black; font-family: Arial, sans-serif;}")
                .append("table {border-collapse: collapse; width: 80%; margin: 20px auto;}")
                .append("th, td {padding: 10px; text-align: center; border: 1px solid black;}")
                .append("th {background-color: #f2f2f2; color: black;}")
                .append("td {background-color: white; color: black;}")
                .append("</style></head><body>")
                .append("<meta charset=\"UTF-8\" />");


        appendOperatorsAndOperandsTable(sb);
        appendHalsteadMetrics(sb);

        sb.append("</body></html>");
        Files.writeString(Paths.get(outputPath), sb.toString());
    }

    private void appendOperatorsAndOperandsTable(StringBuilder sb) {
        sb.append("<h2 style='text-align: center;'>Operators and Operands</h2>");
        sb.append("<table><tr><th>i</th><th>Оператор</th><th>Количество</th><th>j</th><th>Операнд</th><th>Количество</th></tr>");

        int numOperators = 1;
        int numOperands = 1;

        Iterator<Map.Entry<String, Integer>> opIt = Halstead.operators.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .iterator();
        Iterator<Map.Entry<String, Integer>> operandIt = Halstead.operands.entrySet().iterator();

        while (opIt.hasNext() || operandIt.hasNext()) {
            sb.append("<tr>");

            if (opIt.hasNext()) {
                Map.Entry<String, Integer> operatorEntry = opIt.next();
                sb.append("<td>").append(numOperators++).append("</td>")
                        .append("<td>").append(operatorEntry.getKey()).append("</td>")
                        .append("<td>").append(operatorEntry.getValue()).append("</td>");
            } else {
                sb.append("<td></td><td></td><td></td>");
            }

            if (operandIt.hasNext()) {
                Map.Entry<String, Integer> operandEntry = operandIt.next();
                sb.append("<td>").append(numOperands++).append("</td>")
                        .append("<td>").append(operandEntry.getKey()).append("</td>")
                        .append("<td>").append(operandEntry.getValue()).append("</td>");
            } else {
                sb.append("<td></td><td></td><td></td>");
            }
            sb.append("</tr>");
        }

        n1 = (int) Halstead.operators.values().stream().filter(count -> count > 0).count();
        n2 = Halstead.operands.size();
        N1 = Halstead.operators.values().stream().mapToInt(Integer::intValue).sum();
        N2 = Halstead.operands.values().stream().mapToInt(Integer::intValue).sum();

        sb.append("<tr>")
                .append("<td>n1 = ").append(n1).append("</td>")
                .append("<td colspan='1'></td>")
                .append("<td>N1 = ").append(N1).append("</td>")
                .append("<td>n2 = ").append(n2).append("</td>")
                .append("<td colspan='1'></td>")
                .append("<td>N2 = ").append(N2).append("</td>")
                .append("</tr>");



        sb.append("</table>");
    }

    private void appendHalsteadMetrics(StringBuilder sb) {
        int programLength = N1 + N2;
        int dictionarySize = n1 + n2;
        double volume = programLength * log2(dictionarySize);

        sb.append("<h2 style='text-align: center;'>Метрика Холстеда</h2>");
        sb.append("<table><tr><th>Словарь программы</th><th>Длина программы</th><th>Объем программы</th></tr>");
        sb.append("<tr><td>").append(dictionarySize).append("</td><td>").append(programLength)
                .append("</td><td>").append(String.format("%.2f", volume)).append("</td></tr></table>");
    }

    private double log2(double n) {
        return Math.log(n) / Math.log(2);
    }
}
