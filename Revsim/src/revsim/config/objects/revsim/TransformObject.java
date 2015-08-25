package revsim.config.objects.revsim;

import java.util.ArrayList;
import java.util.List;

import revsim.config.ConfigException;
import revsim.config.Util;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;

public class TransformObject extends ConfigObject {
	
	private int srcDepth;
	private List<Integer> srcPath = new ArrayList<Integer>();
	private TransformSourceOption srcOption = TransformSourceOption.Keep;
	private Transform operation;
	private List<Integer> destPath = new ArrayList<Integer>();
    private TransformDestOption destOption = TransformDestOption.Add;
	
	public TransformDestOption getDestOption() {
		return destOption;
	}

	public void setDestOption(TransformDestOption destOption) {
		this.destOption = destOption;
	}

	public List<Integer> getDestPath() {
		return destPath;
	}

	public void setDestPath(List<Integer> destPath) {
		this.destPath = destPath;
	}

	public Transform getOperation() {
		return operation;
	}

	public void setOperation(Transform operation) {
		this.operation = operation;
	}

	public int getSrcDepth() {
		return srcDepth;
	}

	public void setSrcDepth(int srcDepth) {
		this.srcDepth = srcDepth;
	}

	public TransformSourceOption getSrcOption() {
		return srcOption;
	}

	public void setSrcOption(TransformSourceOption srcOption) {
		this.srcOption = srcOption;
	}

	public List<Integer> getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(List<Integer> srcPath) {
		this.srcPath = srcPath;
	}

	@Override
	public ConfigObject duplicate() {
		TransformObject ret = new TransformObject();
		ret.srcDepth = srcDepth;
		ret.srcPath.addAll(srcPath);
		ret.srcOption = srcOption;
		ret.operation = operation;
		ret.destPath.addAll(destPath);
		ret.destOption = destOption;
		return ret;
	}

	@Override
	public Object getLocal(String key) {
		if ("srcDepth".equals(key)) {
			return new Integer(srcDepth);
		}
		if ("srcPath".equals(key)) {
			List<Integer> retPath = new ArrayList<Integer>();
			retPath.addAll(srcPath);
			return retPath;
		}
		if ("srcOption".equals(key)) {
			return srcOption;
		}
		if ("op".equals(key)) {
			return operation;
		}
		if ("destPath".equals(key)) {
			List<Integer> retPath = new ArrayList<Integer>();
			retPath.addAll(destPath);
			return retPath;
		}
		if ("destOption".equals(key)) {
			return destOption;
		}
		return null;
	}
	
	private List<Integer> convertPath (Object o) throws ConfigException {
		List<Integer> ret = new ArrayList<Integer>();
		if (!(o instanceof ArrayObject)) {
			throw new ConfigException("path must be an array of int");
		}
		ArrayObject pathArray = (ArrayObject)o;
		List<Object> els = pathArray.getObjects();
		for (Object e : els) {
			if (!(e instanceof Float)) {
				throw new ConfigException("path element must be an int");
			}
			int i = Util.castNum(e).intValue();
			ret.add(i);
		}
		return ret;
	}

	@Override
	public void setLocal(String key, Object obj) throws ConfigException {
		if ("srcDepth".equals(key)) {
			int sl = Util.castNum(obj).intValue();
			if (sl < 0) {
				throw new ConfigException("srcDepth cannot be negative");
			}
			srcDepth = sl;
			return;
		}
		if ("srcPath".equals(key)) {
			srcPath = convertPath(obj);
			return;
		}
		if ("srcOption".equals(key)) {
			String op = (String)obj;
			if ("keep".equals(op)) {
				srcOption = TransformSourceOption.Keep;
				return;
			}
			if ("remove".equals(op)) {
				srcOption = TransformSourceOption.Remove;
				return;
			}
			throw new ConfigException("unknown source option " + op);
		}
		if ("op".equals(key)) {
			if (!(obj instanceof Transform)) {
				throw new ConfigException("op must be an object that implements Transform");
			}
			operation = (Transform)obj;
			return;
		}
		if ("destPath".equals(key)) {
			destPath = convertPath(obj);
			return;
		}
		if ("destOption".equals(key)) {
			String op = (String)obj;
			if ("merge".equals(op)) {
				destOption = TransformDestOption.Merge;
				return;
			}
			if ("add".equals(op)) {
				destOption = TransformDestOption.Add;
				return;
			}
			throw new ConfigException("unknown dest option " + op);
		}
		throw new ConfigException("unknown transform attribute '" + key + "'");

	}
	

}
