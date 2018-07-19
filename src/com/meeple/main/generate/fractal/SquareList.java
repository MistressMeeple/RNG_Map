package com.meeple.main.generate.fractal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import com.meeple.lib.math.MathHelper;
import com.meeple.lib.math.VPoint2D;
import com.meeple.lib.math.VPoint3D;

public class SquareList implements Cloneable {
	public class Square {
		//INDEX from the linked arraylist of points
		public int p1, p2, p3, p4;

		public Square(VPoint3D p1, VPoint3D p2, VPoint3D p3, VPoint3D p4) {
			pointList.add(p1);
			pointList.add(p2);
			pointList.add(p3);
			pointList.add(p4);

			this.p1 = pointList.indexOf(p1);
			this.p2 = pointList.indexOf(p2);
			this.p3 = pointList.indexOf(p3);
			this.p4 = pointList.indexOf(p4);
		}

		public VPoint3D getP1() {
			return pointList.get(p1);
		}

		public VPoint3D getP2() {
			return pointList.get(p2);
		}

		public VPoint3D getP3() {
			return pointList.get(p3);
		}

		public VPoint3D getP4() {
			return pointList.get(p4);
		}

	}

	//HashMap to so fast checks if we already have an element in the list 
	public HashMap<VPoint2D, Double> hashmap = new HashMap<VPoint2D, Double>();
	public ArrayList<VPoint3D> pointList = new ArrayList<VPoint3D>() {
		/**
				* Overriding the add method so we sort as soon as added
				*/

		private static final long serialVersionUID = 1L;

		/**
			* Should disable duplicates because of the check if its already in the hashmap
			* @param Point to add
			* @return whether the point got added
			*/
		public boolean add(VPoint3D e) {
			if (hashmap.put(new VPoint2D(e.x, e.y), e.z) == null) {
				return super.add(e);
			}
			return false;
		};

		@Override
		public int indexOf(Object o) {
			if (!(o instanceof VPoint3D)) {
				return 0;
			}
			VPoint3D p = (VPoint3D) o;
			for (int i = 0; i < this.size(); i++) {
				VPoint3D e = get(i);
				if (MathHelper.compareDoubles(p.x, e.x) && MathHelper.compareDoubles(p.y, e.y)) {
					return i;
				}
			}
			return 0;
		};
	};
	public ArrayList<Square> squares = new ArrayList<Square>();

	public void sortPoints() {
		ArrayList<VPoint3D> pList = new ArrayList<VPoint3D>(pointList);

		pointList.sort(new Comparator<VPoint3D>() {

			@Override
			public int compare(VPoint3D o1, VPoint3D o2) {
				int result = Double.compare(o1.x, o2.x);
				if (result == 0) {
					// both X are equal -> compare Y too
					result = Double.compare(o1.y, o2.y);
				}
				return result;
			}
		});
		for (Square s : squares) {
			s.p1 = pointList.indexOf(pList.get(s.p1));
			s.p2 = pointList.indexOf(pList.get(s.p2));
			s.p3 = pointList.indexOf(pList.get(s.p3));
			s.p4 = pointList.indexOf(pList.get(s.p4));
		}

	}

	public void addSquare(VPoint3D p1, VPoint3D p2, VPoint3D p3, VPoint3D p4) {
		squares.add(new Square(p1, p2, p3, p4));
	}

	public void addSquare(Square s) {
		squares.add(s);
	}

	public int size() {
		return squares.size();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object clone() {

		SquareList clone = new SquareList();
		try {
			clone.hashmap = (HashMap<VPoint2D, Double>) this.hashmap.clone();
			clone.pointList = (ArrayList<VPoint3D>) this.pointList.clone();
			clone.squares = (ArrayList<Square>) this.squares.clone();
		} catch (Exception e) {
			return null;
		}
		return clone;
	}

	public void addSquares(ArrayList<Square> squares) {
		for (Square s : squares) {
			addSquare(s);
		}

	}
}
