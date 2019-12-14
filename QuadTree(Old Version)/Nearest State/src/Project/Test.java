package Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import Project.QuadTree.Node;

public class Test {
	
	public static QuadTree readGraph(String filename) throws FileNotFoundException {
		Scanner graphScanner = new Scanner (new File(filename)); // Scanner object to convert data to string
		String line = graphScanner.nextLine(); // read the first line and skip
		String[] lineArray;
		ArrayList<Node> nodes = new ArrayList<Node>();
		double minX = 0, minY = 0, maxX = 0, maxY = 0;
		int i = 0;
		String state, county;
		double x, y;
		// read Data line by line
		while (graphScanner.hasNextLine()) {
			line = graphScanner.nextLine();
			lineArray = line.trim().split("\\s+");
			state = lineArray[0];
			if (lineArray.length > 3) {
				county = lineArray[1];
			}
			else {
				county = "Empty";
			}
			for (int j = 2; j < lineArray.length - 2; j++) {
				county += " " + lineArray[j];
			}
			// county = Arrays.toString(Arrays.copyOfRange(lineArray, 1, lineArray.length - 2));
			System.out.println(county);
			x = Double.parseDouble(lineArray[lineArray.length-1]);
			y = Double.parseDouble(lineArray[lineArray.length-2]);
			Node node = new Node(x, y, state, county); // create correspond node object
			nodes.add(node);
			if (i == 0) {
				minX = x; maxX = x; minY = y; maxY = y;
			}
			else {
				if (x < minX) {
					minX = x;
				}
				else if (x > maxX) {
					maxX = x;
				}
				if (y < minY) {
					minY = y;
				}
				else if (y > maxY) {
					maxY = y;
				}
			}
			
			i++;
		}
		
		graphScanner.close(); 
		
		QuadTree root = new QuadTree(new Coord(minX, maxY), new Coord(maxX, minY));
		for (Node node : nodes) {
			root.insert(node);
		}
		return root;
	}
	
	public static String[] GetVotesResult(Node[] nearestNodes) {
		HashMap<String, Integer> stateVotes = new HashMap<String, Integer>();
		HashMap<String, Integer> countyVotes = new HashMap<String, Integer>();
		String closestState = null, closestCounty = null;
		for (int i = 0; i < 5; i++) {
			if (!stateVotes.containsKey(nearestNodes[i].state)) {
				stateVotes.put(nearestNodes[i].state, 1);
			}
			else {
				stateVotes.put(nearestNodes[i].state, stateVotes.get(nearestNodes[i].state) + 1);
			}
			if (closestState == null) {
				closestState = nearestNodes[i].state;
			}
			else if (stateVotes.get(closestState) < stateVotes.get(nearestNodes[i].state)) {
				closestState = nearestNodes[i].state;
			}
			
			
			if (!countyVotes.containsKey(nearestNodes[i].county)) {
				countyVotes.put(nearestNodes[i].county, 1);
			}
			else {
				countyVotes.put(nearestNodes[i].county, countyVotes.get(nearestNodes[i].county) + 1);
			}
			if (closestCounty == null) {
				closestCounty = nearestNodes[i].county;
			}
			else if (countyVotes.get(closestCounty) < countyVotes.get(nearestNodes[i].county)) {
				closestCounty = nearestNodes[i].county;
			}
			
		}
		String[] result = {closestState, closestCounty};
		return result;
	}
	
	public static boolean isDouble(String s) {
		try {
			double d = Double.parseDouble(s);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static double distanceBtw2Points(Coord a, Coord b) {
		double x = Math.toRadians((b.x - a.x)) * Math.cos(Math.toRadians((a.y + b.y)/2));
		double y = Math.toRadians(b.y - a.y);
		return Math.sqrt(x*x + y*y) * 6371;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String filename = "test.txt";
		Coord coord = new Coord(0, 0);
		QuadTree root = readGraph(filename);
		Node[] nearestNodes = new Node[10];
		root.search(coord, nearestNodes);
		
		root.printTree("");
		
		for (Node node : nearestNodes) {
			System.out.println(node);
		}

	}

}
