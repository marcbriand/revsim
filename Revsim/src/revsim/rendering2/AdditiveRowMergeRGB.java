package revsim.rendering2;

import revsim.rendering.RenderUtil;

// assumes alpha is 255, sets output alpha to 255
public class AdditiveRowMergeRGB implements IRowMergeARGB {

	@Override
	public void merge(int[] oldRow, int[] newRow, int[] outputRow) {
		
		if (oldRow.length != newRow.length || newRow.length != outputRow.length)
			throw new IllegalArgumentException("lengths must be equal!");
		
		for (int i = 0; i < oldRow.length; i++) {
			int oldColor = oldRow[i];
			int newColor = newRow[i];
			
			int[] oldColors = RenderUtil.splitColor4(oldColor);
			int[] newColors = RenderUtil.splitColor4(newColor);
			
			int[] outputVal = new int[4];
			outputVal[0] = 255; // alpha
			
			for (int j = 1; j < 4; j++) {
				int o = oldColors[j];
				int n = newColors[j];
				int sum = o + n;
				sum = Math.max(0, sum);
				sum = Math.min(255, sum);
				outputVal[j] = sum;
			}
			
			outputRow[i] = RenderUtil.combineColor4(outputVal);
			
		}

	}

}
