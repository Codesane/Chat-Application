package exceptions;

public class ServerOfflineException extends Exception {
	private static final long serialVersionUID = 8390131551652190904L;

	public ServerOfflineException(String errMsg) {
		super(errMsg);
	}
	
	public ServerOfflineException() {
		this("Server is offline.");
	}
	
	@Override
	public Throwable getCause() {
		return super.getCause();
	}
}
