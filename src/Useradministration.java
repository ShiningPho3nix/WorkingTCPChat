/**
 * TODO Kommentare schreiben
 * 
 * @author Steffen Dworsky, Ram�n Schultz
 *
 */

public interface Useradministration {

	/**
	 * F�gt einen Benutzernamen mit dazugeh�rigen Passwort hinzu.
	 * 
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 */
	public void addUser(String username, char[] password);

	/**
	 * Pr�ft ob der �begebene Benutzername zusammen mit dem �bergebenen Passwort
	 * existiert.
	 * 
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 * @return true bei richtiger username-password Kombination
	 */
	public boolean checkUser(String username, char[] password);
}