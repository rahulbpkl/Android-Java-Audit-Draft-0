 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
 
public class Main {
 
	public static void main(String[] args) throws IOException {
		//**************************Read Java File***************************************
				String str;
				BufferedReader br = new BufferedReader(new FileReader("Vector.java"));
				try {
				    StringBuilder sb = new StringBuilder();
				    String line = br.readLine();
				    while (line != null) {
				        sb.append(line);
				        sb.append(System.lineSeparator());
				        line = br.readLine();
				    }
				   str = sb.toString();
				} finally {
				    br.close();
				}
		//*******************************************************************************
 
				//*****************Compilation Unit**********************************************
				   
				ASTParser parser = ASTParser.newParser(AST.JLS3);
			    parser.setSource(str.toCharArray());
				parser.setKind(ASTParser.K_COMPILATION_UNIT);
			    CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		//*******************************************************************************
 
			   // System.out.println("Class declaration found");
		//TypeFinderVisitor v = new TypeFinderVisitor();
		cu.accept(new MyVisitor(cu,str));		
	}
}
 
class MyVisitor extends ASTVisitor{
	CompilationUnit cu;
	String source;
	String Classname;
	int LineNo;
	public MyVisitor(CompilationUnit cu, String source) throws IOException {
		super();
		this.cu = cu;
		this.source = source;
		// System.out.println("here");
		/* List types = cu.types();    
		 TypeDeclaration typeDec = (TypeDeclaration) types.get(1); 
		 //typeDec value become class
		 this.Classname=typeDec.getName().toString();*/
			//System.out.println("className:" + typeDec.getName());
			
			
			 
	}
	//***********************It is for getting class name********
	public boolean visit(TypeDeclaration node) {
	// It is useful if multiple classes are present in a single .java file			
		SimpleName name=node.getName(); // Gives class name of current visiting node
		this.Classname=name.toString(); // Saving to a global variable for many other function
		this.LineNo=cu.getLineNumber(name.getStartPosition());// Line number saving to a global variable. It is useful for giving error message
		try {
			ruleDcl01(); // Calling the method for DCL01-J. Currently added as a method. Later it may include this block.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	//*************************Block Ends Here*************************************
	//***********************Prevent class initialization cycles(IntraClass)*********
	
	public boolean visit(FieldDeclaration node) {
		 Type t=node.getType();
//********************************* Constructor invocation node position checking  ********************	
// ******** To implement DCL001-J IntraClass allow constructor after all variable declaration**********
		 if(t.toString().equals(this.Classname))
			{
				 
				System.out.println("Class object creation found at line "+cu.getLineNumber(t.getStartPosition()));
				/*Here some code needed to check this FieldDeclaration node (Constructor invoked) is the right most
 				FieldDeclaration node if yes no code violation */
			}
			 
			return false; // do not continue to avoid usage info
		}
	public void ruleDcl01() throws IOException{
		//*********************Read Class List*******************************************
				Path filePath = new File("javaclass").toPath();
				   Charset charset = Charset.defaultCharset();        
				   List<String> stringList = Files.readAllLines(filePath, charset);
				   String[] stringArray = stringList.toArray(new String[]{});
				   int retVal =0;
		//*******************************************************************************
		//***********************Do not reuse public identifiers from the Java Standard Library(Detection)*********
		//********************************* Comparing Class name with Java Standard Class names********************		
				String searchVal = this.Classname; // typeDec.getName() gives the class name
			    retVal=Arrays.binarySearch(stringArray,searchVal); // binary search using java standard method. If found it will return the index(index>=0)
				if(retVal>=0)//If index greater than or equals zero match found. ie class name is same as a java standard class name. So a warning message for programmer.
				{
				System.out.println("DCL01-J. Do not reuse public identifiers from the Java Standard Library:  Error at line "+LineNo+" "+". '"+searchVal+"' is a built-in Java Class name. Please use different one ");
			    }
		//*********************************************************************************************************
		//*********************************************************************************************************
			}
	}
