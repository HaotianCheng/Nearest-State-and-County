package Project;

public class QuadTree {
	
	public static class Node {
		public Coord point;
		public String state, county;
		
		public Node(double x, double y, String state, String county) {
			point = new Coord(x, y);
			this.state = state;
			this.county = county;
		}
		
		public String toString() {
			return state + " " + county + " " + point.y + " " + point.x;
		}
	}
	
	public Node node;
	public QuadTree parent;
	public QuadTree ne, nw, se, sw;
	public Coord topLeft, botRight;
	
	public QuadTree (Coord tl, Coord br) {
		topLeft = tl;
		botRight = br;
	}
		
	public void insert(Node node) {
		if (node == null) {
			return;
		}
		else if (this.node != null) {
			if (node.point.x == this.node.point.x && node.point.y == this.node.point.y) {
				System.out.println("Duplication: " + node);
				return;
			}
		}
		if (!isInBoundary(node)) {
			System.out.println(node);
			System.out.println(topLeft);
			System.out.println(botRight);
			System.out.println("Not in boundary");
			return;
		}
		
		if (this.node == null && ne == null && nw == null && se == null && sw == null) {
			this.node = node;
		}
		else {
			if (node.point.x < (botRight.x + topLeft.x)/2) {
				if (node.point.y > (topLeft.y + botRight.y)/2) {
					if (nw == null) {
						nw = new QuadTree(topLeft, new Coord((botRight.x + topLeft.x)/2, (topLeft.y + botRight.y)/2));
						nw.parent = this;
						nw.node = node;
						if (this.node != null) {
							Node temp = this.node;
							this.node = null;
							insert(temp);
						}
						
						
					}
					else {
						nw.insert(node);
					}
				}
				else {
					if (sw == null) {
						sw = new QuadTree(new Coord(topLeft.x, (topLeft.y + botRight.y)/2), new Coord((botRight.x + topLeft.x)/2, botRight.y));
						sw.parent = this;
						sw.node = node;
						if (this.node != null) {
							Node temp = this.node;
							this.node = null;
							insert(temp);
							// System.out.println(temp);
						}
					}
					else {
						// System.out.println(node);
						sw.insert(node);
					}
				}
			}
			else {
				if (node.point.y > (topLeft.y + botRight.y)/2) {
					if (ne == null) {
						ne = new QuadTree(new Coord((botRight.x + topLeft.x)/2, topLeft.y), new Coord(botRight.x, (topLeft.y + botRight.y)/2));
						ne.parent = this;
						ne.node = node;
						if (this.node != null) {
							Node temp = this.node;
							this.node = null;
							insert(temp);
						}
					}
					else {
						ne.insert(node);
					}
				}
				else {
					if (se == null) {
						se = new QuadTree(new Coord((botRight.x + topLeft.x)/2, (topLeft.y + botRight.y)/2), botRight);
						se.parent = this;
						se.node = node;
						
						if (this.node != null) {
							Node temp = this.node;
							this.node = null;
							insert(temp);
						}
					}
					else {
						se.insert(node);
					}
				}
			}
		}
	}
	
	public void search(Coord coord, Node[] nodes) {
		if (node == null) {
			QuadTree[] sequence = new QuadTree[4];
			if (coord.x < (botRight.x + topLeft.x)/2) {
				if (coord.y > (topLeft.y + botRight.y)/2) {
					sequence[0] = nw;
					sequence[1] = ne;
					sequence[2] = sw;
					sequence[3] = se;
				}
				else {
					sequence[0] = sw;
					sequence[1] = se;
					sequence[2] = nw;
					sequence[3] = ne;
				}
			}
			else {
				if (coord.y > (topLeft.y + botRight.y)/2) {
					sequence[0] = ne;
					sequence[1] = nw;
					sequence[2] = se;
					sequence[3] = sw;
				}
				else {
					sequence[0] = se;
					sequence[1] = sw;
					sequence[2] = ne;
					sequence[3] = nw;
				}
			}
			
			for (QuadTree child : sequence) {
				if (child != null) {
					if (nodes[nodes.length - 1] == null) {
						child.search(coord, nodes);
					}
					else if (flatClosestPossDis(coord, child) < coord.flatDistanceFrom(nodes[nodes.length - 1].point)) {
						child.search(coord, nodes);
						
					}
				}
			}
		}
		else {
			// compare distance and add
			if (nodes[nodes.length - 1] == null) {
				nodes[nodes.length - 1] = node;
				sortLastNode(coord, nodes);
			}
			else if (coord.distanceFrom(node.point) < coord.distanceFrom(nodes[nodes.length - 1].point)) {
				nodes[nodes.length - 1] = node;
				sortLastNode(coord, nodes);
				
			}
		}
	}
	
