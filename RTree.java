package Project;

import java.lang.Math;
import java.util.Arrays;

import Project.QuadTree.Node;
import Project.Coord;

public class RTree {
	
	int count = 0;
	public Node node;
	public RTree parent;
	public RTree child1, child2, child3, overflow;
	public Coord topleft, bottomright;
	
	public void search(Coord coord, Node[] nodes) {
		RTree thisone = this;
		while(thisone.node == null)
		{
			thisone = thisone.child1;
		}
		for (int i = 0; i < 10; i++)
		{
			nodes[i] = thisone.node;
		}
	}
	
	public boolean insert(Node node) { //Boolean flags whether the root node has changed (1 means true)
		boolean not_leaf = true;
		if(this.child1 != null)
		{
			if(this.child1.node != null)
			{
				System.out.println("child1");
				not_leaf = false;
			}
		}
		if(this.child2 != null)
		{
			if(this.child2.node != null)
			{
				System.out.println("child2");
				not_leaf = false;
			}
		}
		if(this.child3 != null)
		{
			if(this.child3.node != null)
			{
				System.out.println("child3");
				not_leaf = false;
			}
		}
		
		if(this.child1 == null & this.child2 == null & this.child3 == null) //First insertion case
		{
			System.out.println("first");
			this.child1 = new RTree();
			this.child1.node = node; //Insert
		}
		else if(not_leaf) //Non-leaf case
		{
			System.out.println("Non-leaf case");
			if(this.child1 != null)
			{
				if(is_contained_by(this.child1, node))
				{
					System.out.println("Recursion on child 1");
					this.child1.insert(node);
					return false;
				}
			}
			if(this.child2 != null)
			{
				if(is_contained_by(this.child2, node))
				{
					System.out.println("Recursion on child 2");
					this.child2.insert(node);
					return false;
				}
			}
			if(this.child3 != null)
			{
				if(is_contained_by(this.child3, node))
				{
					System.out.println("Recursion on child 3");
					this.child3.insert(node);
					return false;
				}
			}

			
				System.out.println("Didn't fit into children.");
				double a1 = area_added(this.child1, node);
				double a2 = area_added(this.child2, node);
				double a3 = area_added(this.child3, node);
				if(a2 < a1)
				{
					if(a2 < a3)
					{
						System.out.println("Expanding child 2.");
						expand_box(this.child2, node);
						System.out.println("Inserting into child 2.");
						this.child2.insert(node);
					}
					else
					{
						System.out.println("Expanding child 3.");
						expand_box(this.child3, node);
						System.out.println("Inserting into child 3.");
						this.child3.insert(node);
					}
				}
				else
				{
					if(a1 < a3)
					{
						System.out.println("Expanding child 1.");
						expand_box(this.child1, node);
						System.out.println("Inserting into child 1.");
						this.child1.insert(node);
					}
					else
					{
						System.out.println("Expanding child 3.");
						expand_box(this.child3, node);
						System.out.println("Inserting into child 3.");
						this.child3.insert(node);
					}
				}
		}
		else //Leaf case
		{
			System.out.println("Leaf case");
			if(this.child1 == null)
			{
				System.out.println("Creating new child 1 tree and inserting the node.");
				this.child1 = new RTree();
				this.child1.node = node; //Insert
			}
			else if(this.child2 == null)
			{
				System.out.println("Creating new child 2 tree and inserting the node.");
				this.child2 = new RTree();
				this.child2.node = node; //Insert
			}
			else if(this.child3 == null)
			{
				System.out.println("Creating new child 3 tree and inserting the node.");
				this.child3 = new RTree();
				this.child3.node = node; //Insert
			}
			else //Full
			{
				System.out.println("Tree is full.  Inserting into overflow.");
				this.overflow = new RTree();
				this.overflow.node = node;
				System.out.println("Splitting tree.");
				return split_tree(this);
			}
		}
		return false;
	}
	
	public boolean is_contained_by(RTree tree, Node node)
	{
		if(node.point.x >= tree.topleft.x & node.point.x <= tree.bottomright.x)
		{
			if(node.point.y <= tree.topleft.y & node.point.y >= tree.bottomright.y)
				return true;
		}
		return false;
	}
	
