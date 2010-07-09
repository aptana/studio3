package com.aptana.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;

public class Scope<T>
{
	private Scope<T> _parent;
	private List<Scope<T>> _children;
	private Map<String, List<T>> _symbols;
	private IRange _range;
	private List<T> _assignments;

	/**
	 * Scope
	 */
	public Scope()
	{
	}

	/**
	 * addAssignment
	 * 
	 * @param lhs
	 * @param rhs
	 */
	public void addAssignment(T assignment)
	{
		if (assignment != null)
		{
			if (this._assignments == null)
			{
				this._assignments = new ArrayList<T>();
			}
			
			this._assignments.add(assignment);
		}
	}
	
	/**
	 * addScope
	 * 
	 * @param scope
	 */
	public void addScope(Scope<T> scope)
	{
		if (scope != null)
		{
			scope.setParent(this);

			if (this._children == null)
			{
				this._children = new ArrayList<Scope<T>>();
			}

			this._children.add(scope);
			
			if (this.getRange().isEmpty())
			{
				this.setRange(scope.getRange());
			}
			else
			{
				this.setRange(
					new Range(
						Math.min(this.getRange().getStartingOffset(), scope.getRange().getStartingOffset()),
						Math.max(this.getRange().getEndingOffset(), scope.getRange().getEndingOffset())
					)
				);
			}
		}
	}

	/**
	 * addSymbol - Note that value can be null
	 * 
	 * @param name
	 * @param value
	 */
	public void addSymbol(String name, T value)
	{
		if (name != null && name.length() > 0)
		{
			if (this._symbols == null)
			{
				this._symbols = new HashMap<String, List<T>>();
			}

			List<T> nodes = this._symbols.get(name);

			if (nodes == null)
			{
				nodes = new ArrayList<T>();
				this._symbols.put(name, nodes);
			}

			nodes.add(value);
		}
	}

	/**
	 * getAssignments
	 * 
	 * @return
	 */
	public List<T> getAssignments()
	{
		List<T> result = this._assignments;
		
		if (result == null)
		{
			result = Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * getChildren
	 * 
	 * @return
	 */
	public List<Scope<T>> getChildren()
	{
		List<Scope<T>> result = Collections.emptyList();
		
		if (this._children != null)
		{
			result = this._children;
		}
		
		return result;
	}

	/**
	 * getLocalSymbol
	 * 
	 * @param name
	 * @return
	 */
	public List<T> getLocalSymbol(String name)
	{
		List<T> result = Collections.emptyList();
		
		if (this._symbols != null)
		{
			List<T> candidate = this._symbols.get(name);
			
			if (candidate != null)
			{
				result = candidate;
			}
		}
		
		return result;
	}
	
	/**
	 * getLocalSymbolNames
	 * 
	 * @return
	 */
	public List<String> getLocalSymbolNames()
	{
		List<String> result = Collections.emptyList();
		
		if (this._symbols != null)
		{
			result = new ArrayList<String>(this._symbols.keySet());
		}
		
		return result;
	}
	
	/**
	 * getParent
	 * 
	 * @return
	 */
	public Scope<T> getParentScope()
	{
		return this._parent;
	}
	
	/**
	 * getRange
	 * 
	 * @return
	 */
	public IRange getRange()
	{
		return (this._range != null) ? this._range : Range.EMPTY;
	}
	
	/**
	 * getScopeAtOffset
	 * 
	 * @param offset
	 * @return
	 */
	public Scope<T> getScopeAtOffset(int offset)
	{
		Scope<T> result = null;
		
		if (this.getRange().contains(offset))
		{
			result = this;
			
			for (Scope<T> child : this.getChildren())
			{
				Scope<T> candidate = child.getScopeAtOffset(offset);
				
				if (candidate != null)
				{
					result = candidate;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * getSymbol
	 * 
	 * @param name
	 * @return
	 */
	public List<T> getSymbol(String name)
	{
		List<T> result = Collections.emptyList();
		
		Scope<T> current = this;
		
		while (current != null)
		{
			if (current.hasLocalSymbol(name))
			{
				result = current.getLocalSymbol(name);
				break;
			}
			else
			{
				current = current.getParentScope();
			}
		}
		
		return result;
	}
	
	/**
	 * getSymbolNames
	 * 
	 * @return
	 */
	public List<String> getSymbolNames()
	{
		Set<String> result = new HashSet<String>();
		
		Scope<T> current = this;
		
		while (current != null)
		{
			result.addAll(this.getLocalSymbolNames());
			
			current = current.getParentScope();
		}
		
		return new ArrayList<String>(result);
	}

	/**
	 * hasLocalSymbol
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasLocalSymbol(String name)
	{
		return (this._symbols != null) ? this._symbols.containsKey(name) : false;
	}
	
	/**
	 * hasSymbol
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasSymbol(String name)
	{
		boolean result = false;
		Scope<T> current = this;
		
		while (current != null)
		{
			if (current.hasLocalSymbol(name))
			{
				result = true;
				break;
			}
			else
			{
				current = current.getParentScope();
			}
		}
		
		return result;
	}
	
	/**
	 * setParent
	 * 
	 * @param parent
	 */
	protected void setParent(Scope<T> parent)
	{
		this._parent = parent;
	}

	/**
	 * setRange
	 * 
	 * @param range
	 */
	public void setRange(IRange range)
	{
		this._range = range;
	}
}
