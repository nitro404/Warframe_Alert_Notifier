package utilities;

import java.awt.*;

public class Utilities {
	
	private Utilities() { }
	
	public static Point parsePoint(String data) {
		if(data == null) { return null; }
		
		String[] pointData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(pointData.length != 2) { return null; }
		
		int x, y;
		try { x = Integer.parseInt(pointData[0]); } catch(NumberFormatException e) { return null; }
		try { y = Integer.parseInt(pointData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Point(x, y);
	}
	
	public static Dimension parseDimension(String data) {
		if(data == null) { return null; }
		
		String[] dimensionData = data.replaceAll("^[^-0-9]+", "").replaceAll("[^-0-9]+$", "").split("[^-0-9]+");
		if(dimensionData.length != 2) { return null; }
		
		int width, height;
		try { width = Integer.parseInt(dimensionData[0]); } catch(NumberFormatException e) { return null; }
		try { height = Integer.parseInt(dimensionData[1]); } catch(NumberFormatException e) { return null; }
		
		return new Dimension(width, height);
	}
	
	public static Color parseColour(String data) {
		if(data == null) { return null; }
		
		String[] colourData = data.replaceAll("^[^-0-9.]+", "").replaceAll("[^-0-9.]+$", "").split("[^-0-9.]+");
		if(colourData.length != 3) { return null; }
		
		boolean normalized = false;
		for(int i=0;i<colourData.length;i++) {
			if(colourData[i].contains(".")) {
				normalized = true;
			}
		}
		
		if(normalized) {
			float[] colours = new float[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Float.parseFloat(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0.0f || colours[i] > 1.0f) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
		else {
			int[] colours = new int[3];
			for(int i=0;i<colourData.length;i++) {
				try { colours[i] = Integer.parseInt(colourData[i]); } catch(NumberFormatException e) { return null; }
				if(colours[i] < 0 || colours[i] > 255) { return null; }
			}
			
			return new Color(colours[0], colours[1], colours[2]);
		}
	}
	
	public static String getExtension(String fileName) {
		if(fileName.lastIndexOf('.') != -1) {
			return fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
		}
		else {
			return null;
		}
	}
	
	public static boolean hasExtension(String fileName, String fileExtension) {
		String actualFileExtension = getExtension(fileName);
		return actualFileExtension != null && actualFileExtension.equalsIgnoreCase(fileExtension);
	}
	
	public static String removeExtension(String fileName) {
		if(fileName.lastIndexOf('.') != -1) {
			return fileName.substring(0, fileName.lastIndexOf('.'));
		}
		else {
			return fileName;
		}
	}
	
}