	public double flatClosestPossDis(Coord coord, QuadTree tree) {
		if (coord.x < tree.topLeft.x) {
			if (coord.y > tree.topLeft.y) {
				return Math.sqrt(((coord.x - tree.topLeft.x)*(coord.x - tree.topLeft.x) + (coord.y - tree.topLeft.y)*(coord.y - tree.topLeft.y)));
			}
			else if (coord.y < tree.botRight.y) {
				return Math.sqrt(((coord.x - tree.topLeft.x)*(coord.x - tree.topLeft.x) + (coord.y - tree.botRight.y)*(coord.y - tree.botRight.y)));
			}
			else {
				return tree.topLeft.x - coord.x;
			}
		}
		else if (coord.x > tree.botRight.x) {
			if (coord.y > tree.topLeft.y) {
				return Math.sqrt(((coord.x - tree.botRight.x)*(coord.x - tree.botRight.x) + (coord.y - tree.topLeft.y)*(coord.y - tree.topLeft.y)));
			}
			else if (coord.y < tree.botRight.y) {
				return Math.sqrt(((coord.x - tree.botRight.x)*(coord.x - tree.botRight.x) + (coord.y - tree.botRight.y)*(coord.y - tree.botRight.y)));
			}
			else {
				return coord.x - botRight.x;
			}
		}
		else {
			if (coord.y > tree.topLeft.y) {
				return coord.y - tree.topLeft.y;
			}
			else if (coord.y < tree.botRight.y) {
				return tree.botRight.y - coord.y;
			}
			else {
				return 0;
			}
		}
	}
	
	public double closestPossDis(Coord coord, QuadTree tree) {
		if (coord.x < tree.topLeft.x) {
			if (coord.y > tree.topLeft.y) {
				return coord.distanceFrom(topLeft);
			}
			else if (coord.y < tree.botRight.y) {
				return coord.distanceFrom(new Coord(topLeft.x, botRight.y));
			}
			else {
				return coord.distanceFrom(new Coord(topLeft.x, coord.y));
			}
		}
		else if (coord.x > tree.botRight.x) {
			if (coord.y > tree.topLeft.y) {
				return coord.distanceFrom(new Coord(botRight.x, topLeft.y));
			}
			else if (coord.y < tree.botRight.y) {
				return coord.distanceFrom(botRight);
			}
			else {
				return coord.distanceFrom(new Coord(botRight.x, coord.y));
			}
		}
		else {
			if (coord.y > tree.topLeft.y) {
				return coord.distanceFrom(new Coord(coord.x, topLeft.y));
			}
			else if (coord.y < tree.botRight.y) {
				return coord.distanceFrom(new Coord(coord.x, botRight.y));
			}
			else {
				return 0;
			}
		}
	}
	
	public boolean isInBoundary(Node node) {
		return node.point.x >= topLeft.x && node.point.x <= botRight.x && node.point.y >= botRight.y && node.point.y <= topLeft.y;
	}
	
	public void sortLastNode(Coord coord, Node[] nodes) {
		double d = coord.distanceFrom(nodes[nodes.length - 1].point);
		Node temp;
		for (int i = 1; i < nodes.length; i++) {
			if (nodes[nodes.length - 1 - i] == null) {
				temp = nodes[nodes.length - 1 - i];
				nodes[nodes.length - 1 - i] = nodes[nodes.length - i];
				nodes[nodes.length - i] = temp; 
			}
			else if (coord.distanceFrom(nodes[nodes.length - 1 - i].point) > d) {
				temp = nodes[nodes.length - 1 - i];
				nodes[nodes.length - 1 - i] = nodes[nodes.length - i];
				nodes[nodes.length - i] = temp;
			}
			else {
				break;
			}
		}
	}
	
	public void printTree(String s) {
		System.out.println(s + topLeft.toString() + " " + botRight.toString());
		if (node == null) {	
			if (ne != null) {
				System.out.println(s + "ne: ");
				ne.printTree(s + "   ");
			}
			if (nw != null) {
				System.out.println(s + "nw: ");
				nw.printTree(s + "   ");
			}
			if (se != null) {
				System.out.println(s + "se: ");
				se.printTree(s + "   ");
			}
			if (sw != null) {
				System.out.println(s + "sw: ");
				sw.printTree(s + "   ");
			}
			
		}
		else {
			System.out.println(s + node + "\n");
		}
	}
}
