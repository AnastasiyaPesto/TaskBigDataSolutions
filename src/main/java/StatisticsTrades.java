import sun.security.krb5.internal.crypto.Des;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StatisticsTrades {
    /**
     * Класс для запоминания макимального количества сделок на каждой бирже
     */
    class DescriptorAnswer {
        private String exchange;
        private String startSecond;
        private String endSecond;
        private int count;

        public DescriptorAnswer(String exchange, String startSecond, String endSecond, int count) {
            this.exchange = exchange;
            this.startSecond = startSecond;
            this.endSecond = endSecond;
            this.count = count;
        }

        public String getExchange() {
            return exchange;
        }

        public String getStartSecond() {
            return startSecond;
        }

        public String getEndSecond() {
            return endSecond;
        }

        public int getCount() {
            return count;
        }
    }

    /** Информация по всем биржам
     * @param key - биржа
     * @param Trade {@link Trade}*/
    private Map<String, List<Trade>> trades;

    /** Для вывода окна на экран в виде 00:00:00.000 и 00:00:00.999 */
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

    public StatisticsTrades() {
        this.trades = new HashMap<>();
    }

    /**
     * Метод для определения количества бирж
     * @return возвращает общее количесвто бирж
     */
    public int getCountExchange() {
        return trades.size();
    }

    /**
     * Метод для чтения из файла
     * @param path полный путь к файлу
     */
    private void readFromCSV(Path path) throws IOException, ParseException {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                // время от начала суток 00:00:00.000 в милисекундах
                long time = format.parse(data[0].trim()).getTime();
                double price = Double.parseDouble(data[1]);
                int share = Integer.parseInt(data[2]);
                String exchange = data[3].trim();

                Trade currentTrade = new Trade(time, price, share, exchange);
                putTradeInMap(currentTrade);
            }
        }
    }

    private void putTradeInMap(Trade trade) {
        String key = trade.getExchange();
        if (trades.containsKey(key)) {
            trades.get(key).add(trade);
        } else {
            List<Trade> tradeList = new ArrayList<>();
            tradeList.add(trade);
            trades.put(key, tradeList);
        }
    }

    /**
     * Главый метод для запуска обработки
     * @param path полный путь к файлу
     * @return возвращает статистику по каждой бирже, где есть окно длительностью РОВНО 1 секунда
     */
    public List<DescriptorAnswer> execute(Path path) throws IOException, ParseException {
        readFromCSV(path);
        Iterator<Map.Entry<String, List<Trade>>> it = trades.entrySet().iterator();
        List<DescriptorAnswer> answers = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry<String, List<Trade>> pair = it.next();
            List<Trade> values = (ArrayList<Trade>)pair.getValue();
            DescriptorAnswer answer = search(pair.getKey(), values);
            if (answer != null) {
                answers.add(answer);
            }
        }
        return answers;
    }

    private DescriptorAnswer search(String exchange, List<Trade> tradeList) {
        long maxStartSecond = 0;
        long maxEndSecond = 0;
        long currentTime = 0;
        int tradeCount = 0;
        int maxCount = 0;
        DescriptorAnswer answer = null;
        for (int i = 0; i < tradeList.size() - 1; i++) {
            long t = tradeList.get(i + 1).getTime() - tradeList.get(i).getTime();
            tradeCount += tradeList.get(i + 1).getShare();
            if (t == 999) {
                if (maxCount < tradeCount) {
                    maxStartSecond = tradeList.get(i).getTime();
                    maxEndSecond = tradeList.get(i + 1).getTime();
                    maxCount = tradeCount;
                    tradeCount = 0;
                }
            } else if (t < 999) {
                currentTime += t;
                if (currentTime == 999) {
                    if (maxCount < tradeCount) {
                        maxEndSecond = tradeList.get(i + 1).getTime();
                        maxStartSecond = maxEndSecond - currentTime;
                        maxCount = tradeCount;
                        tradeCount = 0;
                    }
                } else if (currentTime > 999) {
                    continue;
                }
            } else {
                tradeCount = 0;
            }
        }

        if (maxCount > 0){
            answer = new DescriptorAnswer(
                    exchange,
                    format.format(new Date(maxStartSecond)),
                    format.format(new Date(maxEndSecond)),
                    maxCount);
        }
        return answer;
    }
}
