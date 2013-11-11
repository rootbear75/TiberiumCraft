


package tiberiumcraft.util;


import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class QuaternionUtil
{
	// public static
	
	public static Quaternion random(double random1, double random2, double random3)
	{
		double s = random1;
		double sigma1 = StrictMath.sqrt(1 - s);
		double sigma2 = StrictMath.sqrt(s);
		double theta1 = 2 * StrictMath.PI * random2;
		double theta2 = 2 * StrictMath.PI * random3;
		return new Quaternion((float) (StrictMath.sin(theta1) * sigma1), (float) (StrictMath.cos(theta1) * sigma1), (float) (StrictMath.sin(theta2) * sigma2),
				(float) (StrictMath.cos(theta2) * sigma2));
	}
	
	public static Vector3f rotate(Quaternion Q, Vector3f v)
	{
		Quaternion V = new Quaternion(v.x, v.y, v.z, 0);
		Quaternion out = new Quaternion();
		Quaternion.mul(Q, V, out);
		Quaternion.mul(out, inv(Q), out);
		return new Vector3f(out.x, out.y, out.z);
	}
	
	public static Quaternion conjugate(Quaternion Q)
	{
		return new Quaternion(-Q.x, -Q.y, -Q.z, Q.w);
	}
	
	public static double norm(Quaternion Q)
	{
		return StrictMath.sqrt(Q.x * Q.x + Q.y * Q.y + Q.z * Q.z + Q.w * Q.w);
	}
	
	public static float norm2(Quaternion Q)
	{
		return Q.x * Q.x + Q.y * Q.y + Q.z * Q.z + Q.w * Q.w;
	}
	
	public static Quaternion inv(Quaternion Q)
	{
		Quaternion.scale(1 / norm2(Q), conjugate(Q), Q);
		return Q;
	}
}
