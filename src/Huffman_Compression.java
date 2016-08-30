import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Huffman_Compression {

    private static int ascii = 256;// Global to accommodate all ascii chars
   
    private static class Node implements Comparable<Node> {
       public final int frequency;
       private boolean visited;
       private final char letter;
       private final Node right, left;
       private Node parent;
      
       Node(int frequency, boolean visited, char letter, Node right, Node left, Node parent) {
           this.frequency = frequency;
           this.visited = visited;
           this.letter = letter;
           this.right = right;
           this.left = left;
           this.parent = parent;
       }
       
       public int compareTo(Node other) {
           return this.frequency - other.frequency;
       }
       
       private boolean isLeaf(){
           return(left == null && right == null);               
       }
    }
    
    // Generates Frequency Table and calls the remaining processes
    private static int[] freq(String input){
       int table[] = new int[ascii];
       char[] chars = input.toCharArray();
       String output = "";
       
      // Determining frequency of each character
       for (int i = 0; i < chars.length; i++){
           table[chars[i]]++;
       }
       
       // Printing out the Frequency Table
       System.out.println("\nFrequency Table: ");
       for (int i = 0; i < chars.length; i++) {
    	   if (table[chars[i]] > 0) {
    		   System.out.println("     " + (char)chars[i] + "     -     " + table[chars[i]]);
    	   }
       }
       System.out.println();
      
       Node root = createBTree(table); // send frequency table to createBTree and return the root node of tree
      
       String[] encoding = new String[ascii];
       encodingPostorder(encoding, root); // 
       
       output = printEncodedString(encoding, input);
       
       decoder(root, output);
      
       return table;
    }
    
    // Takes originally inputed string and compares it to the encoded table. When an original character matches
    // a character in the encoded table, the corresponding "1" and "0" string is appended to output 
    private static String printEncodedString(String[] encodedTable, String origInput) {
    	String output = "";
    	
    	// Does the compare against the encoded table
    	for (int i = 0; i < origInput.length(); i++) {
    		for (int j = 0; j < ascii; j++) {
    			if ((origInput.charAt(i)) == (char)j) {
    				output += encodedTable[j];
    			}
    		}
    	}
    	    	
    	System.out.println("The compressed output string is :  " + output);
    	
    	return output;
    	
    }
    
    // Takes the original post order binary tree and the output string of ones and zeros and traverses the tree
    // one node at a time until a leaf is found. The left child will be traveled to if a zero is encountered and 
    // the right child will be traveled to if a one is encountered. Each time a leaf is discovered, the letter value
    // of that node is appended to decodedStr and the loop starts again from the root node and the next character in
    // the output string
    private static void decoder(Node root, String output) {
    	Node current = new Node(root.frequency, root.visited, root.letter, root.right, root.left, root.parent);
    	int i = 0;
    	String decodedStr = "";
    	
    	while (i < output.length()) {
	    	while (!current.isLeaf() && i < output.length()) {
	    		
	    		if (output.charAt(i) == '0') {
	    			current = current.left;
	    			i++;
	    		}
	    		else {
	    			current = current.right;
	    			i++;
	    		}
	    		if (current.isLeaf()) {
	    			decodedStr += String.valueOf(current.letter);
	    			current = root;
	    		}
	    	}
    	}
    	
    	System.out.print("\nThe decoded string is:  " + decodedStr);
    }
    
    // This is a post order non-recursive traversal of a binary tree. The order of left, right, root is observed. 
    // At each root a string of ones and zeros is added to an encoding table that contains the associated character
    // of that node. This method also will print out the Encoding Table once it has been created.
    private static void encodingPostorder(String[] table, Node root){
    	String append = "";
    	Node current = new Node(root.frequency, root.visited, root.letter, root.right, root.left, root.parent);
    	
    	// traversing b tree in post order non-recursive
    	do {
    		current = root;
    		while(!current.isLeaf() && !current.visited){
    			if(current.left != null && !current.left.visited){
    				append += "0";
    				current.left.parent = current;
    				current = current.left;
    				root = current.parent;
    				if(current.isLeaf()){
    					table[(int)current.letter] = append;
    					current.visited = true;
    					append = append.substring(0, append.length()-1); // When going back up to the root, the last character
    				}													 // of the append string must be removed
    			}
    			else if (current.right != null && !current.right.visited){
    				append += "1";
    				current.right.parent = current;
    				current = current.right;
    				root = current.parent;
    				if(current.isLeaf()){
        				table[(int)current.letter] = append;
        				current.visited = true;
        				append = append.substring(0, append.length()-1);
        			}
    			}
    			else {
    				root = current.parent;
    				table[(int)current.letter] = append;
    				current.visited = true;
    				if(append.length() > 0)
    					append = append.substring(0, append.length()-1);
    			}
    		}
    		    		
    	}while (current.parent != null);
    	
    	// Prints out the Encoding Table
    	System.out.println("Encoding Table:");
    	for (int i = 1; i < table.length; i++){
            if(table[i] != null){
            System.out.println("    "+(char)i+"    -     "+table[i]);
            }
        }
    	System.out.println();
    }

        
    // Sort array of nodes from smallest to largest
    public static void sort(Node[] root, int size){ 
    	// Insertion sort method
    	for(int i = 1; i < size; i++){
        	int key = root[i].frequency;
        	Node keyRoot = new Node(root[i].frequency, root[i].visited, root[i].letter, root[i].right, root[i].left, root[i].parent);
        	int k = i - 1;
        	while(k >= 0 && root[k].frequency > key){
        		root[k+1] = root[k];
        		k--;
        	}
        	root[k+1] = keyRoot;
        }
    }
  
    // Uses the frequency table to build a binary tree
    private static Node createBTree(int[] table) {
        int arraySize = 0;
        Node root = null;
        Node[] forest = new Node[ascii];
        // Create all the single trees
        for(int i = 0; i < ascii; i++){
            if (table[i] > 0){
            Node single = new Node (table[i], false, (char)i, null, null, null);
            forest[arraySize] = single; // assign each new single node to the array of nodes, forest[]
            arraySize++; // Counter 
            }
        }
        
        // Sort forest array of Nodes from smallest to largest
        sort(forest, arraySize);
        
        //Building B Tree with two smallest nodes
        while(arraySize > 1){
            // create new node with the 2 smallest frequency nodes and assign them to left and right of new node
            root = new Node ((forest[0].frequency + forest[1].frequency), false, '\0', forest[1], forest[0], null);
            root.left.parent = root; // assigning the new parent to it's children
            root.right.parent = root; // assigning the new parent to it's children
            forest[0] = root; //assign new node back to forest at beginning of array, which also deletes forest[0]
                                    
            // Moving all remaining nodes left one position to eliminate forest[1]
            int i = 1; // counter 
            int j = arraySize; // counter
            while(j > 2){
            	forest[i] = forest[i+1];
            	i++;
            	j--;
            }
            --arraySize; // decrement arraySize each iteration
            sort(forest, arraySize);// resort array
            
        }
             
        return root;
    }
 
    public static void main(String[] args) {
        InputStreamReader istream = new InputStreamReader(System.in) ;
        BufferedReader bufRead = new BufferedReader(istream) ;
        
        try {
            System.out.println("Please enter a string without spaces to be compressed.");
            String input = bufRead.readLine();
            freq(input); // Calls freq() with the original input to start processing the compression algorithm
        }
        catch (IOException err) {
            System.out.println("Error reading line");
        }
    }
}
	

