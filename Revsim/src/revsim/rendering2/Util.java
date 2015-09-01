package revsim.rendering2;

public class Util {

	public static class ByteAndBitpos {
		public long byt;
		public short bitpos; // 7 == MSB
		
		public ByteAndBitpos(long byt, short bitpos) {
			this.byt = byt;
			this.bitpos = bitpos;
		}
	}
	
	public static ByteAndBitpos rowOffsetToByteAndBitpos(long offset) {
		long byt = offset/8;
		long col = offset - byt*8;
		long bitpos = 7 - col;
		return new ByteAndBitpos(byt, (short)bitpos);
	}
	
	public static double sqrt2 = Math.sqrt(2.0);
	
	public static class RenderRegion {
		public int ulx;
		public int uly;
		public int lrx;
		public int lry;
		
		public RenderRegion(int ulx, int uly, int lrx, int lry) {
			this.ulx = ulx;
			this.uly = uly;
			this.lrx = lrx;
			this.lry = lry;
		}
	}
}
