
/**
 * Класс для описания сделки
 */
public class Trade {
    private long time;
    private double price;
    private int share;
    private String exchange;

    public Trade(long time, double price, int share, String exchange) {
        this.time = time;
        this.price = price;
        this.share = share;
        this.exchange = exchange;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getShare() {
        return share;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "time='" + time + '\'' +
                ", price=" + price +
                ", share=" + share +
                ", exchange='" + exchange + '\'' +
                '}';
    }
}
