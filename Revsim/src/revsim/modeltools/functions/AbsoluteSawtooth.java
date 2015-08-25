package revsim.modeltools.functions;

public class AbsoluteSawtooth extends Sawtooth {

	@Override
	public float exec(long frame, long birthday) {
		return super.exec(frame, 0);
	}
}
