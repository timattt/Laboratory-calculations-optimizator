/**
 * 
 */
package lang.tree.vertices;

import java.math.BigDecimal;

/**
 * @author timat
 *
 */
public abstract class LVertex {

	/**
	 * Children of this vertex
	 */
	protected LVertex[] children;

	protected final int id;
	
	public LVertex(int id, int children_count) {
		this.id = id;
		children = new LVertex[children_count];
	}

	/**
	 * @return
	 */
	public LVertex[] getChildren() {
		return children;
	}
	
	public void pushChild(LVertex v) {
		for (int i = 0; i < children.length; i++) {
			if (children[i] == null) {
				children[i] = v;
				return;
			}
		}
		
		throw new RuntimeException("No more space in vertex!");
	}
	
	public BigDecimal[] process() {
		for (LVertex v : children) {
			v.process();
		}
		return null;
	}
	
}
