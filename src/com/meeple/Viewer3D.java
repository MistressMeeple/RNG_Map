package com.meeple;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleFanArray;
import javax.media.j3d.View;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.meeple.lib.math.VPoint3D;
import com.meeple.main.generate.FractalLandscape;
import com.meeple.main.generate.fractal.SquareList;
import com.meeple.main.generate.fractal.SquareList.Square;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Viewer3D extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BoundingSphere bounds;
	private javax.swing.JPanel drawingPanel;
	private SimpleUniverse univ = null;
	private BranchGroup scene = null;

	/**
	 * Setup a JFrame with 3D rendering of the fractal landscape. 
	 * @param landscape - Fractal Landscape
	 */
	public Viewer3D(FractalLandscape landscape) {
		System.setProperty("sun.awt.noerasebackground", "true");
		initComponents();
		Canvas3D c = createUniverse();
		drawingPanel.add(c, java.awt.BorderLayout.CENTER);
		try {
			scene = register(landscape);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		univ.addBranchGraph(scene);
		this.setVisible(true);
	}

	/**
	 * Creates the default view for the 3D universe
	 * including orbital view controls
	 * @return
	 */
	private Canvas3D createUniverse() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		univ = new SimpleUniverse(c);
		ViewingPlatform viewingPlatform = univ.getViewingPlatform();
		OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 500.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		//Get roughtly the right view
		Transform3D lookAt = new Transform3D();

		lookAt.lookAt(new Point3d(0.0, -1, 2.0), new Point3d(0.0, 0.0, 0.0), new Vector3d(0.0, 1.0, 0.0));
		lookAt.invert();
		univ.getViewingPlatform().getViewPlatformTransform().setTransform(lookAt);

		univ.getViewer().getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
		univ.getViewer().getView().setMinimumFrameCycleTime(5);

		return c;
	}

	/**
	 * Creates the Swing components
	 */
	private void initComponents() {
		drawingPanel = new javax.swing.JPanel();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Test");
		drawingPanel.setLayout(new java.awt.BorderLayout());
		drawingPanel.setPreferredSize(new java.awt.Dimension(700, 700));
		getContentPane().add(drawingPanel, java.awt.BorderLayout.CENTER);
		pack();
	}

	Point3d getPoint(VPoint3D p, FractalLandscape landscape) {
		double x = p.x;
		double y = p.y;
		double z = p.z;

		//move it half backwards
		x = x - (0.5 * landscape.getWidth());
		y = y - (0.5 * landscape.getHeight());
		//put the values between 0 and 1			
		x = x / landscape.getWidth();
		y = y / landscape.getHeight();
		z = z / ((landscape.getWidth() + landscape.getHeight()) / 2);
		return new Point3d(x, z, y);
	}

	BranchGroup register(FractalLandscape landscape) throws IOException {
		SquareList list = landscape.getSquareList();
		BranchGroup objRoot = new BranchGroup();
		TransformGroup allSquares = new TransformGroup();
		Shape3D square = new Shape3D();

		//Shape matieral
		Material m = new Material();
		m.setDiffuseColor(new Color3f(Color.gray));
		Appearance a = new Appearance();
		m.setLightingEnable(true);
		a.setMaterial(m);

		File f = new File("texture.png");
		BufferedImage image = ImageIO.read(f);
		TextureLoader loader = new TextureLoader(image);
		Texture texture = loader.getTexture();
		texture.setBoundaryModeS(Texture.WRAP);
		texture.setBoundaryModeT(Texture.WRAP);
		texture.setBoundaryColor(new Color4f(0.0f, 1.0f, 0.0f, 0.0f));
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		a.setTexture(texture);

		//Add lighting
		DirectionalLight light1 = new DirectionalLight(new Color3f(0.196078431f, 0.6f, 0.8f), new Vector3f(0.0f, 7.0f, -12.0f));
		light1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
		objRoot.addChild(light1);

		int vertexCount = 4;//4 vertices per square
		int totalVertexCount = vertexCount * list.squares.size();//total vertex count = vertex per face * how many faces/squares
		int[] vertex = new int[list.squares.size()];//assign an array to store the points. each face needs to be told its storing 4 vertices
		for (int i = 0; i < list.squares.size(); i++)
			vertex[i] = vertexCount;
		TriangleFanArray face = new TriangleFanArray(totalVertexCount, GeometryArray.COORDINATES | GeometryArray.NORMALS, vertex);
		int i = 0;
		//add the vertices to the face, which in turn gets added to the square. which is then added as a new child of allSquares
		for (Square s : list.squares) {

			face.setCoordinate(0 + i, getPoint(s.getP1(), landscape));
			face.setCoordinate(1 + i, getPoint(s.getP2(), landscape));
			face.setCoordinate(2 + i, getPoint(s.getP3(), landscape));
			face.setCoordinate(3 + i, getPoint(s.getP4(), landscape));

			i += vertexCount;

			square = new Shape3D(face, a);
			allSquares.addChild(square);
		}

		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		//t3d.setScale(2);
		//		t3d.transform(new Point3d(0, 0, -((landscape.getHeight() + landscape.getWidth()) / 2)));
		//t3d.rotX(-45);
		objScale.setTransform(t3d);

		Background bg = new Background(new Color3f(Color.cyan));
		bg.setApplicationBounds(bounds);
		objScale.addChild(bg);

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
