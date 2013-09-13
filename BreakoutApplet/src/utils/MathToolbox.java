package utils;
/***
 * A helper class containing math functions
 * @author Erik
 *
 */
public class MathToolbox {
	
	/***
	 * Gets the closest point on a line
	 * @param p The point from which the closest point on the line should be calculated
	 * @param p1 The start point of the line
	 * @param p2 The end point of the line
	 * @return The closest point on the line
	 */
	public static float[] closestPointOnLine(float[] p, float[] p1, float[] p2){
		float[] diff = {p[0] - p1[0], p[1] - p1[1]};
		float[] dir = {p2[0] - p1[0], p2[1] - p1[1]};
		
		float t = dot(diff, dir) / dot(dir, dir);
		
		if(t <= 0.0f) {
			float[] ret = {p1[0], p1[1]};
			return ret;
		}
		else if(t >= 1.0f) {
			float[] ret = {p2[0], p2[1]};
			return ret;
		}
		else {
			float[] ret = {(p1[0] + t * dir[0]), 
						   (p1[1] + t * dir[1])};
			return ret;
		}
	}
	
	/***
	 * Gets the dot product of two vectors
	 * @param v1 Vector 1
	 * @param v2 Vector 2
	 * @return The dot product
	 */
	public static float dot(float[] v1, float[] v2) {
		float sum = 0;
		for(int i = 0; i < v1.length; i++) {
			sum += v1[i] * v2[i];
		}
		return sum;
	}
}
