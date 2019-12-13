package Project;

import java.lang.Math;

//import java.util.Arrays;
import Project.QuadTree.Node;
import Project.Coord;

public class KDTree {
	public static class N {
		//name is county
		String state="",county="";
		double x,y;
		double log, lat;
		N left=null, right=null;
		
		// N constructors
		public N(double logitude, double latitude,String state, String county) {
			this.state = state;
			this.county = county;
			this.log = logitude;
			this.lat = latitude;
			//this.x = (this.log +127.675)*88/7;
			//this.y = (this.lat-50.7143)*70/4;
			this.x = Math.toRadians((this.log - 127.675)) * Math.cos(Math.toRadians((this.lat + 50.7143)/2));
			this.y = Math.toRadians(this.lat - 50.7143);
		}
		
		public N(String s, String n, double i, double j) {
			this.state = s;
			this.county = n;
			this.x = i;
			this.y = j;
			this.log = this.x*7/88-127.675;
			this.lat = this.y*4/70+50.7143;
		}
		public double flatDist(N a){
			return Math.sqrt((this.x-a.x)*(this.x-a.x)+(this.y-a.y)*(this.y-a.y));
		}

	}
	N root=null;

	// Tree constructors
	public KDTree(Node n) {
		this.root = new N(n.point.x,n.point.y,n.state,n.county);
	}
	public KDTree(N n) {
		this.root = n;
	}
	public KDTree(String s, String n, double i, double j) {
		this.root = new N(i,j,s,n);
	}
	
	public void insert(Node n) {
		N myNode = new N(n.point.x,n.point.y,n.state,n.county);
		insert_x(this.root,myNode);
	}
	
	public void insert_x(N r, N n) {
		
		
		if (n==null)	return;
		if (r==null) {
			r = new N(n.log,n.lat,n.state,n.county);
			return;
		}
		if (r.log==n.log && r.lat==n.lat) {
			return;
		}
		if (r.x>=n.x) {
			if (r.left!=null) {
				insert_y(r.left, n);
				return;
			}
			r.left = n;
		} else {
			if (r.right!=null) {
				insert_y(r.right,n);
				return;
			}
			r.right = n;
		}
		return;
		
		
	}
	public void insert_y(N r, N n) {
		if (n==null)	return;
		if (r==null) {
			r = n;
			return;
		}
		if (r.log==n.log && r.lat==n.lat) {
			return;
		}
		if (r.y>=n.y) {
			if (r.left!=null) {
				insert_x(r.left,n);
				return;
			}
			r.left = n;
		} else {
			if (r.right!=null) {
				insert_x(r.right,n);
				return;
			}
			r.right =n;
		}
		return;
	}
	
	public Node[] nearest(N r, Coord coord) {
		double log = coord.x;
		double lat = coord.y;
		double x = Math.toRadians((log - 127.675)) * Math.cos(Math.toRadians((lat + 50.7143)/2));
		double y = Math.toRadians(lat - 50.7143);
		N temp1 = r;
		N temp2 = null;
		boolean leftOrRight = true;	//if true check x, if false check y
		//iterate to find current closest point
		while (temp1!=null) {
			if (leftOrRight) {
				if (x<=temp1.x) {
					temp2 = temp1;
					temp1 = temp1.left;
				}
				else {
					temp2 = temp1;
					temp1 = temp1.right;
				}
			}
			else {
				if (y<=temp1.y) {
					temp2 = temp1;
					temp1 = temp1.left;
				}
				else {
					temp2 = temp1;
					temp1 = temp1.right;
				}
			}
			leftOrRight = !leftOrRight;
		}
		//current closest
		double closest = distance(temp2,log,lat);//Math.sqrt(Math.pow(temp2.x-x,2)+Math.pow(temp2.y-y,2));
		System.out.println(temp2.log+", "+temp2.lat);
		System.out.println();
		N[] arr = new N[10];
		int pl = 0;
		arr[0] = temp2;
		while(arr[pl]!=null) {
			System.out.println(arr[pl].log+","+arr[pl].lat);
		pl++;
		}
		System.out.println();
		N clsNode = closer1(r, closest, true,log, lat,arr);
		while (arr[9]==null) {
			clsNode = closer2(r,closest,log,lat,arr);
		}
		int c = 0;
		
		Node[] nodes = new Node[10];
		while (c<10 && arr[c]!=null) {
			nodes[c]=new Node(arr[c].log,arr[c].lat,arr[c].state,arr[c].county);
			c++;
		}
		
		return nodes;
		
	}
	public N closer1(N r, double closest, boolean lOrR, double log, double lat, N[] accessed) {
		double x = Math.toRadians((log - 127.675)) * Math.cos(Math.toRadians((lat + 50.7143)/2));
		double y = Math.toRadians(lat - 50.7143);
		if (r==null) return accessed[0];
		N tmp_left = null, tmp_right=null;
		if (lOrR) {
			if (closest >= Math.abs(r.x-x)) {
				double ifSmaller = distance(r,log,lat);
				if (ifSmaller<closest) {
					add(accessed,r,log,lat);
					int c=0;
					N t=null;
					while (c<10 && accessed[c]!=null) {
						t = accessed[c];
						//System.out.println(t.x+", "+t.y);	
						c++;
					}
					double d = distance(t,log,lat);
					//System.out.println();
					tmp_left = closer1(r.left, d,!lOrR,log,lat,accessed);
					tmp_right = closer1(r.right, d,!lOrR,log,lat,accessed);
				}
				else {
					//System.out.println("[[["+r.x+","+ r.y+"\n");
					tmp_left = closer1(r.left, closest,!lOrR,log,lat,accessed);
					tmp_right = closer1(r.right, closest,!lOrR,log,lat,accessed);
				}
			}
			else return closer1(r.left, closest, !lOrR,log,lat,accessed);
		}
		if (!lOrR) {
			if (closest >= Math.abs(r.y-y)) {
				double ifSmaller = distance(r,log,lat);
				if (ifSmaller<closest) {
					add(accessed,r,log,lat);
					int c=0;
					N t=null;
					while (c<10 && accessed[c]!=null) {
						
						t = accessed[c];
						c++;
					}
					double d = distance(t,log,lat);
					//System.out.println();
					tmp_left = closer1(r.left, d,!lOrR,log,lat,accessed);
					tmp_right = closer1(r.right, d,!lOrR,log,lat,accessed);
				}
				else {
					
					tmp_left = closer1(r.left, closest,!lOrR,log,lat,accessed);
					tmp_right = closer1(r.right, closest,!lOrR,log,lat,accessed);
				}
			}
			else return closer1(r.left,closest,!lOrR,log,lat,accessed);
		}
		
		if (tmp_left==null && tmp_right==null) return accessed[0];
		else if (tmp_left==null) {
			return tmp_right;
		}
		else if (tmp_right==null) {
			return tmp_left;
		}
		if (distance(tmp_left,log,lat)<=distance(tmp_right,log,lat)) {
			return tmp_left;
		}
		return tmp_right;
	}
	
