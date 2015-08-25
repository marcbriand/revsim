package revsim.config.parse.revsim;

import java.util.Arrays;
import java.util.List;

import revsim.config.lex.Token;
import revsim.config.parse.Acceptor;
import revsim.config.parse.GUAcceptor;
import revsim.config.parse.TokenIter;

public class ValueAcceptor extends GUAcceptor {

    public ValueAcceptor () {
    	super(Arrays.asList(new TemplateAcceptor(),
    			            new MakeAcceptor(),
    			            new CreateAcceptor(),
    			            new RefAcceptor(),
    			            new DataAcceptor(),
    			            new ArrayAcceptor(),
    			            new PrimitiveValueAcceptor()),
    			            "template_e",
    			            "make_e",
    			            "create_e",
    			            "ref_e",
    			            "data_e",
    			            "array_e",
    			            "primitive_e");
    		  
    }
	
	@Override
	public Acceptor factory() {
		return new ValueAcceptor();
	}

}
