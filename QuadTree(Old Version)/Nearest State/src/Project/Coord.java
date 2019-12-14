package Project;

public class Coord {
	public double x, y;
	
	public Coord (double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double flatDistanceFrom(Coord other) {
		return Math.sqrt((x - other.x)*(x - other.x) + (y - other.y)*(y - other.y));
	}
	
	public double distanceFrom(Coord other) {
		double x = Math.toRadians((other.x - this.x)) * Math.cos(Math.toRadians((this.y + other.y)/2));
		double y = Math.toRadians(other.y - this.y);
		return Math.sqrt(x*x + y*y) * 6371;
	}
	
	public String toString() {
		return "x: " + x + " y: " + y;
	}
}