	public double area_added(RTree tree, Node node)
	{
		if(tree == null)
		{
			return Double.MAX_VALUE;
		}
		double x_min = tree.topleft.x;
		double x_max = tree.bottomright.x;
		double y_min = tree.bottomright.y;
		double y_max = tree.topleft.y;
		double old_area = (x_max - x_min) * (y_max - y_min);
		if(node.point.x <= x_min)
		{
			x_min = node.point.x;
		}
		if(node.point.x >= x_max)
		{
			x_max = node.point.x;
		}
		if(node.point.y <= y_min)
		{
			y_min = node.point.y;
		}
		if(node.point.y >= y_max)
		{
			y_max = node.point.y;
		}
		double new_area = (x_max - x_min) * (y_max - y_min);
		return new_area - old_area;
	}
	
	public void expand_box(RTree tree, Node node)
	{	
		if(tree.topleft.x > node.point.x)
		{
			tree.topleft.x = node.point.x;
		}
		if(tree.topleft.y < node.point.y)
		{
			tree.topleft.y = node.point.y;
		}
		if(tree.bottomright.x < node.point.x);
		{
			tree.bottomright.x = node.point.x;
		}
		if(tree.bottomright.y > node.point.y)
		{
			tree.bottomright.y = node.point.y;
		}
	}
	
	public void recalc_box(RTree tree)
	{
		double max_x;
		double min_x;
		double max_y;
		double min_y;
		
		if(tree.child1.node != null) //Leaves
		{
			if(tree.child1.node.point.x > tree.child2.node.point.x)
			{
				max_x = tree.child1.node.point.x;
				min_x = tree.child2.node.point.x;
			}
			else
			{
				max_x = tree.child2.node.point.x;
				min_x = tree.child1.node.point.x;
			}
			if(tree.child1.node.point.y > tree.child2.node.point.y)
			{
				max_y = tree.child1.node.point.y;
				min_y = tree.child2.node.point.y;
			}
			else
			{
				max_y = tree.child2.node.point.y;
				min_y = tree.child1.node.point.y;
			}
		}
		else
		{
			if(tree.child1.topleft.x < tree.child2.topleft.x)
			{
				min_x = tree.child1.topleft.x;
			}
			else
			{
				min_x = tree.child2.topleft.x;
			}
			if(tree.child1.topleft.y > tree.child2.topleft.y)
			{
				max_y = tree.child1.topleft.y;
			}
			else
			{
				max_y = tree.child2.topleft.y;
			}
			
			if(tree.child1.bottomright.x > tree.child2.bottomright.x)
			{
				max_x = tree.child1.bottomright.x;
			}
			else
			{
				max_x = tree.child2.bottomright.x;
			}
			if(tree.child1.bottomright.y < tree.child2.bottomright.y)
			{
				min_y = tree.child1.bottomright.y;
			}
			else
			{
				min_y = tree.child2.bottomright.y;
			}
		}
		
		tree.topleft = new Coord(min_x, max_y);
		tree.bottomright = new Coord(max_x, min_y);
	}
	
	
	public boolean split_tree(RTree tree)
	{
			RTree new_tree = new RTree();
			new_tree.child1 = tree.child3;
			new_tree.child2 = tree.overflow;
			tree.child3 = null;
			tree.overflow = null;
			recalc_box(tree);
			recalc_box(new_tree);
			if(tree.parent == null)
			{
				System.out.println("Creating new root.");
				tree.parent = new RTree();
				tree.parent.child1 = tree;
				tree.parent.child2 = new_tree;
				recalc_box(tree.parent);
				return true;
			}
			else if(tree.parent.child1 == null)
			{
				System.out.println("Inserting new tree as parent's child 1");
				tree.parent.child1 = new_tree;
			}
			else if(tree.parent.child2 == null)
			{
				System.out.println("Inserting new tree as parent's child 2");
				tree.parent.child2 = new_tree;
			}
			else if(tree.parent.child3 == null)
			{
				System.out.println("Inserting new tree as parent's child 3");
				tree.parent.child3 = new_tree;
			}
			else
			{
				System.out.println("Inserted at parent. Parent overflowed and will now be split.");
				tree.parent.overflow = new_tree;
				split_tree(tree.parent);
			}
			return false;
	}
}