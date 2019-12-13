package Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;


import Project.QuadTree.Node;

public class Test {
	
	public static double[] readGraph(String filename, ArrayList<Node> nodes) throws FileNotFoundException {
		Scanner graphScanner = new Scanner (new File(filename), "UTF-8"); // Scanner object to convert data to string
		String line = graphScanner.nextLine(); // read the first line and skip
		String[] lineArray;
		double minX = 0, minY = 0, maxX = 0, maxY = 0;
		int i = 0;
		String state, county;
		double x, y; // x is lon, y is lat
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
		
		double[] graphBounds = {minX, maxY, maxX, minY};
		return graphBounds;
	}
	
	public static QuadTree buildQuadTree(ArrayList<Node> nodes, double[] graphBounds) {
		QuadTree root = new QuadTree(new Coord(graphBounds[0], graphBounds[1]), new Coord(graphBounds[2], graphBounds[3]));
		for (Node node : nodes) {
			root.insert(node);
		}
		return root;
	}
	
	public static KDTree buildKDTree(ArrayList<Node> nodes) {
		KDTree r = new KDTree(nodes.get(0));
		for (int j=1;j<nodes.size();j++) {
			r.insert(nodes.get(j));
		}
		return r;
	}
	
	public static RTree buildRTree(ArrayList<Node> nodes) {
		RTree rootR = new RTree();
		for (Node node : nodes) {
			System.out.println("Inserting " + node.point.x + ", " + node.point.y);
			if(rootR.insert(node) == true) {
				rootR = rootR.parent;
			};
			if(rootR.parent != null)
			{
				rootR = rootR.parent;
			}
//			{
//				rootR = rootR.parent;
//			}
			System.out.println("THE TREE AS IT CURRENTLY STANDS:");
			System.out.println(rootR.parent == null);
			rootR.print_tree(rootR);
			System.out.println("_____________________________________");
		}
		return rootR;
	}
	
	public static String[] getVotesResult(Node[] nearestNodes) {
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
	
	public static void main(String[] args) throws FileNotFoundException {
		String filename = "test.txt";
		Coord coord = new Coord(0, 0);
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		double[] graphBounds; 
		graphBounds = readGraph(filename, nodes);
		
		QuadTree rootQ = buildQuadTree(nodes, graphBounds);
		KDTree rootKD = buildKDTree(nodes);
		RTree rootR = buildRTree(nodes);
		
		// simple test
		Node[] nearestNodes = new Node[10];
		rootQ.search(coord, nearestNodes);
		
		// rootQ.printTree("");
		
		System.out.println("QuadTree Result: ");
		for (Node node : nearestNodes) {
			System.out.println(node);
		}
		
		System.out.println("-------------------------");
		
		nearestNodes = rootKD.nearest(rootKD.root, coord);
		
		System.out.println("KDTree Result: ");
		for (Node node : nearestNodes) {
			System.out.println(node);
		}
	}

}
