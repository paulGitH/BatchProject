package batch;

public class BatchException extends Exception {
	public BatchException(String excep){
		super(excep);
	}
	public BatchException(String excep, Throwable other){
		super(excep, other);
	}
}
