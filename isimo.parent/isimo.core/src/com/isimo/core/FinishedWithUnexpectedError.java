package com.isimo.core;

public class FinishedWithUnexpectedError extends RuntimeException {

	public FinishedWithUnexpectedError() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FinishedWithUnexpectedError(String pArg0, Throwable pArg1, boolean pArg2, boolean pArg3) {
		super(pArg0, pArg1, pArg2, pArg3);
		// TODO Auto-generated constructor stub
	}

	public FinishedWithUnexpectedError(String pArg0, Throwable pArg1) {
		super(pArg0, pArg1);
		// TODO Auto-generated constructor stub
	}

	public FinishedWithUnexpectedError(String pArg0) {
		super(pArg0);
		// TODO Auto-generated constructor stub
	}

	public FinishedWithUnexpectedError(Throwable pArg0) {
		super(pArg0);
		// TODO Auto-generated constructor stub
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getStackTrace()
	 * Suppress Stacktrace as it's irrelevant here
	 */
	@Override
	public StackTraceElement[] getStackTrace() {
		return new StackTraceElement[0];
	}

}
