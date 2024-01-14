package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;
import javafx.util.Pair;

public class Nodes {
	
	private Node[][][] nodes;
	private ArrayList<Node> border;
	
	private int xrows, yrows, zrows, gap, radius;
	
	ArrayList<NodeMesh> meshes = new ArrayList<>();
	ArrayList<Connector> connectors = new ArrayList<>();
	
	public Nodes(int xrows, int yrows, int zrows, int gap, int radius) {
		this.xrows = xrows; this.yrows = yrows; this.zrows = zrows; this.gap = gap; this.radius = radius;
		nodes = new Node[xrows][yrows][zrows];
		border = new ArrayList<>();
		
		/*for(int i = 0; i < xrows; i++)
			for(int j = 0; j < yrows; j++)
				for(int k = 0; k < zrows; k++) {
					//Node node = new Node(radius, radius, radius);
					//PhongMaterial mat = new PhongMaterial();
					//mat.setDiffuseColor(new Color(0.6, 0.3, 0.9, 1.0));
					
					//node.setOpacity(0);
					//node.setMaterial(mat);
					//nodes[i][j][k] = node;
					//node.getTransforms().add(new Translate(((-radius - gap) * (xrows/2)) + (i * (radius + gap)), ((-radius - gap) * (yrows/2)) + (j * (radius + gap)), ((-radius - gap) * (zrows/2)) + (k * (radius + gap))));
					
				}*/
		
		for(int i = -1; i <= xrows; i++)
			for(int j = -1; j <= yrows; j++)
				for(int k = -1; k <= zrows; k++)
					if(((i == -1 || i == xrows) && (j == -1 || j == yrows)) || ((i == -1 || i == xrows) && (k == -1 || k == zrows)) || ((j == -1 || j == yrows) && (k == -1 || k == zrows))) {
						Node node = new Node(radius - gap, radius - gap, radius - gap);
						PhongMaterial mat = new PhongMaterial();
						mat.setDiffuseColor(new Color(0, 0, 0, .3));
						node.setMaterial(mat);
						node.getTransforms().add(new Translate(((-radius - gap) * (xrows/2)) + (i * (radius + gap)), ((-radius - gap) * (yrows/2)) + (j * (radius + gap)), ((-radius - gap) * (zrows/2)) + (k * (radius + gap))));
						border.add(node);
					}
		Random rand = new Random();
		
		int nodesNum = 30; //rand.nextInt(xrows / 2);
		int attempts = 0;
		
		NUM: for(int l = 0; l < nodesNum; l++) {
			
			ArrayList<V3> meshNodes = new ArrayList<>();
			
			int centerX = rand.nextInt(xrows+1), centerY = rand.nextInt(yrows+1), centerZ = rand.nextInt(zrows+1);
			int widthRadi = rand.nextInt(xrows/5)+1, lengthRadi = rand.nextInt(zrows/5)+1, heightRadi = rand.nextInt(yrows/7)+1;
			
			System.out.println(l + " : ( " + centerX + ", " + centerY + ", " + centerZ + " )" + "\t Width: " + widthRadi + "\t Length: " + lengthRadi + "\t Height: " + heightRadi);
			
			boolean valid = true;
			
			for(int i = -widthRadi; i <= widthRadi; i++)
				for(int j = -lengthRadi; j <= lengthRadi; j++)
					for(int k = -heightRadi; k <= heightRadi; k++)
						if(i + centerX >= 0 && i + centerX < xrows && j + centerZ >= 0 && j + centerZ < zrows && k + centerY >= 0 && k + centerY < yrows)
							meshNodes.add(new V3(i + centerX,j + centerZ,k + centerY));
						else {valid = false; System.out.println("INVALID"); attempts++; l--; continue NUM;}
			
			
			for(NodeMesh mesh : meshes)
				if(mesh.intersects(toArray(meshNodes))) {
					valid = false; l--; attempts++; break;
				}
			
			if(valid) {
				//for(V3 v3 : meshNodes)
				//	highlightNode(v3.v1(), v3.v3(), v3.v2());
			
				NodeMesh mesh = new NodeMesh(toArray(meshNodes));
				mesh.setCenter(new V3(centerX, centerY, centerZ));
				mesh.setDims(widthRadi * 2 + 1, lengthRadi * 2 + 1, heightRadi * 2 + 1);
				meshes.add(mesh);
				highlightMesh(mesh, Color.LIGHTBLUE);
			}
		}
		
		System.out.println("Given Size: " + nodesNum + "\tActual Nodes: " + meshes.size() + "\tAttempts: " + attempts);
			
		for(int o = 0; o < meshes.size(); o++) {
		
		NodeMesh mesh = meshes.get(o);
		V3 center = mesh.getCenter();
		int h = center.v1(), k = center.v2(), l = center.v3();
		
		ArrayList<Pair<Integer, Double>> distances = new ArrayList<>();
		
		System.out.println("(x,y,z) center: " + h + ", " + k +", " + l);
		double shortest = Integer.MAX_VALUE;
		int index = -1;
		
		for(int i = 0; i < meshes.size(); i++) {
			if(i != o) {
				V3 otherCenter = meshes.get(i).getCenter();
				int q = otherCenter.v1(), r = otherCenter.v2(), s = otherCenter.v3();
				double distance = Math.sqrt(Math.pow((q-h), 2) + Math.pow((r-k), 2) + Math.pow((s-l), 2));
				distances.add(new Pair<>(i, distance));
				
			
				//System.out.println("Distance: " + distance);
			}	
		}
		
		distances.sort(new Comparator<>() {
			@Override
			public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
				if(o1.getValue() > o2.getValue())
					return 1;
				else if(o1.getValue() == o2.getValue())
					return 0;
				else return -1;
			}	
		});
		
		System.out.println(distances);
		
		
		for(int p = 0; p < 5; p++) {
			ArrayList<V3> points = new ArrayList<>();
			NodeMesh closest = meshes.get(distances.get(p).getKey());
			int q = closest.getCenter().v1(), r = closest.getCenter().v2(), s = closest.getCenter().v3();
			int dx = Math.abs(q - h);
			int dy = Math.abs(r - k);
			int dz = Math.abs(s - l);
			int xs, ys, zs, p1, p2;
			if(q > h) xs = 1; else xs = -1;
			if(r > k) ys = 1; else ys = -1;
			if(s > l) zs = 1; else zs = -1;
			
			// Driving axis is X-axis
		    if (dx >= dy && dx >= dz) {        
		        p1 = 2 * dy - dx;
		        p2 = 2 * dz - dx;
		        while (h != q) {
		            h += xs;
		            if (p1 >= 0) {
		                k += ys;
		                p1 -= 2 * dx;
		            }
		            if (p2 >= 0) {
		                l += zs;
		                p2 -= 2 * dx;
		            }
		            p1 += 2 * dy;
		            p2 += 2 * dz;
		            points.add(new V3(h, k, l));
		        }
		  
		    // Driving axis is Y-axis
		    } else if (dy >= dx && dy >= dz) {       
		        p1 = 2 * dx - dy;
		        p2 = 2 * dz - dy;
		        while (k != r) {
		            k += ys;
		            if (p1 >= 0) {
		                h += xs;
		                p1 -= 2 * dy;
		            }
		            if (p2 >= 0) {
		                l += zs;
		                p2 -= 2 * dy;
		            }
		            p1 += 2 * dx;
		            p2 += 2 * dz;
		            points.add(new V3(h, k, l));
		        }
		    // Driving axis is Z-axis
		    } else {        
		        p1 = 2 * dy - dz;
		        p2 = 2 * dx - dz;
		        while (l != s) {
		            l += zs;
		            if (p1 >= 0) {
		                k += ys;
		                p1 -= 2 * dz;
		            }
		            if (p2 >= 0) {
		                h += xs;
		                p2 -= 2 * dz;
		            }
		            p1 += 2 * dy;
		            p2 += 2 * dx;
		            points.add(new V3(h, k, l));
		        }
		    }
		    
		    points.remove(closest.getCenter());
			points.remove(center);
			boolean intersects = false;
		    Connector connector = new Connector(center, closest.getCenter(), mesh, closest, distances.get(p).getValue(), toArray(points));
		    for(Connector otherConnectors : connectors)
		    	if(connector.interesects(otherConnectors.getConnectorPoints())) {
		    		System.out.println("DOES INTERSECT");
		    		intersects = true;
		    	}
		    
		    if(!intersects) {
		    	connectors.add(connector);
		    	mesh.addConnectors(connector);
		    	closest.addConnectors(connector);
		    	System.out.println("DOESN'T INTERSECT");
		    }
	    
	    
		}
		
		for(Connector connector : connectors) {
			for(V3 point : connector.getConnectorPoints())
				highlightNode(point.v1(), point.v2(), point.v3(), new Color(0, 0, 0, 1.0));
		}
		
		}
	}
	
	public V3[] toArray(ArrayList<V3> list) {
		V3[] arr = new V3[list.size()];
		for(int i = 0; i < arr.length; i++)
			arr[i] = list.get(i);
		return arr;
	}
	
	public void highlightNode(int i, int j, int k) {
		nodes[i][j][k] = new Node(radius + gap, radius + gap, radius + gap);
		nodes[i][j][k].getTransforms().add(new Translate(((-radius - gap) * (xrows/2)) + (i * (radius + gap)), ((-radius - gap) * (yrows/2)) + (j * (radius + gap)), ((-radius - gap) * (zrows/2)) + (k * (radius + gap))));
		nodes[i][j][k].setTranslateX(nodes[i][j][k].getTranslateX() - gap/2);
		nodes[i][j][k].setTranslateY(nodes[i][j][k].getTranslateY() - gap/2);
		nodes[i][j][k].setTranslateZ(nodes[i][j][k].getTranslateZ() - gap/2);
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(new Color(0.6, 0.7, 0.9, 1.0));
		nodes[i][j][k].setOpacity(1.0);
		nodes[i][j][k].setMaterial(mat);
	}
	
	public void highlightNode(int i, int j, int k, Color color) {
		nodes[i][j][k] = new Node(radius + gap + 10, radius + gap + 10, radius + gap + 10);
		nodes[i][j][k].getTransforms().add(new Translate(((-radius - gap) * (xrows/2)) + (i * (radius + gap)), ((-radius - gap) * (yrows/2)) + (j * (radius + gap)), ((-radius - gap) * (zrows/2)) + (k * (radius + gap))));
		nodes[i][j][k].setTranslateX(nodes[i][j][k].getTranslateX() - gap/2);
		nodes[i][j][k].setTranslateY(nodes[i][j][k].getTranslateY() - gap/2);
		nodes[i][j][k].setTranslateZ(nodes[i][j][k].getTranslateZ() - gap/2);
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(color);
		nodes[i][j][k].setOpacity(1.0);
		nodes[i][j][k].setMaterial(mat);
	}
	
	public void highlightMesh(NodeMesh mesh, Color color) {
		int[] dims = mesh.getDims();
		V3 center = mesh.getCenter();
		Node node = new Node((radius + gap) * dims[0], (radius + gap) * dims[2], (radius + gap) * dims[1]);
		node.getTransforms().add(new Translate(((-radius - gap) * (xrows/2)) + (center.v1() * (radius + gap)), ((-radius - gap) * (yrows/2)) + (center.v2() * (radius + gap)), ((-radius - gap) * (zrows/2)) + (center.v3() * (radius + gap))));
		node.setTranslateX(node.getTranslateX() - gap/2);
		node.setTranslateY(node.getTranslateY() - gap/2);
		node.setTranslateZ(node.getTranslateZ() - gap/2);
		PhongMaterial mat = new PhongMaterial();
		mat.setDiffuseColor(color);
		node.setOpacity(1.0);
		node.setMaterial(mat);
		mesh.setNode(node);
	}
	
	public Node[][][] getNodes() {
		return this.nodes;
	}
	
	public void setNodes(Node[][][] nodes) {
		this.nodes = nodes;
	}
	
	public Group toGroup() {
		Group group = new Group();
		for(int i = 0; i < nodes.length; i++)
			for(int j = 0; j < nodes[i].length; j++)
				for(int k = 0; k < nodes[i][j].length; k++)
					if(nodes[i][j][k]!=null)
						group.getChildren().add(nodes[i][j][k]);
		
		for(NodeMesh mesh : meshes)
			group.getChildren().add(mesh.getNode());
		return group;
	}
	
	public Group border() {
		Group group = new Group();
		for(Node node : border)
			group.getChildren().add(node);
		return group;
	}
	
	public void hide() {
		for(NodeMesh mesh : meshes)
			mesh.getNode().setOpacity(0.0);
			//for(V3 point : mesh.getMeshPoints())
				//nodes[point.v1()][point.v3()][point.v2()].setOpacity(0.0);
	}
	
	public void show() {
		for(NodeMesh mesh : meshes)
			mesh.getNode().setOpacity(1.0);
			//for(V3 point : mesh.getMeshPoints())
				//nodes[point.v1()][point.v3()][point.v2()].setOpacity(1.0);
	}
	
	public void pickTwoRandom() {
		
		NodeMesh meshOne = meshes.get(new Random().nextInt(meshes.size()));
		NodeMesh meshTwo = meshes.get(new Random().nextInt(meshes.size()));
		ArrayList<Connector> curr = new ArrayList<>();
		if(!meshOne.equals(meshTwo)) {
		
		for(V3 point : meshOne.getMeshPoints())	
			highlightNode(point.v1(), point.v3(), point.v2(), Color.LIMEGREEN);
		
		for(V3 point : meshTwo.getMeshPoints())	
			highlightNode(point.v1(), point.v3(), point.v2(), Color.LIMEGREEN);
		
		System.out.println(traverse(meshOne, meshTwo, new ArrayList<>(), new ArrayList<>()));
			
		} else System.out.println("SAME MESH");
	}
	
	public ArrayList<Connector> traverse(NodeMesh start, NodeMesh end, ArrayList<NodeMesh> previous, ArrayList<Connector> line) {
		
		if(start.equals(end)) {
			System.out.println("FOUND");
			return line;
		}
		
		for(Connector connector : start.getConnectors()) {
			for(NodeMesh visited : previous)
				if(!visited.equals(connector.getEndNode())) {
					previous.add(connector.getEndNode());
					line.add(connector);
					line = traverse(connector.getEndNode(), end, previous, line);
				}
			
		}
		
		return line;
	}
	
	
}

