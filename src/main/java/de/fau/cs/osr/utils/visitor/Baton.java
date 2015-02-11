/**
 * Copyright 2011 The Open Source Research Group,
 *                University of Erlangen-Nürnberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fau.cs.osr.utils.visitor;

/**
 * All of this object's public API methods must be called as the very last
 * statement of a {@code visit} method, best as part of the return statement.
 */
public final class Baton
{
	static final int CODE_QUERIED = -2;
	
	static final int JUST_CREATED = -1;
	
	/** A value greater than this value indicates that a code has been set. */
	static final int CODE_SET_THRESHOLD = 0;
	
	static final int CONTINUE_SAME_TYPE_OR_REDISPATCH = 1;
	
	static final int CONTINUE_ASSIGNABLE_TYPE_OR_REDISPATCH = 2;
	
	static final int CONTINUE_SAME_REF = 3;
	
	static final int CONTINUE_SAME_TYPE = 4;
	
	static final int CONTINUE_ASSIGNABLE_TYPE = 5;
	
	static final int SKIP = 6;
	
	// =========================================================================
	
	private int code = JUST_CREATED;
	
	// =========================================================================
	
	Baton()
	{
		
	}
	
	// =========================================================================
	
	/**
	 * The code must be set once before it can be queries again!
	 */
	final int queryAndResetCode()
	{
		checkCodeSet();
		int code = this.code;
		this.code = CODE_QUERIED;
		return code;
	}
	
	protected final void checkCodeSet()
	{
		if (code <= CODE_SET_THRESHOLD)
			throw new IllegalStateException("No code set: " + code);
	}
	
	// =========================================================================
	
	/**
	 * Tell the controller to continue processing of the current object if the
	 * following constraints are satisfied: If the returned object <strong>has
	 * the <em>exact same type</em> as the object that was passed to the
	 * {@code visit} method</strong> it will be passed on to the next
	 * {@code visit} method in the chain of visitors applicable to this object
	 * type. <strong>If the returned object does not fulfill this constraint but
	 * is not {@code null}</strong> it will be re-dispatched. If the returned
	 * object is {@code null} the object passed to the {@code visit} method will
	 * not be further processed.
	 */
	public final <T> T continueIfSameTypeOrRedispatch(T returnValue)
	{
		setCode(CONTINUE_SAME_TYPE_OR_REDISPATCH);
		return returnValue;
	}
	
	/**
	 * Tell the controller to continue processing of the current object if the
	 * following constraints are satisfied: If the returned object <strong>is
	 * <em>assignable</em> to the type of the object that was passed to the
	 * {@code visit} method</strong> it will be passed on to the next
	 * {@code visit} method in the chain of visitors applicable to this object
	 * type. <strong>If the returned object does not fulfill this constraint but
	 * is not {@code null}</strong> it will be re-dispatched. If the returned
	 * object is {@code null} the object passed to the {@code visit} method will
	 * not be further processed.
	 * 
	 * <strong>Attention:</strong> If the return value is assignable but is not
	 * of the same type as the object passed to the {@code visit} method, the
	 * {@code visit} method to which the return value is passed on <strong>may
	 * not be the most specific visit method available</strong>. This is the
	 * case if another, later visitor offers a more specific method for the
	 * returned object's type. Since no re-dispatch takes place, the controller
	 * keeps using the visit chain for the original object's type.
	 */
	public final <T> T continueIfAssignableTypeOrRedispatch(T returnValue)
	{
		setCode(CONTINUE_ASSIGNABLE_TYPE_OR_REDISPATCH);
		return returnValue;
	}
	
	/**
	 * Tell the controller to continue processing of the current object if the
	 * following constraints are satisfied: If the returned object <strong>is
	 * the same object (by reference) that was passed to the {@code visit}
	 * method </strong> it will be passed on to the next {@code visit} method in
	 * the chain of visitors applicable to this object type. <strong>If the
	 * returned object does not fulfill this constraint or is {@code null}
	 * </strong> the object passed to the {@code visit} method will not be
	 * further processed.
	 */
	public final <T> T continueIfSameRef(T returnValue)
	{
		setCode(CONTINUE_SAME_REF);
		return returnValue;
	}
	
	/**
	 * Tell the controller to continue processing of the current object if the
	 * following constraints are satisfied: If the returned object <strong>has
	 * the <em>exact same type</em> as the object that was passed to the
	 * {@code visit} method</strong> it will be passed on to the next
	 * {@code visit} method in the chain of visitors applicable to this object
	 * type. <strong>If the returned object does not fulfill this constraint or
	 * is {@code null}</strong> the object passed to the {@code visit} method
	 * will not be further processed.
	 */
	public final <T> T continueIfSameType(T returnValue)
	{
		setCode(CONTINUE_SAME_TYPE);
		return returnValue;
	}
	
	/**
	 * Tell the controller to continue processing of the current object if the
	 * following constraints are satisfied: If the returned object <strong>is
	 * <em>assignable</em> to the type of the object that was passed to the
	 * {@code visit} method</strong> it will be passed on to the next
	 * {@code visit} method in the chain of visitors applicable to this object
	 * type. <strong>If the returned object does not fulfill this constraint or
	 * is {@code null}</strong> the object passed to the {@code visit} method
	 * will not be further processed.
	 * 
	 * <strong>Attention:</strong> If the return value is assignable but is not
	 * of the same type as the object passed to the {@code visit} method, the
	 * {@code visit} method to which the return value is passed on <strong>may
	 * not be the most specific visit method available</strong>. This is the
	 * case if another, later visitor offers a more specific method for the
	 * returned object's type. Since no re-dispatch takes place, the controller
	 * keeps using the visit chain for the original object's type.
	 */
	public final <T> T continueIfAssignableType(T returnValue)
	{
		setCode(CONTINUE_ASSIGNABLE_TYPE);
		return returnValue;
	}
	
	/**
	 * Skip further processing of the object passed to this visit method and
	 * just return the specified return value.
	 */
	public final <T> T skip(T returnValue)
	{
		setCode(SKIP);
		return returnValue;
	}
	
	protected final void setCode(int setCode)
	{
		if (this.code >= CODE_SET_THRESHOLD)
			throw new IllegalStateException("No code set: " + this.code);
		this.code = setCode;
	}
}
