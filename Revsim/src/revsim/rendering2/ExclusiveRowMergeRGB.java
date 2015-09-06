package revsim.rendering2;

import revsim.rendering.RenderUtil;

public class ExclusiveRowMergeRGB implements IRowMergeARGB {

	@Override
	public void merge(int[] oldRow, int[] newRow, int[] outputRow) {

		if (oldRow.length != newRow.length || newRow.length != outputRow.length)
			throw new IllegalArgumentException("lengths must be equal!");
		
		for (int i = 0; i < oldRow.length; i++) {
			int oldColor = oldRow[i];
			if (oldColor > 0)
				continue;
			outputRow[i] = newRow[i];
		}

	}

}
