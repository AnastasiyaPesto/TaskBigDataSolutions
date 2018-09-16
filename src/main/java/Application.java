import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Application {
    public static void main(String[] args) throws ParseException, IOException {

        Path path = Paths.get("TRD.csv");

        StatisticsTrades statisticsTrades = new StatisticsTrades();
        List<StatisticsTrades.DescriptorAnswer> answers = statisticsTrades.execute(path);
        for (StatisticsTrades.DescriptorAnswer answer : answers) {
            System.out.println("Максимальное количество сделок в течении одной секунды было между " +
                    answer.getStartSecond() + " и " +
                    answer.getEndSecond() + " . В этот интервал прошло " + answer.getCount() + " сделок на бирже " +
                    answer.getExchange() + ".");
        }
    }
}
