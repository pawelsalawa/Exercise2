package pl.com.salsoft.exercise2;

/**
 * Interface to implement for all beans that nead initialization at application startup.
 * To actaully be initialized, a bean has to be returned from App#createBeans() method.
 */
public interface Initializable {
	/**
	 * Initializes the bean at application startup.
	 */
	void init();
}
