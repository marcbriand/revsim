package revsim.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import revsim.config.lex.APathAcceptor;
import revsim.config.lex.AlphaNumAcceptor;
import revsim.config.lex.CharBlob;
import revsim.config.lex.ClassnameAcceptor;
import revsim.config.lex.DPathAcceptor;
import revsim.config.lex.LexException;
import revsim.config.lex.NumAcceptor;
import revsim.config.lex.RPathAcceptor;
import revsim.config.lex.Token;
import revsim.config.lex.TokenSource;
import revsim.config.lex.WhitespaceAcceptor;
import revsim.config.lex.revsim.BooleanAcceptor;
import revsim.config.lex.revsim.ControllerAcceptor;
import revsim.config.lex.revsim.CreateAcceptor;
import revsim.config.lex.revsim.EqualsAcceptor;
import revsim.config.lex.revsim.LBraceAcceptor;
import revsim.config.lex.revsim.LCurlyAcceptor;
import revsim.config.lex.revsim.LParenAcceptor;
import revsim.config.lex.revsim.MakeAcceptor;
import revsim.config.lex.revsim.MultiCommentAcceptor;
import revsim.config.lex.revsim.QStringAcceptor;
import revsim.config.lex.revsim.RBraceAcceptor;
import revsim.config.lex.revsim.RCurlyAcceptor;
import revsim.config.lex.revsim.RParenAcceptor;
import revsim.config.lex.revsim.RefAcceptor;
import revsim.config.lex.revsim.RegistryAcceptor;
import revsim.config.lex.revsim.SingleCommentAcceptor;
import revsim.config.lex.revsim.TemplateAcceptor;
import revsim.config.lex.revsim.ViewAcceptor;
import revsim.config.objects.ArrayObject;
import revsim.config.objects.ConfigObject;
import revsim.config.objects.DataObject;
import revsim.config.objects.TemplateObject;
import revsim.config.objects.revsim.RevsimConfig;
import revsim.config.parse.TokenIter;
import revsim.config.parse.revsim.ArrayAcceptor;
import revsim.config.parse.revsim.ConfigAcceptor;
import revsim.config.parse.revsim.DataAcceptor;
import revsim.config.parse.revsim.KeyedStatementAcceptor;
import revsim.config.parse.revsim.PrimitiveValueAcceptor;
import revsim.config.parse.revsim.ValueAcceptor;

public class ConfigParser {
	
	private static Map<String, String> registry;
	private static Map<String, TemplateObject> templateRegistry = new HashMap<String, TemplateObject>();
	
	private static void filteredAdd (Token tok, List<Token> tokens) {
		if (tok.types.contains("whitespace_t") ||
		    tok.types.contains("multicomment_t") ||
		    tok.types.contains("singlecomment_t")) {
			return;
		}
		tokens.add(tok);
	}

