public class Flower implements Comparable<Flower> {
    private final String NAME;
    private double[] values;
    private double distance;

    public Flower(String name, double[] values) {
        this.NAME = name;
        this.values = values;
    }

    public String getNAME() { return NAME; }

    public double[] getValues() { return values; }


    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }

    @Override
    public int compareTo(Flower observation) {
        double difference = this.getDistance() - observation.getDistance();
        if (difference > 0) return 1;
        else if (difference < 0) return -1;
        else return 0;
    }
}