import java.util.Stack;

/**
 * Observer to create a DOT file.
 * @author tuvialerea
 *
 */
public class GraphicalObserver implements Observer {

	private int currentLevel;
	private Stack<Integer> levelStack; // Holds the Stack
	private String data;
	
	/**
	 * Creates a new GraphicalObserver.
	 */
	public GraphicalObserver() {
		this.currentLevel = -1;
		this.levelStack = new Stack<Integer>();
		this.data = "";
		this.levelStack.push(0);
	}
	
	@Override
	public void decend() {
		this.levelStack.push(this.currentLevel);
	}

	@Override
	public void accend() {
		this.levelStack.pop();
	}

	@Override
	public void add(String item) {
		this.currentLevel++;
		this.data += "L" + this.currentLevel + " [label=\"" + item + "\",shape=box]\n";
		if (this.currentLevel != 0) {
			this.data += "L" + this.levelStack.peek() + " -> " + "L" + this.currentLevel + "\n";
		}
	}

	@Override
	public void add(Token item) {
		this.currentLevel++;
		this.data += "L" + this.currentLevel + " [label=\"" + item.getText() + "\",shape=diamond]\n";
		this.data += "L" + this.levelStack.peek() + " -> " + "L" + this.currentLevel + "\n";
	}
	
	/**
	 * The toString.
	 * @return Returns a valid DOT String representation of the Observer.
	 */
	public String toString() {
		return "strict digraph CST {\n" + this.data + "}";
	}

}
