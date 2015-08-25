package props;

public class IntProp implements DProp {
	
	public int value;
	
	public IntProp(int value) {
		this.value = value;
	}

	@Override
	public DProp duplicate() {
		return new IntProp(value);
	}

}
