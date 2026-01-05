public class TrigonometryUtility {

	public static double calculateStandardAngle(double xA, double yA, double xB, double yB) {

			// Calculate standard angles in java that corresponds to the screen coordinates instead of the
			// mathematical definition. This is somewhat complicated because, opposite to a Cartesian grid
			// the y dimension increases towards the bottom of the screen. Thus, our 'standard'
			// angle is reversed from the mathematical definition. 0 is still to the right,
			// but positive angles indicate clockwise rotation. 90 is the bottom of the screen,
			// 180 is the left, and 270 is the top. That allows the sine, cosine, and tangent ratios
			// still correctly correspond to the standard angle
	
			// Thus, quadrants are reversed. Top right is I, bottom right is II,
		// bottom left is III, top left is IV

		// As a further complication, methods in the Math class work with radians, which
		// are not necessarily intuitive. So, the examples below convert to radians when
		// necessary, but we store the angle in degrees.

		double x = xB - xA;
		double y = yB - yA;
		double thetaR = 0;

		//if x is zero, tan is undefined... theta
		if (x == 0) {			
			if (y >= 0) {
				thetaR = 90;
			}
			else {
				thetaR = 270;
			}
		}		
		else {
			// calculate tan ratio
			double tan = y / x;
			// calculate reference angle (thetaR)
			// note the conversion from radians to degrees
			// reference angle is always positive; the Math.abs method removes the sign.
			thetaR = Math.abs( Math.toDegrees(Math.atan(tan)));
		}

		//based on the quadrant, reference angle will give us standard angle (theta)
		double theta;

		if (x >= 0 && y >= 0) {
			//quadrant I
			theta = thetaR;			
		} else if (x < 0 && y >= 0) {
			//quadrant II
			theta = 180 - thetaR;		
		} else if (x < 0 && y < 0) {
			//quadrant III
			theta = 180 + thetaR;		
		} else {
			//quadrant IV
			theta = 360 - thetaR;		
		}

		return theta;

	}

}
