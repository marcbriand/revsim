package revsim.modeltools.functions;

import revsim.config.objects.ConfigObject;

public abstract class AbstractFunction extends ConfigObject implements Function {
	
	protected long offset;
	protected boolean absolute;
	
	protected void endow (AbstractFunction fun) {
		fun.offset = offset;
		fun.absolute = absolute;
	}
	
	public boolean isAbsolute() {
		return absolute;
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	public long getOffset () {
		return offset;
	}
	
	public void setOffset (long offset) {
		this.offset = offset;
	}
	
}