class NodeMesh {
	
	private V3[] meshPoints;
	private V3 center;
	private int width, length, height;
	private ArrayList<Connector> connectors;
	private Node node;
	
	public NodeMesh(V3... meshPoints) {
		this.meshPoints = meshPoints;
		connectors = new ArrayList<>();
	}
	
	public V3[] getMeshPoints() {
		return this.meshPoints;
	}
	
	public V3 getCenter() {
		return this.center;
	}
	
	public int[] getDims() {
		return new int[] {width, length, height};
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public void setCenter(V3 center) {
		this.center = center;
	}
	
	
	public void setDims(int width, int length, int height) {
		this.width = width;
		this.length = length;
		this.height = height;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	
	public void addConnectors(Connector connector) {
		connectors.add(connector);
	}
	
	public ArrayList<Connector> getConnectors() {
		return this.connectors;
	}
	
	public boolean intersects(V3[] otherMeshPoints) {
		boolean output = false;
		PARENT: for(V3 thisCurrentPoint : meshPoints)
			for(V3 otherCurrentPoint : otherMeshPoints)
				if(thisCurrentPoint.equals(otherCurrentPoint)) {
					output = true;
					break PARENT;
				}
		return output;
	}
	
	
	
}

class Connector {
	
	private V3[] connectorPoints;
	private V3 start, end;
	private NodeMesh startNode;
	private NodeMesh endNode;
	private double distance;

	public Connector(V3 start, V3 end, NodeMesh startNode, NodeMesh endNode, double distance, V3... connectorPoints) {
		this.start = start;
		this.end = end;
		this.connectorPoints = connectorPoints;
		this.startNode = startNode;
		this.endNode = endNode;
		this.distance = distance;
	}
	
	public V3[] getConnectorPoints() {
		return this.connectorPoints;
	}
	
	public V3 getStart() {
		return this.start;
	}
	
	public V3 getEnd() {
		return this.end;
	}
	
	public NodeMesh getStartNode() {
		return this.startNode;
	}
	
	public NodeMesh getEndNode() {
		return this.endNode;
	}
	
	public boolean interesects(V3[] otherPoints) {
		boolean output = false;
		for(V3 point : connectorPoints)
			for(V3 otherPoint : otherPoints)
				if(point.equals(otherPoint))
					output = true;
		return output;
	}
}

class V3 {
	
	private int v1, v2, v3;
	public V3(int v1, int v2, int v3) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
	}
	
	public int v1() {
		return v1;
	}
	
	public int v2() {
		return v2;
	}
	
	public int v3() {
		return v3;
	}
	
	public boolean equals(V3 other) {
		return (this.v1 == other.v1 && this.v2 == other.v2() && this.v3 == other.v3());
	}
}

