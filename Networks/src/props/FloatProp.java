package props;

public class FloatProp implements DProp {
	
	public float value = 0;
	
	public FloatProp(float value) {
		this.value = value;
	}

	@Override
	public DProp duplicate() {
		return new FloatProp(value);
	}

}