	// enlarge the circle to 
	public N closer2(N r,double closest, double log, double lat, N[] accessed) {
		double x = Math.toRadians((log - 127.675)) * Math.cos(Math.toRadians((lat + 50.7143)/2));
		double y = Math.toRadians(lat - 50.7143);
		if (accessed[9]==null) {
			
			closer1(r,closest+5,true,log,lat,accessed);
			return closer2(r,closest+5,log,lat,accessed);
		}
		return accessed[0];
	}
	
	
	//add or substitute elements in array
	
	public N[] add(N[] accessed,N r, double log, double lat) {
		double x = Math.toRadians((log - 127.675)) * Math.cos(Math.toRadians((lat + 50.7143)/2));
		double y = Math.toRadians(lat - 50.7143);
		if (accessed[9]==null) {
			for (int i=0;i<10;i++) {
				if (accessed[i]==null) {
					accessed[i]=r;
					break;
				}
				if (accessed[i].equals(r)) {
					break;
				}
			}
			N[] newNode = sort(accessed,log,lat);
			return newNode;
		}
		else {
			for (int i=0;i<10;i++) {
				if (accessed[i].equals(r)) {
					break;
				}
				if (i==9) {
					if (distance(accessed[9],log,lat)>distance(r,log,lat)) {
						accessed[9]=r;
					}
					
				}
			}
		}
		N[] newNode = sort(accessed,log,lat);
		return newNode;
	}
	
	
	// sort by distance
	public N[] sort(N[] accessed, double log, double lat) {
		double x = Math.toRadians((log - 127.675)) * Math.cos(Math.toRadians((lat + 50.7143)/2));
		double y = Math.toRadians(lat - 50.7143);
		if (accessed[9]!=null) {
			//bubble sort
			for (int j=0;j<9;j++) {
				for (int i = 0; i<9;i++) {
					if (distance(accessed[i],log,lat)>distance(accessed[i+1],log,lat)) {
						N temp = accessed[i];
						accessed[i] = accessed[i+1];
						accessed[i+1] = temp;
					}
				}
			}
			return accessed;
			}
		int c=0;
		//bubble sort
		for (int i=0;i<9;i++) {
			while (accessed[c]!=null && accessed[c+1]!=null) {
				if (distance(accessed[c],log,lat)>distance(accessed[c+1],log,lat)) {
					N temp = accessed[c];
					accessed[c] = accessed[c+1];
					accessed[c+1] = temp;
				}
				c++;
			}
			c=0;
		}
		
		return accessed;
		
	}
	//calculating distance between the given position to a node
	public double distance(N n, double log, double lat) {
		double x = Math.toRadians((log - n.log)) * Math.cos(Math.toRadians((lat + n.lat)/2));
		double y = Math.toRadians(lat - n.lat);
		return Math.sqrt(x*x + y*y) * 6371;
		
	}
	
	// print all coordinates in order. For debugging purpose...
	public void inOrderPrint(N r) {
		if (r==null) return;
		inOrderPrint(r.left);
		System.out.println("("+r.log+", "+r.lat+")");
		inOrderPrint(r.right);
	}
	/*
	public static void main(String argv[]) {
		double[] x_arr = new double[] {0,1,3,100,5,5,1,0,12,-4,-100,-34,-55,11,11,43,0,61,23};
		double[] y_arr = new double[] {0,1,4,100,-1,-3,3,1,1,3,23,-98,-3,100,-78,33,66,9,12};
		KDTree KDT = new KDTree("NA","NA",x_arr[0],y_arr[0]);
		for (int i=1;i<19;i++) {
			N n1 = new N("NA","NA",x_arr[i],y_arr[i]);	
			insert_x(KDT.root,n1);
		}
		inOrderPrint(KDT.root);
		N[] t = nearest(KDT.root,0,0);
		for (N nd : t) {
			System.out.println("x="+nd.x+" y="+nd.y);
		}
		
		
	}*/
}
