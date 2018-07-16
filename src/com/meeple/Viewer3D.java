package com.meeple;

import java.awt.GraphicsConfiguration;
import java.util.Random;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleFanArray;
import javax.vecmath.Point3d;

import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.FractalLandscape;
import com.meeple.main.generate.fractal.SquareList;
import com.meeple.main.generate.fractal.SquareList.Square;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Viewer3D extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BoundingSphere bounds;

	public static void main(String[] args) {

		System.setProperty("sun.awt.noerasebackground", "true");
		new Viewer3D().setVisible(true);

	}

	public Viewer3D() {
		FractalLandscape fl = new FractalLandscape(100, 100, new Random(1), 4);
		// Initialize the GUI components
		initComponents();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		Canvas3D c = createUniverse();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);

		// Create the content branch and add it to the universe
		fl.print();
		scene = register(fl);
		univ.addBranchGraph(scene);

	}

	private Canvas3D createUniverse() {
		// Get the preferred graphics configuration for the default screen
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		// Create a Canvas3D using the preferred configuration
		Canvas3D c = new Canvas3D(config);

		// Create simple universe with view branch
		univ = new SimpleUniverse(c);
		

		// add mouse behaviors to the ViewingPlatform
		ViewingPlatform viewingPlatform = univ.getViewingPlatform();
		OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 500.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		univ.getViewingPlatform().setNominalViewingTransform();

		// Ensure at least 5 msec per frame (i.e., < 200Hz)
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return c;
	}

	private void initComponents() {
		drawingPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Test");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
		getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);

		pack();
	}

	private javax.swing.JPanel drawingPanel;

	private SimpleUniverse univ = null;
	private BranchGroup scene = null;

	boolean scaleDown = true;

	Point3d getPoint(VPoint3D p, FractalLandscape landscape) {
		double x = p.x;
		double y = p.y;
		double z = p.z;

		//move it half backwards
		x = x - (0.5 * landscape.getWidth());
		y = y - (0.5 * landscape.getHeight());
		z = z - (1.5 * landscape.getHeight());
		//put the values between 0 and 1
		if (scaleDown) {
			x = x / landscape.getWidth();
			y = y / landscape.getHeight();
			z = z / (landscape.getWidth() + landscape.getHeight()) / 2;
		}
		return new Point3d(x, z, y);
	}

	BranchGroup register(FractalLandscape landscape) {
		SquareList list = landscape.getSquareList();
		BranchGroup objRoot = new BranchGroup();
		TransformGroup allSquares = new TransformGroup();
		Shape3D square = new Shape3D();

		int vertexCount = 4;
		int totalVertexCount = vertexCount * list.squares.size();
		int[] vertex = new int[list.squares.size()];
		for (int i = 0; i < list.squares.size(); i++)
			vertex[i] = vertexCount;
		TriangleFanArray face = new TriangleFanArray(totalVertexCount, GeometryArray.COORDINATES | GeometryArray.NORMALS, vertex);
		int i = 0;
		for (Square s : list.squares) {

			face.setCoordinate(0 + i, getPoint(s.getP1(), landscape));
			face.setCoordinate(1 + i, getPoint(s.getP2(), landscape));
			face.setCoordinate(2 + i, getPoint(s.getP3(), landscape));
			face.setCoordinate(3 + i, getPoint(s.getP4(), landscape));

			i += vertexCount;

			square = new Shape3D(face);
			allSquares.addChild(square);
		}

		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		//t3d.setScale(2);
		t3d.transform(new Point3d(0, 0, -((landscape.getHeight() + landscape.getWidth())/2)  ));

		//t3d.rotX(-45);
		objScale.setTransform(t3d);


		
		TransformGroup squareTransform = new TransformGroup();
		squareTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		objScale.addChild(squareTransform);
		Transform3D zAxis = new Transform3D();
		zAxis.setIdentity();
		zAxis.rotX(0);
		zAxis.rotY(0);
		zAxis.rotZ(0);
		
		Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 8000, 0, 0, 0, 0, 0);

		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, squareTransform, zAxis, 0.0f, (float) Math.PI * 2.0f);
		rotator.setSchedulingBounds(bounds);
		squareTransform.addChild(allSquares);
		squareTransform.addChild(rotator);


		objRoot.addChild(objScale);

		objRoot.compile();
		return objRoot;
	}
}
