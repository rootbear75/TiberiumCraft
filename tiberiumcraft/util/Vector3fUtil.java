


package tiberiumcraft.util;


import org.lwjgl.util.vector.Vector3f;

public class Vector3fUtil
{
	public static final Vector3f zeroVector = new Vector3f(0, 0, 0);
	public static final Vector3f unitX = new Vector3f(1, 0, 0);
	public static final Vector3f unitY = new Vector3f(0, 1, 0);
	public static final Vector3f unitZ = new Vector3f(0, 0, 1);
	
	public static Vector3f normalized(Vector3f V)
	{
		float d = V.length();
		if(d != 0)
		{
			return new Vector3f(V.x / d, V.y / d, V.z / d);
		}
		else
			return zeroVector;
	}
	
	/**
	 * reflect this vector across a normal
	 */
	public static Vector3f bounce(Vector3f v, Vector3f normal)
	{
		float x, y;
		
		x = v.x - 2 * normal.y * normal.z * (v.y - v.z);
		y = v.y - 2 * normal.x * normal.z * (v.z - v.x);
		return new Vector3f(x, y, v.z - 2 * normal.x * normal.y * (v.x - v.y));
	}
	
	/**
	 * rotate about a vector
	 */
	public static Vector3f rotate(Vector3f v, Vector3f about, double radian)
	{
		float c, cm1, sx, sy, sz, xyC, xzC, yzC;
		sx = (float) StrictMath.sin(radian);
		sy = sx * about.y;
		sz = sx * about.z;
		sx *= about.x;
		c = (float) StrictMath.cos(radian);
		cm1 = 1 - c;
		xyC = cm1 * about.x * about.y;
		xzC = cm1 * about.x * about.z;
		yzC = cm1 * about.y * about.z;
		return new Vector3f(v.x * c + about.x * about.x * cm1 + xyC + sz + xzC - sy, v.y * xyC - sz + c + about.y * about.y * cm1 + yzC + sx, v.z * xzC + sy
				+ yzC - sx + c + about.z * about.z * cm1);
	}
	
	/**
	 * determine if the vectors are perpendicular
	 */
	public static boolean orthogonal(Vector3f v1, Vector3f v2)
	{
		return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z) == 0;
	}
	
	/**
	 * determine if the vectors are parallel
	 */
	public static boolean parallel(Vector3f v1, Vector3f v2)
	{
		Vector3f temp = zeroVector;
		Vector3f.sub(v1, v2, temp);
		return temp.length() == v1.length() - v2.length();
	}
	
	public static String v3fToString(Vector3f v)
	{
		return "<".concat(Float.toString(v.x)).concat(",").concat(Float.toString(v.y)).concat(",").concat(Float.toString(v.z)).concat(">");
	}
}
