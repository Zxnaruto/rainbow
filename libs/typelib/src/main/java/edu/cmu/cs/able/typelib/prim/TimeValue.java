package edu.cmu.cs.able.typelib.prim;

import incubator.pval.Ensure;

/**
 * Data value representing a time value.
 */
public class TimeValue extends JavaObjectDataValue<Long> {
	/**
	 * Creates a new time value.
	 * @param value the number of microseconds since the epoch
	 * @param type the type
	 */
	protected TimeValue(long value, TimeType type) {
		super(value, type);
		Ensure.is_true(value >= 0, "value < 0");
	}
	
	@Override
	public TimeValue clone() throws CloneNotSupportedException {
		return new TimeValue(value(), (TimeType) type());
	}
}
