package revsim.config.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import revsim.config.ConfigException;
import revsim.config.Util;

public abstract class ConfigObject {
	
	protected ConfigObject parent;
	protected ConfigObject root;
	
	protected final Map<String, ConfigObject> children = new HashMap<String, ConfigObject>();
	
	public Object getLocal (String key) {
		return getLocalR(key);
	}
	public void setLocal (String key, Object obj) throws ConfigException {
		setLocalR(key, obj);
	}
	public abstract ConfigObject duplicate ();
	public void setParent (ConfigObject parent) {
		this.parent = parent;
	}
	public void setRoot (ConfigObject root) {
		this.root = root;
	}
	public ConfigObject getRoot () {
		return root;
	}
	
	public Object get (String path) {
		if (Util.isAbsolute(path)) {
			String newPath = Util.trimOffAbsolute(path);
			return root.get(newPath);
		}
		if (Util.isUp(path)) {
			String newPath = Util.trimOffUp(path);
			return parent.get(newPath);
		}
		StringBuilder remainder = new StringBuilder();
		String childName = Util.splitOnFirstNode(path, remainder);
		if (remainder.length() == 0) {
			return getLocal(childName);
		}
		ConfigObject child = children.get(childName);
		if (child == null) {
			return null;
		}
		return child.get(remainder.toString());
	}
	
	public void set (String path, Object obj) throws ConfigException {
		if (Util.isAbsolute(path)) {
			String newPath = Util.trimOffAbsolute(path);
			root.set(newPath, obj);
			return;
		}
		if (Util.isUp(path)) {
			String newPath = Util.trimOffUp(path);
			parent.set(newPath, obj);
			return;
		}
		StringBuilder remainder = new StringBuilder();
		String childName = Util.splitOnFirstNode(path, remainder);
		if (remainder.length() == 0) {
			this.setLocal(childName, obj);
			return;
		}
		ConfigObject child = children.get(childName);
		if (child == null) {
			Object maybe = this.getLocal(childName);
			if (maybe == null || !(maybe instanceof ConfigObject)) {
			   throw new ConfigException("could not set object for path " + path + "; no child node named " + childName);
			}
			ConfigObject cmaybe = (ConfigObject)maybe;
			cmaybe.set(remainder.toString(), obj);
			return;
		}
		child.set(remainder.toString(), obj);
	}
	
	public List<Method> getGetters () {
		List<Method> ret = new ArrayList<Method>();
		Method[] methods = this.getClass().getMethods();
		for (Method meth : methods) {
			if (meth.getName().startsWith("get")) {
				String remainder = meth.getName().substring(3);
				if (remainder.isEmpty()) {
					continue;
				}
				if (remainder.equals("Local")) {
					continue;
				}
				if (remainder.equals("Root")) {
					continue;
				}
				if (remainder.equals("Class")) {
					continue;
				}
				if (remainder.equals("Getters")) {
					continue;
				}
				if (remainder.equals("Setters")) {
					continue;
				}
				if (meth.getParameterTypes().length > 0) {
					continue;
				}
				ret.add(meth);
			}
		}
		return ret;
	}
	
	public List<Method> getSetters () {
		List<Method> ret = new ArrayList<Method>();
		Method[] methods = this.getClass().getMethods();
		for (Method meth : methods) {
			if (meth.getName().startsWith("set")) {
				String remainder = meth.getName().substring(3);
				if (remainder.isEmpty()) {
					continue;
				}
				if (remainder.equals("Local")) {
					continue;
				}
				if (remainder.equals("Root")) {
					continue;
				}
				if (remainder.equals("Class")) {
					continue;
				}
				if (remainder.equals("Parent")) {
					continue;
				}
				if (meth.getParameterTypes().length != 1) {
					continue;
				}
			ret.add(meth);
			}
		}
		return ret;
	}
	
	private String makeSetMethodName (String key) {
		String firstChar = key.substring(0, 1);
		return "set" + firstChar.toUpperCase() + key.substring(1);
	}
	
	private String makeGetMethodName (String key) {
		String firstChar = key.substring(0, 1);
		return "get" + firstChar.toUpperCase() + key.substring(1);
	}
	
	private boolean matchesSetter (Method method, Object obj) {
		Class[] types = method.getParameterTypes();
		if (types.length != 1) {
			return false;
		}
		String typeName = types[0].getName();
		if (typeName.equals(obj.getClass().getName())) {
			return true;
		}
		if (types[0].isAssignableFrom(obj.getClass())) {
			return true;
		}
		if (obj.getClass().getName().endsWith("Boolean")) {
			return typeName.equals("boolean");
		}
		if (obj.getClass().getName().endsWith("Float")) {
			return typeName.equals("long") ||
				   typeName.equals("float");
		}
		return false;
	}
	
	private Method findMatchingSetter (String methName, Object obj) {
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methName)) {
				if (matchesSetter(method, obj)) {
					return method;
				}
			}
		}
		return null;
	}
	
	private Method findMatchingGetter (String methName) {
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methName)) {
				if (method.getParameterTypes().length == 0) {
					return method;
				}
			}
		}
		return null;
	}
	
	private boolean doFloatToLong (Method method, Object obj) {
		Class[] params = method.getParameterTypes();
		if (params.length == 0) {
			return false;
		}
		if (params[0].getName().equals("long")) {
			if (obj.getClass().getName().equals("java.lang.Float")) {
				return true;
			}
		}
		return false;
	}
	
	private boolean doLongToFloat (Method method) {
		Class retType = method.getReturnType();
		if (retType.getName().equals("long") || retType.getName().equals("java.lang.Long")) {
			return true;
		}
		return false;
	}
	
	public void setLocalR (String key, Object obj) throws ConfigException {
		String methName = makeSetMethodName(key);
		Method method = findMatchingSetter(methName, obj);
		if (method == null) {
			throw new ConfigException("no setter found for property '" + key + "'");
		}
		try {
			if (doFloatToLong(method, obj)) {
			    Float fobj = (Float)obj;				
			    method.invoke(this, fobj.longValue());
			}
			else {
				method.invoke(this,  obj);
			}
		} 
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ConfigException("IllegalAccessException: " + e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ConfigException("IllegalArgumentException: " + e.getMessage());
		} 
		catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new ConfigException("InvocationTargetException: " + e.getMessage());
		}		
	}
	
	public Object getLocalR (String key) {
		String methName = makeGetMethodName(key);
		Method method = findMatchingGetter(methName);
		if (method == null) {
			return null;
		}
		try {
			Object ob = method.invoke(this);
			if (doLongToFloat(method)) {
				Long l = (Long)ob;
				return new Float(l);
			}
			return ob;
		} 
		catch (IllegalAccessException e) {
 		    e.printStackTrace();
			return null;
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} 
		catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected int getFloatAsInt(String name, Object obj) throws ConfigException {
		if (obj == null)
			throw new ConfigException("null value passed for int '" + name + "'");
		if (obj instanceof Float) {
			Float fobj = (Float)obj;
			return fobj.intValue();
		}
		else
			throw new ConfigException("'" + name + "' was not a number");
		
	}


}