	public static List<Token> getTokens (File file) {
		InputStream istream = null;
		
		List<Token> tokens = new ArrayList<Token>();
		try {
			istream = new FileInputStream(file);
			List<String> lines = FileUtil.getLines(istream);
			List<CharBlob> blobs = CharBlob.makeBlobs(lines);
			TokenSource tokenSource = 
				new TokenSource(blobs, new RegistryAcceptor(),
						               new EqualsAcceptor(),
						               new LCurlyAcceptor(),
						               new RCurlyAcceptor(),
						               new AlphaNumAcceptor(),
						               new ClassnameAcceptor(),
						               new WhitespaceAcceptor(),
						               new NumAcceptor(),
						               new SingleCommentAcceptor(),
						               new MultiCommentAcceptor(),
						               new LParenAcceptor(),
						               new RParenAcceptor(),
						               new TemplateAcceptor(),
						               new MakeAcceptor(),
						               new DPathAcceptor(),
						               new APathAcceptor(),
						               new RPathAcceptor(),
						               new LBraceAcceptor(),
						               new RBraceAcceptor(),
						               new ControllerAcceptor(),
						               new ViewAcceptor(),
						               new CreateAcceptor(),
						               new RefAcceptor(),
						               new BooleanAcceptor(),
						               new QStringAcceptor()
						               );
			while (true) {
				Token tok = tokenSource.nextToken();
				if (tok == null) {
					break;
				}
				System.out.println(tok.value + " " + tok.getTypesStr());
				filteredAdd(tok, tokens);
			}
			return tokens;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (LexException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				istream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static ConfigAcceptor getConfigAcceptor (List<Token> tokens) {
		TokenIter tokenIter = new TokenIter(tokens);
		ConfigAcceptor configAcceptor = new ConfigAcceptor();
		boolean accepted = configAcceptor.accept(tokenIter);
		if (accepted) {
			System.out.println("ConfigAcceptor accepted");
		}
		else {
			System.out.println("ConfigAcceptor did not match");
		}
		return configAcceptor;
	}
	
	private static Map<String, String> makeRegistry (ValueAcceptor vacc) throws ConfigException {
		if (!vacc.getEntityTypes().contains("data_e")) {
			throw new ConfigException("registry definition right-hand side is not in proper format");
		}
		DataAcceptor dacc = (DataAcceptor)vacc.getAcceptorForEntity("data_e");
		List<KeyedStatementAcceptor> statements = dacc.getStatements();
		Map<String, String> ret = new HashMap<String, String>();
		for (KeyedStatementAcceptor acc : statements) {
			String key = acc.getKey();
			ValueAcceptor vacc2 = acc.getValueAcceptor();
			if (!vacc2.getEntityTypes().contains("primitive_e")) {
				ConfigException e = new ConfigException("registry statement rhs is not a classname");
				throw vacc2.addLineInfo(e);
			}
			PrimitiveValueAcceptor pvacc = (PrimitiveValueAcceptor)vacc2.getAcceptorForEntity("primitive_e");
			List<Token> tokens = pvacc.getTokens();
			if (tokens == null || tokens.isEmpty()) {
				ConfigException e = new ConfigException("registry statement rhs is not a classname");
				throw pvacc.addLineInfo(e);				
			}
			Token tok = tokens.get(0);
			if (tok.getTypesStr().indexOf("classname_t") < 0) {
				ConfigException e = new ConfigException("registry statement rhs is not a classname");
				throw pvacc.addLineInfo(e);								
			}
			ret.put(key, tok.value);
		}
		return ret;
	}
	
	private static TemplateObject createTemplateObject (ValueAcceptor vacc, ConfigObject parent) 
	    throws ConfigException {
		revsim.config.parse.revsim.TemplateAcceptor tacc =
			(revsim.config.parse.revsim.TemplateAcceptor) vacc.getAcceptorForEntity("template_e");
		String alias = tacc.getAlias();
		String classname = registry.get(alias);
		if (classname == null) {
			throw vacc.addLineInfo(new ConfigException("no registry entry found for alias " + alias));
		}
		try {
			Class kls = Class.forName(classname);
			Object obj = kls.newInstance();
			if (!(obj instanceof ConfigObject)) {
				throw tacc.addLineInfo(new ConfigException("the class referenced by alias " + alias + " is not a ConfigObject"));
			}
			ConfigObject cobj = (ConfigObject)obj;
			cobj.setParent(parent);
			cobj.setRoot(parent.getRoot());
			DataAcceptor dacc = tacc.getDataAcceptor();
			List<KeyedStatementAcceptor> statements = dacc.getStatements();
			for (KeyedStatementAcceptor ksa : statements) {
				setProperty(cobj, ksa);
			}
			TemplateObject tobj = new TemplateObject();
			tobj.setParent(parent);
			tobj.setRoot(parent.getRoot());
			tobj.setObject(cobj);
			return tobj;
		} 
		catch (ClassNotFoundException e) {
			throw vacc.addLineInfo(e);
		}
		catch (InstantiationException e) {
			throw vacc.addLineInfo(e);
		}
		catch (IllegalAccessException e) {
			throw vacc.addLineInfo(e);
		}
	}
	
	private static ConfigObject makeObjectFromTemplate (ValueAcceptor vacc, ConfigObject parent) 
	   throws ConfigException {
		
		revsim.config.parse.revsim.MakeAcceptor macc =
			(revsim.config.parse.revsim.MakeAcceptor)vacc.getAcceptorForEntity("make_e");
		String tpath = macc.getTemplatePath();
/*
		Object obj = parent.get(tpath);
		if (obj == null || !(obj instanceof TemplateObject)) {
			throw macc.addLineInfo(new ConfigException("no template found at path " + tpath));
		}
		TemplateObject tobj = (TemplateObject)obj;
*/
		if (!templateRegistry.containsKey(tpath)) {
			throw macc.addLineInfo(new ConfigException("no template named " + tpath + " was found"));
		}
		TemplateObject tobj = templateRegistry.get(tpath);
		tobj = (TemplateObject)tobj.duplicate();
		ConfigObject newObj = tobj.getObject();
		newObj.setParent(parent);
		newObj.setRoot(parent.getRoot());
		DataAcceptor dacc = macc.getDataAcceptor();
		for (KeyedStatementAcceptor ksa : dacc.getStatements()) {
			setProperty(newObj, ksa);
		}
		return newObj;		
	}
	
	private static ConfigObject createObjectFromCreate (ValueAcceptor vacc, ConfigObject parent) 
	   throws ConfigException {
		
		revsim.config.parse.revsim.CreateAcceptor cacc =
			(revsim.config.parse.revsim.CreateAcceptor)vacc.getAcceptorForEntity("create_e");
		String alias = cacc.getAlias();
		String classname = registry.get(alias);
		if (classname == null) {
			throw vacc.addLineInfo(new ConfigException("no entry found in classname registry for alias " + alias));
		}
		try {
			Class kls = Class.forName(classname);
			Object obj = kls.newInstance();
			if (!(obj instanceof ConfigObject)) {
				throw vacc.addLineInfo(new ConfigException("the class referenced by alias " + alias + " is not a ConfigObject"));
			}
			ConfigObject cobj = (ConfigObject)obj;
			DataAcceptor dacc = cacc.getDataAcceptor();
			List<KeyedStatementAcceptor> statements = dacc.getStatements();
			for (KeyedStatementAcceptor ksa : statements) {
				setProperty(cobj, ksa);
			}
			cobj.setParent(parent);
			cobj.setRoot(parent.getRoot());
			return cobj;
		} 
		catch (ClassNotFoundException e) {
			throw vacc.addLineInfo(e);
		}
		catch (InstantiationException e) {
			Exception ex = new Exception("InstantiationException on " + e.getMessage());
			throw vacc.addLineInfo(ex);
		}
		catch (IllegalAccessException e) {
			throw vacc.addLineInfo(e);
		}
		
		
	}
	
	private static Object makePrimitive (ValueAcceptor vacc) {
		
		PrimitiveValueAcceptor pacc = (PrimitiveValueAcceptor)vacc.getAcceptorForEntity("primitive_e");
		Token tok = pacc.getTokens().get(0);
		if (tok.types.contains("num_t")) {
			return Float.parseFloat(tok.value);
		}
		if (tok.types.contains("qstr_t")) {
			return tok.value.substring(1, tok.value.length()-1);
		}
		if (tok.types.contains("bool_t")) {
			return Boolean.parseBoolean(tok.value);
		}
		return tok.value;
		
	}
	
	private static ConfigObject createDataObject (ValueAcceptor vacc, ConfigObject parent) 
	    throws ConfigException {
		DataAcceptor dacc = (DataAcceptor)vacc.getAcceptorForEntity("data_e");
		DataObject ret = new DataObject();
		ret.setParent(parent);
		ret.setRoot(parent.getRoot());
		for (KeyedStatementAcceptor ksa : dacc.getStatements()) {
			setProperty(ret, ksa);
		}
		return ret;
	}
	
	private static ConfigObject createArrayObject (ValueAcceptor vacc, ConfigObject parent) 
	   throws ConfigException {
		ArrayObject ret = new ArrayObject();
		ArrayAcceptor aacc = (ArrayAcceptor)vacc.getAcceptorForEntity("array_e");
		List<ValueAcceptor> vaccs = aacc.getValueAcceptors();
		ret.setParent(parent);
		ret.setRoot(parent.getRoot());
		for (ValueAcceptor v : vaccs) {
			ret.addObject(createValueObject(v, ret, null));
		}
		return ret;
	}
	
	private static Object createValueObject (ValueAcceptor vacc, ConfigObject parent, String key)
	   throws ConfigException {
		
		if (vacc.getEntityTypes().contains("template_e")) {
			if (vacc.getLevel() != 2) {
				throw new ConfigException("templates may only be created at the top level");
			}
			if (key == null) {
				throw new ConfigException("template must be assigned to a name");
			}
			if (templateRegistry.containsKey(key)) {
				throw new ConfigException("duplicate definition of template " + key);
			}
			TemplateObject ret = createTemplateObject(vacc, parent);
			templateRegistry.put(key, ret);
			return ret;
		}
		if (vacc.getEntityTypes().contains("primitive_e")) {
		    return makePrimitive(vacc);	
		}
		if (vacc.getEntityTypes().contains("make_e")) {
			return makeObjectFromTemplate(vacc, parent);
		}
		if (vacc.getEntityTypes().contains("create_e")) {
			return createObjectFromCreate(vacc, parent);
		}
		if (vacc.getEntityTypes().contains("data_e")) {
			return createDataObject(vacc, parent);
		}
		if (vacc.getEntityTypes().contains("array_e")) {
			return createArrayObject(vacc, parent);
		}
		return null;
	}
	
	private static void setProperty (ConfigObject obj, KeyedStatementAcceptor ksa)
	   throws ConfigException {
		
		ValueAcceptor vacc = ksa.getValueAcceptor();
		Object value = createValueObject(vacc, obj, ksa.getKey());
		obj.set(ksa.getKey(), value);
	}
	
	public static RevsimConfig getConfigObject (ConfigAcceptor configAcceptor) 
	    throws ConfigException {
		
		RevsimConfig ret = new RevsimConfig();
		ret.setRoot(ret);
		
		for (KeyedStatementAcceptor ksa : configAcceptor.getStatements()) {
			try {
			    if ("registry".equals(ksa.getKey())) {
			    	if (registry != null) {
			    		throw ksa.addLineInfo(new ConfigException("registry has already been created"));
			    	}
			    	
			        registry = makeRegistry(ksa.getValueAcceptor());
			    }
			    else {
			    	System.out.println("setting property for key " + ksa.getKey());
			    	setProperty(ret, ksa);
			    }
			}
			catch (ConfigException ex) {
				throw ksa.addLineInfo(ex);
			}
	
		}
		
		return ret;
	}
}
